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


import org.pmw.tinylog.Logger;

/**
 * A SQL statement comprised of a template SQL string, which is a {@link java.util.Formatter} string, and
 * the arguments to pass to it.
 *
 * @since 0.9.0
 */
public class SqlStatement {
    private final String sql;
    private Object[] args;

    public SqlStatement(String sql, Object... args) {
        this.sql = sql;
        setArgs(args);
    }

    public void setArgs(Object... args) {
        this.args = args;
    }

    @Override
    public String toString() {
        final String formatted = (args == null || args.length == 0) ?  sql : String.format(sql, args);
        Logger.debug("Formatted SQL: {}", formatted);
        return formatted;
    }
}
