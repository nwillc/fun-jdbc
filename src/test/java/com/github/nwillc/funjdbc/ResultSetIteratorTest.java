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

import com.github.nwillc.contracts.IteratorContract;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.utils.Closer;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

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

@RunWith(JMockit.class)
public class ResultSetIteratorTest extends IteratorContract implements DbAccessor {
    private final static Extractor<String> wordExtractor = rs -> rs.getString("WORD");
    private List<ResultSetIterator<String>> iterators;

    @Rule
    public final EmbeddedDatabaseRule embeddedDb = EmbeddedDatabaseRule
            .builder()
            .initializedByPlugin(new TestDbInitialization())
            .build();

    @Mocked
    ResultSet mockResultSet;
    @Mocked
    Extractor mockExtractor;

    @Before
    public void setUp() throws Exception {
        iterators = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        iterators.forEach(Closer::close);
        iterators = null;
    }

    @Override
    protected Iterator getNonEmptyIterator() {
        try {
            Connection connection = getConnection();
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
        } catch (NullPointerException ignored) {
        }

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM WORDS")) {
            new ResultSetIterator(resultSet, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (NullPointerException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExtractorSQLException() throws Exception {
        new Expectations() {{
            mockResultSet.next();
            result = true;
            mockExtractor.extract((ResultSet) any);
            result = new SQLException();
        }};

        ResultSetIterator resultSetIterator = new ResultSetIterator(mockResultSet, mockExtractor);
        try {
            resultSetIterator.next();
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleResultSetExceptions() throws Exception {
        new Expectations() {{
            mockResultSet.next();
            result = new SQLException();
        }};

        ResultSetIterator resultSetIterator = new ResultSetIterator(mockResultSet, wordExtractor);

        try {
            resultSetIterator.hasNext();
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException ignored) {
        }
    }

    @Test
    public void testOnClose() throws Exception {
        final AtomicBoolean tattleTale = new AtomicBoolean(false);
        ResultSet resultSet;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM WORDS");
            @SuppressWarnings("unchecked") ResultSetIterator resultSetIterator = new ResultSetIterator(resultSet, wordExtractor).onClose(() -> tattleTale.set(true));
            resultSetIterator.close();
            assertThat(tattleTale.get()).isTrue();
            assertThat(resultSet.isClosed()).isTrue();
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        return embeddedDb.getConnection();
    }
}
