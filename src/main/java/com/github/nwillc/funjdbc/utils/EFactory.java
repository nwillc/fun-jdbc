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

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.functions.Enricher;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.functions.ThrowingBiFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Create a basic Enricher or Extractor from a series of setter/getter/index tuples.
 *
 * @param <B>  the type of the Bean
 * @since 0.8.3+
 */
public final class EFactory<B> {
    private Enricher<B> consumer = null;
    private Supplier<B> factory = null;

    /**
     * Provide a factor this Extractor will use to create the object it will extract data
     * from the result set into.
     *
     * @param factory a factory instance
     *
     * @return the extractor factory.
     */
    public EFactory<B> factory(Supplier<B> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Add an extraction used by the Extractor. An extraction pulls a know type from an indexed column of ResultSet
     * and calls a setter with it.
     *
     * @param setter a BiConsumer that will set the value extracted
     * @param getter a BiFunction that will extract the value from the ResultSet
     * @param index the column index
     * @param <T> the type
     *
     * @return the factory
     */
    public <T> EFactory<B> add(BiConsumer<B, T> setter, ThrowingBiFunction<ResultSet, Integer, T> getter, Integer index) {
        Objects.requireNonNull(setter);
        Objects.requireNonNull(getter);
        Objects.requireNonNull(index);
        final SingleEnricher<B, T, Integer> singleEnricher = new SingleEnricher<>(setter, getter, index);
        consumer = (consumer == null) ? singleEnricher : consumer.andThen(singleEnricher);
        return this;
    }

    /**
     * Add an extraction used by the Extractor. An extraction pulls a know type from a named column of the ResultSet
     * and calls a setter with it.
     *
     * @param setter a BiConsumer that will set the value extracted
     * @param getter a BiFunction that will extract the value from the ResultSet
     * @param column the name of the column
     * @param <T> the type
     *
     * @return the factory
     */
    public <T> EFactory<B> add(BiConsumer<B, T> setter, ThrowingBiFunction<ResultSet, String, T> getter, String column) {
        Objects.requireNonNull(setter);
        Objects.requireNonNull(getter);
        Objects.requireNonNull(column);
        final SingleEnricher<B, T, String> singleEnricher = new SingleEnricher<>(setter, getter, column);
        consumer = (consumer == null) ? singleEnricher : consumer.andThen(singleEnricher);
        return this;
    }

    /**
     * Get the Enricher that results from the added setter, getter, pairings add.
     *
     * @return the generated enricher
     *
     * @since 0.8.7
     */
    public Enricher<B> getEnricher() {
        Objects.requireNonNull(consumer, "A consumer(s) are required");
        return consumer;
    }

    /**
     * Create the Extractor based on the factory and extractions added.
     *
     * @return the generated extractor
     */
    public Extractor<B> getExtractor() {
        Objects.requireNonNull(factory, "A non null factory is required");
        Objects.requireNonNull(consumer, "A consumer(s) are required");
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

    private static class SingleEnricher<B, T, C> implements Enricher<B> {
        final BiConsumer<B, T> setter;
        final BiFunction<ResultSet, C, T> getter;
        final C column;

        SingleEnricher(BiConsumer<B, T> setter, BiFunction<ResultSet, C, T> getter, C column) {
            this.setter = setter;
            this.getter = getter;
            this.column = column;
        }

        @Override
        public void acceptThrows(B bean, ResultSet resultSet) {
            setter.accept(bean, getter.apply(resultSet, column));
        }
    }

}
