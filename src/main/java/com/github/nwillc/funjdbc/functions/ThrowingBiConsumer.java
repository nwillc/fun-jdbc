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

package com.github.nwillc.funjdbc.functions;

import java.util.function.BiConsumer;

import static com.github.nwillc.funjdbc.utils.Throwables.propagate;

/**
 * A BiConsumer that allows for Exceptions which will be converted to appropriate RuntimeExceptions.
 *
 * @param <T> type of the first argument to operations
 * @param <U> type of second argument to operations
 *
 * @since 0.8.5
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U> extends BiConsumer<T, U> {

    /**
     * The default accept allowing assignment as a normal BiConsumer. This method
     * will invoke the acceptThrows and correctly propagate any exception.
     *
     * @param t type of the first argument to operation
     * @param u type of second argument to operation
     */
    @Override
    default void accept(T t, U u) {
        try {
            acceptThrows(t, u);
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    /**
     * A method that accepts two arguments and allows for an Exception to be throws.
     *
     * @param t type of the first argument to operation
     * @param u type of second argument to operation
     *
     * @throws Exception this function can throw exceptions
     */
    @SuppressWarnings({"RedundantThrows", "EmptyMethod"})
    void acceptThrows(T t, U u) throws Exception;
}
