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

import java.util.function.BiFunction;

import static com.github.nwillc.funjdbc.utils.Throwables.propagate;

/**
 * A BiFunction that allows for exceptions, rethrowing them as an appropriate
 * type of RuntimeException.
 *
 * @param <T> type of first argument to apply
 * @param <U> type of second argument to apply
 * @param <R> type of return value from apply
 * @since 0.8.4
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R> extends BiFunction<T, U, R> {

    @Override
    default R apply(T t, U u) {
        try {
            return applyThrows(t, u);
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    @SuppressWarnings("RedundantThrows")
    R applyThrows(T t, U u) throws Exception;
}
