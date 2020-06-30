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

package com.github.nwillc.funjdbc;

import com.github.nwillc.funjdbc.functions.Extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.github.nwillc.funjdbc.utils.Throwables.propagate;

/**
 * This is an Iterator that traverses a ResultSet returning elements via an Extractor. Additionally this
 * implements AutoCloseable, and support for adding other onClose Runnables.
 *
 * @param <T> The type of elements being extracted
 */
public class ResultSetIterator<T> implements Iterator<T>, AutoCloseable {
    private final ResultSet resultSet;
    private final Extractor<T> extractor;
    private Runnable closers = () -> {
    };
    private Boolean nextAvailable = null;

    /**
     * Create an instance with with a ResultSet to iterate over, and an Extractor to apply to
     * each row.
     *
     * @param resultSet the ResultSet to iterate over
     * @param extractor the Extractor to apply to each row
     */
    public ResultSetIterator(final ResultSet resultSet, final Extractor<T> extractor) {
        Objects.requireNonNull(resultSet, "A non null result set is required.");
        Objects.requireNonNull(extractor, "A non null extractor is required");
        this.extractor = extractor;
        this.resultSet = resultSet;
    }

    @Override
    public boolean hasNext() {
        if (nextAvailable == null) {
           try {
               nextAvailable = resultSet.next();
           } catch (Exception e) {
               throw propagate(e);
           }
        }
        return nextAvailable;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        nextAvailable = null;
        try {
            return extractor.extract(resultSet);
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    /**
     * Add a Runnable to be invoked when this instance is closed. Runnables will be invoked in the order they are added.
     *
     * @param runnable a runnable
     * @return this instance
     */
    public ResultSetIterator<T> onClose(final Runnable runnable) {
        Runnable previous = closers;
        closers = () -> {
            previous.run();
            runnable.run();
        };
        return this;
    }

    @Override
    public void close() throws SQLException {
        resultSet.close();
        closers.run();
    }
}
