/*
 * Copyright (c) 2016, nwillc@gmail.com
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

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.functions.Extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Create a simple extractor.
 */
public final class ExtractorFactory<B> {
    private List extractions = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Extractor<B> create(Supplier<B> factory) {
        return new GeneratedExtractor<>(factory, extractions);
    }

    @SuppressWarnings("unchecked")
    public <T> ExtractorFactory<B> add(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
        final Extraction<B, T> extraction = new Extraction<>(setter, getter, index);
        extractions.add(extraction);
        return this;
    }

    private static class GeneratedExtractor<B> implements Extractor<B> {
        private final Supplier<B> factory;
        private final List<Extraction<B, Object>> extractions;

        GeneratedExtractor(Supplier<B> factory, List<Extraction<B, Object>> extractions) {
            this.factory = factory;
            this.extractions = extractions;
        }

        @Override
        public B extract(ResultSet rs) throws SQLException {
            final B bean = factory.get();
            extractions.forEach(e -> e.copy(bean, rs));
            return bean;
        }
    }

    // TODO: use a consumer andThen to chain extractions rather then loop
    private static class Extraction<B, T> {
        final BiConsumer<B, T> setter;
        final BiFunction<ResultSet, Integer, T> getter;
        final Integer index;

        Extraction(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
            this.setter = setter;
            this.getter = getter;
            this.index = index;
        }

        void copy(B bean, ResultSet rs) {
            setter.accept(bean, getter.apply(rs, index));
        }
    }

}
