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

import com.github.nwillc.contracts.IteratorContract;
import com.github.nwillc.funjdbc.utils.Closer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.nwillc.funjdbc.utils.Closer.close;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ResultSetIteratorTest extends IteratorContract {
    private InMemWordsDatabase dao;
    private final static Extractor<String> wordExtractor = rs -> rs.getString("WORD");
    private List<ResultSetIterator<String>> iterators;

    @Before
    public void setUp() throws Exception {
        dao = new InMemWordsDatabase();
        dao.create();
        iterators = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        iterators.stream().forEach(Closer::close);
        iterators = null;
    }

    @Override
    protected Iterator getNonEmptyIterator() {
        try {
            Connection connection = dao.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM WORDS");
            ResultSetIterator<String> iterator = new ResultSetIterator<>(resultSet, wordExtractor).onClose(() -> {
                close(statement);
                close(connection);
            });
            iterators.add(iterator);
            return iterator;
        } catch (SQLException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidConstructorArgs() throws Exception {
        try {
            new ResultSetIterator(null, wordExtractor);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {}

        try (Connection connection = dao.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM WORDS")) {
            new ResultSetIterator(resultSet, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void testOnClose() throws Exception {
        final AtomicBoolean tattleTale = new AtomicBoolean(false);

        try (Connection connection = dao.getConnection();
             Statement statement = connection.createStatement()) {
             ResultSet resultSet = statement.executeQuery("SELECT * FROM WORDS");
             ResultSetIterator resultSetIterator = new ResultSetIterator(resultSet, wordExtractor).onClose(() -> {
                 tattleTale.set(true);
             });
            resultSetIterator.close();
        }
        assertThat(tattleTale.get()).isTrue();
    }
}