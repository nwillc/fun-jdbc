/*
 * Copyright (c) 2017, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package com.github.nwillc.funjdbc;

import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.utils.EFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

import static com.github.nwillc.funjdbc.SqlStatement.sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Parameterized.class)
public class DbAccessorTest {
    private InMemWordsDatabase dao;
    private Extractor<Word> wordExtractor;
    private final static Extractor<WordCount> WORD_COUNT_EXTRACTOR = rs -> new WordCount(rs.getString(1));


    public DbAccessorTest(Extractor<Word> extractor) {
        wordExtractor = extractor;
    }

    @SuppressWarnings("unchecked")
    @Parameterized.Parameters
    public static Collection getExtractors() {
        List<Extractor<Word>[]> extractors = new ArrayList<>();

        extractors.add(new Extractor[]{rs -> new Word(rs.getString(1))});
        final Extractor<Word> extractor = new EFactory<Word>().factory(Word::new)
                .add(Word::setWord, ResultSet::getString, 1)
                .getExtractor();
        extractors.add(new Extractor[]{extractor});
        return extractors;
    }

    @Before
    public void setUp() throws Exception {
        dao = new InMemWordsDatabase();
        dao.create();
    }

    @Test
    public void testGetConnection() throws Exception {
        assertThat(dao.getConnection()).isNotNull();
    }

    @Test
    public void testQuery() throws Exception {
        Stream<Word> words = dao.dbQuery(wordExtractor, sql("SELECT * FROM WORDS"));
        assertThat(words.count()).isEqualTo(3);
    }

    @Test
    public void testQueryWithArgs() throws Exception {
        Stream<Word> words = dao.dbQuery(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = '%s'", "a"));
        assertThat(words.count()).isEqualTo(2);
    }

    @Test
    public void testFind() throws Exception {
        Optional<Word> word = dao.dbFind(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = 'b'"));
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get().word).isEqualTo("b");
    }

    @Test
    public void testFindWithArgs() throws Exception {
        Optional<Word> word = dao.dbFind(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = '%s'", "b"));
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get().word).isEqualTo("b");
    }


    @Test
    public void testNotFound() throws Exception {
        Optional<Word> word = dao.dbFind(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = 'c'"));
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isFalse();
    }

    @Test
    public void testStream() throws Exception {
        try (Stream<Word> stream = dao.dbQuery(wordExtractor, sql("SELECT * FROM WORDS"))) {
            assertThat(stream.count()).isEqualTo(3);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        final int count = 2;
        final SqlStatement sql = sql("UPDATE WORDS set WORD = 'c' WHERE WORD = 'a'");
        final int dbUpdated = dao.dbUpdate(sql);
        assertThat(dbUpdated).isEqualTo(count);
        Stream<Word> words = dao.dbQuery(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = '%s'", "c"));
        assertThat(words.count()).isEqualTo(count);
    }

    @Test
    public void testUpdateWithException() throws Exception {
        final int count = 2;
        final SqlStatement sql = sql("blah blah");
        assertThatThrownBy(() -> dao.dbUpdate(sql)).isInstanceOf(SQLException.class);
    }


    @Test(expected = SQLException.class)
    public void testFindFails() throws Exception {
        dao.dbFind(wordExtractor, sql("SELECT * FROM WORDS WHERE WORD = 'a'"));
    }

    @Test
    public void shouldDbEnrich() throws Exception {
        Map<String, WordCount> counts = new HashMap<>();

        dao.dbQuery(WORD_COUNT_EXTRACTOR, sql("SELECT DISTINCT WORD FROM WORDS")).forEach(c -> counts.put(c.word, c));

        assertThat(counts.size()).isEqualTo(2);
        dao.dbEnrich(counts,
                rs -> rs.getString(1), (e, rs) -> e.count = rs.getInt(2),
                sql("SELECT WORD, COUNT(*) FROM WORDS GROUP BY WORD"));
        assertThat(counts.get("b").count).isEqualTo(1);
        assertThat(counts.get("a").count).isEqualTo(2);
    }

    @Test
    public void shouldDbEnrichNoKey() throws Exception {
        Map<String, WordCount> counts = new HashMap<>();

        dao.dbQuery(WORD_COUNT_EXTRACTOR, sql("SELECT DISTINCT WORD FROM WORDS")).forEach(c -> counts.put(c.word, c));

        assertThat(counts.size()).isEqualTo(2);
        dao.dbEnrich(counts,
                rs -> rs.getString(1), (e, rs) -> e.count = rs.getInt(2),
                sql("SELECT COUNT(*) FROM WORDS"));
        assertThat(counts.size()).isEqualTo(2);
        counts.values().forEach(wordCount -> assertThat(wordCount.count).isEqualTo(0));
    }

    @Test
    public void shouldDbExecute() throws Exception {
        final SqlStatement sqlStatement = sql("SELECT 1");
        assertThat(dao.dbExecute(sqlStatement)).isTrue();
    }

    @Test
    public void shouldDbEnrichNoValue() throws Exception {
        Map<String, WordCount> counts = new HashMap<>();

        dao.dbEnrich(counts,
                rs -> rs.getString(1), (e, rs) -> e.count = rs.getInt(2),
                sql("SELECT WORD, COUNT(*) FROM WORDS GROUP BY WORD"));
        assertThat(counts).hasSize(0);
    }

    @Test
    public void testAutoclose() throws Exception {
        try (Connection connection = dao.getConnection();
             Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT WORD FROM WORDS");
            final Stream<WordCount> stream = dao.stream(WORD_COUNT_EXTRACTOR, resultSet);
            assertThat(resultSet.isClosed()).isFalse();
            stream.close();
            assertThat(resultSet.isClosed()).isTrue();
        }
    }

    private static class Word {
        String word;

        Word() {
        }

        Word(String word) {
            this.word = word;
        }

        void setWord(String word) {
            this.word = word;
        }
    }

    private static class WordCount {
        final String word;
        int count;

        private WordCount(String word) {
            this.word = word;
            this.count = 0;
        }
    }
}
