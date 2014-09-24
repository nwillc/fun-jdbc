/*
 * Copyright (c) 2014,  nwillc@gmail.com
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DbAccessorTest {
    private InMemWordsDatabase dao;
    private final static Extractor<String> wordExtractor = rs -> rs.getString("WORD");

    @Before
    public void setUp() throws Exception {
        dao = new InMemWordsDatabase();
        dao.create();
    }

    @After
    public void tearDown() throws Exception {
        dao.drop();
        dao = null;
    }

    @Test
    public void testGetConnection() throws Exception {
        assertThat(dao.getConnection()).isNotNull();
    }

    @Test
    public void testQuery() throws Exception {
        List<String> words = dao.query("SELECT * FROM WORDS", wordExtractor);
        assertThat(words).isNotNull();
        assertThat(words.size()).isEqualTo(3);
    }

    @Test
    public void testFind() throws Exception {
        Optional<String> word = dao.find("SELECT * FROM WORDS WHERE WORD = 'b'", wordExtractor);
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isTrue();
        assertThat(word.get()).isEqualTo("b");
    }

    @Test
    public void testNotFound() throws Exception {
        Optional<String> word = dao.find("SELECT * FROM WORDS WHERE WORD = 'c'", wordExtractor);
        assertThat(word).isNotNull();
        assertThat(word.isPresent()).isFalse();
    }

    @Test(expected = SQLException.class)
    public void testFindFails() throws Exception {
        dao.find("SELECT * FROM WORDS WHERE WORD = 'a'", wordExtractor);
    }

}
