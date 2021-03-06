/*
 * Copyright 2018 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.funjdbc;

import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.nwillc.funjdbc.SqlStatement.sql;

public class TestDbInitialization implements DbAccessor, InitializationPlugin {
    private Connection connection;

    @Override
    public void connectionMade(String name, Connection connection) {
        this.connection = connection;
        try {
            dbExecute(sql("CREATE TABLE WORDS ( WORD CHAR(20) )"));
            dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('a')"));
            dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('a')"));
            dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('b')"));
            dbExecute(sql("CREATE TABLE KEYED(ID BIGINT AUTO_INCREMENT, WORD CHAR(20))"));
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
