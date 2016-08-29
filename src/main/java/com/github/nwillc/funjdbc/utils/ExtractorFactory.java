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
import java.util.function.Supplier;

/**
 * Create a basic Extractor from a series of setter/getter/index tuples.
 *
 * @since 0.8.3+
 */
public final class ExtractorFactory<B> {
    private BiConsumer<B, ResultSet> consumer = (b, r) -> {};
    private Supplier<B> factory;

    /**
     * Provide a factor this Extractor will use to create the object it will extract data
     * from the result set into.
     *
     * @param factory a factory instance
     * @return the extractor factory.
     */
    public ExtractorFactory<B> factory(Supplier<B> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Add an extraction used by the Extractor. An extraction pulls a know type from the ResultSet and calls a setter
     * with it.
     *
     * @param setter a BiConsumer that will set the value extracted
     * @param getter a BiFunction that will extract the value from the ResultSet
     * @param index  the column index
     * @param <T>    the type
     * @return the factory
     */
    public <T> ExtractorFactory<B> add(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
        final Extraction<B, T> extraction = new Extraction<>(setter, getter, index);
        consumer = consumer.andThen(extraction);
        return this;
    }

    /**
     * Create the Extractor based on the factory and extractions added.
     * @return the generated extractor
     */
    public Extractor<B> getExtractor() {
        return new GeneratedExtractor<>(factory, consumer);
    }

    private static class GeneratedExtractor<B> implements Extractor<B> {
        private final Supplier<B> factory;
        private final BiConsumer<B, ResultSet> consumer;

        GeneratedExtractor(Supplier<B> factory, BiConsumer<B, ResultSet> consumer) {
            this.factory = factory;
            this.consumer = consumer;
        }

        @Override
        public B extract(ResultSet rs) throws SQLException {
            final B bean = factory.get();
            consumer.accept(bean, rs);
            return bean;
        }
    }

    private static class Extraction<B, T> implements BiConsumer<B, ResultSet> {
        final BiConsumer<B, T> setter;
        final BiFunction<ResultSet, Integer, T> getter;
        final Integer index;

        Extraction(BiConsumer<B, T> setter, BiFunction<ResultSet, Integer, T> getter, Integer index) {
            this.setter = setter;
            this.getter = getter;
            this.index = index;
        }

        @Override
        public void accept(B bean, ResultSet resultSet) {
            setter.accept(bean, getter.apply(resultSet, index));
        }
    }

}
