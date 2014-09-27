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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import static com.github.nwillc.funjdbc.Utils.Throwables.propagate;

/**
 * This is an Iterator that traverses a ResultSet, additionally it is AutoCloseable so that it can clean its resources up.
 * @param <T> The type of elements being extracted
 */
public class ResultSetIterator<T> implements Iterator<T> {
    private final ResultSet resultSet;
    private final Extractor<T> extractor;

    public ResultSetIterator(final ResultSet resultSet, final Extractor<T> extractor) {
        this.extractor = extractor;
        this.resultSet = resultSet;
    }

    @Override
    public boolean hasNext() {
        try {
            return resultSet.next();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    @Override
    public T next() {
        try {
            return extractor.extract(resultSet);
        } catch (SQLException e) {
            throw propagate(e);
        }
    }
}
