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

import org.junit.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class UncheckedSQLExceptionTest {

    @Test
    public void testConstructorForGeneralThrowable() throws Exception {
        final String msg = "foo";
        final NullPointerException nullPointerException = new NullPointerException();

        final UncheckedSQLException uncheckedSQLException = new UncheckedSQLException(msg, nullPointerException);

        assertThat(uncheckedSQLException.getMessage()).isEqualTo(msg);
        assertThat(uncheckedSQLException.getCause()).isEqualTo(nullPointerException);
        assertThat(uncheckedSQLException.getErrorCode().isPresent()).isFalse();
        assertThat(uncheckedSQLException.getSqlState().isPresent()).isFalse();
    }

    @Test
    public void testConstructorForSQLException() throws Exception {
        final String msg = "foo";
        final String sqlState = "bar";
        final int errorCode = 42;
        final SQLException sqlException = new SQLException(msg, sqlState, errorCode);

        final UncheckedSQLException uncheckedSQLException = new UncheckedSQLException(msg, sqlException);

        assertThat(uncheckedSQLException.getMessage()).isEqualTo(msg);
        assertThat(uncheckedSQLException.getCause()).isEqualTo(sqlException);
        assertThat(uncheckedSQLException.getErrorCode().isPresent()).isTrue();
        assertThat(uncheckedSQLException.getErrorCode().get()).isEqualTo(errorCode);
        assertThat(uncheckedSQLException.getSqlState().isPresent()).isTrue();
        assertThat(uncheckedSQLException.getSqlState().get()).isEqualTo(sqlState);
    }

    @Test
    public void testWhereCauseIsUncheckedSqlException() throws Exception {

        final String msg = "foo";
        final String sqlState = "bar";
        final int errorCode = 42;
        final SQLException sqlException = new SQLException(msg, sqlState, errorCode);

        final UncheckedSQLException innerException = new UncheckedSQLException("Inner message", sqlException);
        final UncheckedSQLException uncheckedSQLException = new UncheckedSQLException(msg, innerException);

        assertThat(uncheckedSQLException.getMessage()).isEqualTo(msg);
        assertThat(uncheckedSQLException.getCause()).isEqualTo(innerException);
        assertThat(uncheckedSQLException.getErrorCode().isPresent()).isTrue();
        assertThat(uncheckedSQLException.getErrorCode().get()).isEqualTo(errorCode);
        assertThat(uncheckedSQLException.getSqlState().isPresent()).isTrue();
        assertThat(uncheckedSQLException.getSqlState().get()).isEqualTo(sqlState);
    }

    @Test
    public void testWithoutCause() throws Exception {
        final UncheckedSQLException uncheckedSQLException = new UncheckedSQLException("Test", null);

        assertThat(uncheckedSQLException).isNotNull();
        assertThat(uncheckedSQLException.getErrorCode().isPresent()).isFalse();
        assertThat(uncheckedSQLException.getSqlState().isPresent()).isFalse();
    }
}
