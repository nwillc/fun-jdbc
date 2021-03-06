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
import java.util.Objects;

/**
 * Enrich an object with values from the current row of a result set.
 *
 * @since 0.8.0
 */
@FunctionalInterface
public interface Enricher<B> extends ThrowingBiConsumer<B, ResultSet> {
    void acceptThrows(B bean, ResultSet rs) throws SQLException;

    /**
     * Chains another enricher to this one to be called upon completion.
     *
     * @param after another enricher
     * @return the enriched object
     * @see EFactory
     * @since 0.8.5
     */
    default Enricher<B> andThen(Enricher<B> after) {
        Objects.requireNonNull(after);

        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
