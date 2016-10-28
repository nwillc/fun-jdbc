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
 *
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
