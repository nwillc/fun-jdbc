/*
 * Copyright (c) 2015, nwillc@gmail.com
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

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class DbAccessorTest {
    private InMemWordsDatabase dao;
    private final static Extractor<String> wordExtractor = rs -> rs.getString("WORD");

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
        Stream<String> words = dao.dbQuery(wordExtractor, "SELECT * FROM WORDS");
        assertThat(words).isNotNull();
        assertThat(words.count()).isEqualTo(3);
    }

    @Test
    public void testQueryWithArgs() throws Exception {
        Stream<String> words = dao.dbQuery(wordExtractor, "SELECT * FROM WORDS WHERE WORD = '%s'", "a");
        assertThat(words).isNotNull();
        assertThat(words.count()).isEqualTo(2);
    }

    @Test
    public void testFind() throws Exception {
        Optional<String> word = dao.dbFind(wordExtractor, "SELECT * FROM WORDS WHERE WORD = 'b'");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get()).isEqualTo("b");
    }

    @Test
    public void testFindWithArgs() throws Exception {
        Optional<String> word = dao.dbFind(wordExtractor, "SELECT * FROM WORDS WHERE WORD = '%s'", "b");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get()).isEqualTo("b");
    }


    @Test
    public void testNotFound() throws Exception {
        Optional<String> word = dao.dbFind(wordExtractor, "SELECT * FROM WORDS WHERE WORD = 'c'");
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isFalse();
    }

    @Test
    public void testStream() throws Exception {
        final String sql = "SELECT * FROM WORDS";

        try (Stream<String> stream = dao.dbQuery(wordExtractor, sql)) {
            assertThat(stream.count()).isEqualTo(3);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        final String sql = "UPDATE WORDS set WORD = 'c' WHERE WORD = 'a'";
        dao.dbUpdate(sql);
        Stream<String> words = dao.dbQuery(wordExtractor, "SELECT * FROM WORDS WHERE WORD = '%s'", "c");
        assertThat(words).isNotNull();
        assertThat(words.count()).isEqualTo(2);
    }

    @Test
    public void testName() throws Exception {
        dao.dbQuery(rs -> new Pair(rs.getString("WORD"), 0), "SELECT * FROM WORDS").forEach(p -> System.out.println(p.word + ": " + p.count));

    }

    @Test(expected = SQLException.class)
    public void testFindFails() throws Exception {
        dao.dbFind(wordExtractor, "SELECT * FROM WORDS WHERE WORD = 'a'");
    }

    private class Pair {
        final String word;
        final int count;

        private Pair(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }
}
