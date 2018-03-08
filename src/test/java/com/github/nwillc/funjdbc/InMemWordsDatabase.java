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

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.github.nwillc.funjdbc.SqlStatement.sql;

public class InMemWordsDatabase implements DbAccessor {
    private final static String DRIVER = "org.h2.Driver";
    private final static String URL = "jdbc:h2:mem:";
    private static long instanceId = 0;
    private final DataSource dataSource;

    public InMemWordsDatabase() throws ClassNotFoundException {
        Class.forName(DRIVER);
        String name = String.format("db%04d", instanceId++);
        dataSource = setupDataSource(URL + name);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void create() throws SQLException {
        dbExecute(sql("CREATE TABLE WORDS ( WORD CHAR(20) )"));
        dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('a')"));
        dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('a')"));
        dbExecute(sql("INSERT INTO WORDS (WORD) VALUES ('b')"));
        dbExecute(sql("CREATE TABLE KEYED(ID BIGINT AUTO_INCREMENT, WORD CHAR(20))"));
    }

    public void drop() throws SQLException {
        dbExecute(sql("DROP TABLE WORDS"));
    }

    private static DataSource setupDataSource(String connectURI) {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }

}
