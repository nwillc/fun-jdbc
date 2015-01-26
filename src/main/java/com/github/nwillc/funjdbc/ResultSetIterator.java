/*
 * Copyright (c) 2015, nwillc@gmail.com
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.github.nwillc.funjdbc.utils.Throwables.propagate;

/**
 * This is an Iterator that traverses a ResultSet. Additionally this implements Autocloseable, and support for adding other onClose Runnables.
 *
 * @param <T> The type of elements being extracted
 */
public class ResultSetIterator<T> implements Iterator<T>, AutoCloseable {
    private final ResultSet resultSet;
    private final Extractor<T> extractor;
    private final List<Runnable> closers = new ArrayList<>();
    private Optional<Boolean> nextAvailable = Optional.empty();

    public ResultSetIterator(final ResultSet resultSet, final Extractor<T> extractor) {
        this.extractor = extractor;
        this.resultSet = resultSet;
    }

    @Override
    public boolean hasNext() {
        if (nextAvailable.isPresent()) {
            return nextAvailable.get();
        }
        try {
            nextAvailable = Optional.of(resultSet.next());
            return nextAvailable.get();
        } catch (Exception e) {
            nextAvailable = Optional.of(false);
            throw propagate(e);
        }
    }

    @Override
    public T next() {
        hasNext();
        if (!nextAvailable.get()) {
            throw new NoSuchElementException();
        }

        try {
            T result = extractor.extract(resultSet);
            nextAvailable = Optional.empty();
            return result;
        } catch (SQLException e) {
            nextAvailable = Optional.of(false);
            throw propagate(e);
        }
    }

    /**
     * Add a Runnable to be invoked when this instance is closed. Runnables will be invoked in the order they are added.
     * @param runnable a runnable
     * @return this instance
     */
    public ResultSetIterator<T> onClose(final Runnable runnable) {
        closers.add(runnable);
        return this;
    }

    @Override
    public void close() throws Exception {
        resultSet.close();
        closers.stream().forEach(Runnable::run);
    }
}
