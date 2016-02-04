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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InMemWordsDatabase implements DbAccessor {
    private final static String DRIVER = "org.h2.Driver";
    private final static String URL = "jdbc:h2:mem:";
    private static long instanceId = 0;
    private final String name;
    private final Connection connection;

    public InMemWordsDatabase() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        name = String.format("db%04d", instanceId++);
        connection = getConnection();
    }

    @Override
    public boolean logSql() {
        return true;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + name);
    }

    public void create() throws SQLException {
        try (Connection c = getConnection();
             Statement statement = c.createStatement()) {
            statement.execute("CREATE TABLE WORDS ( WORD CHAR(20) )");
            statement.execute("INSERT INTO WORDS (WORD) VALUES ('a')");
            statement.execute("INSERT INTO WORDS (WORD) VALUES ('a')");
            statement.execute("INSERT INTO WORDS (WORD) VALUES ('b')");
        }
    }

    public void drop() throws SQLException {
        try (Connection c = getConnection();
             Statement statement = c.createStatement()) {
            statement.execute("DROP TABLE WORDS");
            connection.close();
        }
    }
}
