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

import java.sql.SQLException;
import java.util.Optional;

/**
 * An unchecked version of SQLException. Maintains the errorCode and sqlState of the SQLException.
 */
public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public Optional<Integer> getErrorCode() {
        final Throwable cause = getCause();
        if (cause != null) {
            if (cause instanceof SQLException) {
                return Optional.of(((SQLException) cause).getErrorCode());
            }
            if (cause instanceof UncheckedSQLException) {
                return ((UncheckedSQLException) cause).getErrorCode();
            }
        }

        return Optional.empty();
    }

    public Optional<String> getSqlState() {
        final Throwable cause = getCause();
        if (cause != null) {
            if (cause instanceof SQLException) {
                return Optional.of(((SQLException) cause).getSQLState());
            }
            if (cause instanceof UncheckedSQLException) {
                return ((UncheckedSQLException) cause).getSqlState();
            }
        }

        return Optional.empty();
    }
}
