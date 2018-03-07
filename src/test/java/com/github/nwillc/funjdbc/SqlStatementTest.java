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

import org.junit.Test;

import static com.github.nwillc.funjdbc.SqlStatement.sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class SqlStatementTest {
    public static final String SELECT_1 = "SELECT 1";

    @Test
    public void testSetArgs() throws Exception {
        final String sql = "SELECT * FROM FOO WHERE x = '%s'";
        final String x1 = "foo";
        final String x2 = "bar";
        final SqlStatement sqlStatement = new SqlStatement(sql, x1);
        assertThat(sqlStatement.toString()).isEqualTo(String.format(sql, x1));
        sqlStatement.setArgs(x2);
        assertThat(sqlStatement.toString()).isEqualTo(String.format(sql, x2));
    }

    @Test
    public void testToStringNoArgs() throws Exception {
        final String sql = "SELECT * FROM FOO";
        final SqlStatement sqlStatement = new SqlStatement(sql);

        assertThat(sqlStatement.toString()).isEqualTo(sql);
    }

    @Test
    public void testToStringNoArgsArray() throws Exception {
        final String sql = "SELECT * FROM FOO";
        final SqlStatement sqlStatement = new SqlStatement(sql, new Object[0]);

        assertThat(sqlStatement.toString()).isEqualTo(sql);
    }

    @Test
    public void testToStringNullArgs() throws Exception {
        final String sql = "SELECT * FROM FOO";
        final SqlStatement sqlStatement = new SqlStatement(sql, null);

        assertThat(sqlStatement.toString()).isEqualTo(sql);
    }

    @Test
    public void testToStringArgs() throws Exception {
        final String sql = "SELECT * FROM FOO WHERE x = '%s' AND y = %d";
        final String x = "foo";
        final int y = 10;
        final SqlStatement sqlStatement = new SqlStatement(sql, x, y);

        assertThat(sqlStatement.toString()).isEqualTo(String.format(sql, x, y));
    }

    @Test
    public void testSqlStatic() {
        final SqlStatement sql = sql(SELECT_1);
        assertThat(sql).isInstanceOf(SqlStatement.class);
        assertThat(sql.toString()).isEqualTo(SELECT_1);
    }
}