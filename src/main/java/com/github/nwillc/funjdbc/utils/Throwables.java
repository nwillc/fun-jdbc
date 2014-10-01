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

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.UncheckedSQLException;

import java.sql.SQLException;

public final class Throwables {
    private Throwables() {}

    /**
     * Propagate a Throwable as a RuntimeException. The Runtime exception bases it's message not the message of the Throwable,
     * and the Throwable is set as it's cause. This can be used to deal with exceptions in lambdas etc.
     * @param throwable the throwable to repropagate.
     * @return a RuntimeException
     */
    public static RuntimeException propagate(final Throwable throwable) {
        if (throwable instanceof SQLException) {
            return new UncheckedSQLException("Repropagated " + throwable.getMessage(), throwable);  //NOPMD
        }
        return new RuntimeException("Repropagated " + throwable.getMessage(), throwable);  //NOPMD
    }
}
