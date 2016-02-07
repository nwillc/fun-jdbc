/*
 * Copyright (c) 2016, nwillc@gmail.com
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
 */

package com.github.nwillc.funjdbc;

import com.github.nwillc.funjdbc.functions.Extractor;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class DbAccessorTest {
    private InMemWordsDatabase dao;
    private final static Extractor<String> WORD_EXTRACTOR = rs -> rs.getString(1);
	private final static Extractor<WordCount> WORD_COUNT_EXTRACTOR = rs -> new WordCount(rs.getString(1));

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
        Stream<String> words = dao.dbQuery(WORD_EXTRACTOR, "SELECT * FROM WORDS");
        assertThat(words.count()).isEqualTo(3);
    }

    @Test
    public void testQueryWithArgs() throws Exception {
        Stream<String> words = dao.dbQuery(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = '%s'", "a");
        assertThat(words.count()).isEqualTo(2);
    }

    @Test
    public void testFind() throws Exception {
        Optional<String> word = dao.dbFind(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = 'b'");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get()).isEqualTo("b");
    }

    @Test
    public void testFindWithArgs() throws Exception {
        Optional<String> word = dao.dbFind(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = '%s'", "b");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get()).isEqualTo("b");
    }


    @Test
    public void testNotFound() throws Exception {
        Optional<String> word = dao.dbFind(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = 'c'");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isFalse();
    }

    @Test
    public void testStream() throws Exception {
        final String sql = "SELECT * FROM WORDS";

        try (Stream<String> stream = dao.dbQuery(WORD_EXTRACTOR, sql)) {
            assertThat(stream.count()).isEqualTo(3);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        final String sql = "UPDATE WORDS set WORD = 'c' WHERE WORD = 'a'";
        dao.dbUpdate(sql);
        Stream<String> words = dao.dbQuery(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = '%s'", "c");
        assertThat(words.count()).isEqualTo(2);
    }

    @Test(expected = SQLException.class)
    public void testFindFails() throws Exception {
        dao.dbFind(WORD_EXTRACTOR, "SELECT * FROM WORDS WHERE WORD = 'a'");
    }

    @Test
    public void shouldLogSql() throws Exception {
		assertThat(dao.logSql()).isTrue();
    }

    @Test
    public void shouldDbEnrich() throws Exception {
		Map<String,WordCount> counts = new HashMap<>();

		dao.dbQuery(WORD_COUNT_EXTRACTOR, "SELECT DISTINCT WORD FROM WORDS").forEach(c -> counts.put(c.word, c));

		assertThat(counts.size()).isEqualTo(2);
		dao.dbEnrich(counts,
				rs -> rs.getString(1), (e,rs) -> e.count = rs.getInt(2),
				"SELECT WORD, COUNT(*) FROM WORDS GROUP BY WORD");
		assertThat(counts.get("b").count).isEqualTo(1);
		assertThat(counts.get("a").count).isEqualTo(2);
    }

    private static class WordCount {
        final String word;
		int count;

        private WordCount(String word) {
            this.word = word;
            this.count = 1;
        }
	}
}
