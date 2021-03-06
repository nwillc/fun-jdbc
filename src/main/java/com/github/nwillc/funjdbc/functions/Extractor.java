/*
 * Copyright (c) 2018, nwillc@gmail.com
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

package com.github.nwillc.funjdbc.functions;

import com.github.nwillc.funjdbc.utils.EFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A functional interface designed to extract a given type from a single row of a ResultSet.
 *
 * @param <T> type to extract
 */
@FunctionalInterface
public interface Extractor<T> {
    /**
     * Extract type T from the current position in the ResultSet.
     *
     * @param rs the ResultSet to extract from
     * @return the type T extracted
     * @throws SQLException should the extraction fail
     * @see EFactory
     */
    T extract(ResultSet rs) throws SQLException;
}
