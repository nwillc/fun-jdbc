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

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.contracts.UtilityClassContract;
import com.github.nwillc.funjdbc.UncheckedSQLException;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

import static com.github.nwillc.funjdbc.utils.Throwables.propagate;
import static org.assertj.core.api.Assertions.assertThat;

public class ThrowablesTest extends UtilityClassContract {

    @Override
    public Class<?> getClassToTest() {
        return Throwables.class;
    }

    @Test
    public void testHandlesRuntimeException() throws Exception {
        final ArithmeticException arithmeticException = new ArithmeticException();

        final RuntimeException runtimeException = propagate(arithmeticException);
        assertThat(runtimeException).isEqualTo(arithmeticException);
    }

    @Test
    public void testWrapsNormalException() throws Exception {
        final DataFormatException dataFormatException = new DataFormatException();

        final RuntimeException runtimeException = propagate(dataFormatException);
        assertThat(runtimeException).isNotNull();
        assertThat(RuntimeException.class).isAssignableFrom(runtimeException.getClass());
        assertThat(runtimeException.getCause()).isEqualTo(dataFormatException);
    }

    @Test
    public void testHandlesIOException() throws Exception {
        final IOException ioException = new IOException();

        final RuntimeException runtimeException = propagate(ioException);
        assertThat(runtimeException).isNotNull();
        assertThat(runtimeException).isInstanceOf(UncheckedIOException.class);
        assertThat(runtimeException.getCause()).isEqualTo(ioException);
    }

    @Test
    public void testHandlesSQLException() throws Exception {
        final String msg = "foo";
        final String sqlState = "bar";
        final int errorCode = 42;
        final SQLException sqlException = new SQLException(msg, sqlState, errorCode);

        final RuntimeException runtimeException = propagate(sqlException);
        assertThat(runtimeException).isNotNull();
        assertThat(runtimeException).isInstanceOf(UncheckedSQLException.class);
        assertThat(runtimeException.getCause()).isEqualTo(sqlException);
    }
}
