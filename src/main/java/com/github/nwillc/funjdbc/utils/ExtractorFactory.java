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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Create a simple extractor from a series of getter/setter/index tuples.
 */
public final class ExtractorFactory<B> {
    private Consumer<Variant<B>> consumer = v -> {};
    private Supplier<B> factory;

    public ExtractorFactory<B> factory(Supplier<B> factory) {
        this.factory = factory;
        return this;
    }

    public <T> ExtractorFactory<B> add(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
        final Extraction<B, T> extraction = new Extraction<>(setter, getter, index);
        consumer = consumer.andThen(extraction);
        return this;
    }

    public Extractor<B> getExtractor() {
        return new GeneratedExtractor<>(factory, consumer);
    }

    private static class GeneratedExtractor<B> implements Extractor<B> {
        private final Supplier<B> factory;
        private final Consumer<Variant<B>> consumer;

        GeneratedExtractor(Supplier<B> factory, Consumer<Variant<B>> consumer) {
            this.factory = factory;
            this.consumer = consumer;
        }

        @Override
        public B extract(ResultSet rs) throws SQLException {
            final Variant<B> variant = new Variant<>(factory.get(), rs);
            consumer.accept(variant);
            return variant.bean;
        }
    }

    private static class Extraction<B, T> implements Consumer<Variant<B>> {
        final BiConsumer<B, T> setter;
        final BiFunction<ResultSet, Integer, T> getter;
        final Integer index;

        Extraction(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
            this.setter = setter;
            this.getter = getter;
            this.index = index;
        }

        @Override
        public void accept(Variant<B> variant) {
            setter.accept(variant.bean, getter.apply(variant.resultSet, index));
        }
    }

    private static class Variant<B> {
        final B bean;
        final ResultSet resultSet;

        public Variant(B bean, ResultSet resultSet) {
            this.bean = bean;
            this.resultSet = resultSet;
        }
    }

}
