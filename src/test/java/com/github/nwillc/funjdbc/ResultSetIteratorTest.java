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
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import static com.github.nwillc.funjdbc.utils.Closer.close;

public class ResultSetIteratorTest extends IteratorContract {
    private InMemWordsDatabase dao;
    private final static Extractor<String> wordExtractor = rs -> rs.getString("WORD");
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        dao = new InMemWordsDatabase();
        dao.create();
    }

    @After
    public void tearDown() throws Exception {
        close(resultSet);
    }

    @Override
    protected Iterator getNonEmptyIterator() {
        close(resultSet);
        try {
            Connection connection = dao.getConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM WORDS");
            return new ResultSetIterator<>(resultSet, wordExtractor).onClose(() -> {
                close(statement);
                close(connection);
            });
        } catch (SQLException e) {
            return null;
        }
    }
}
