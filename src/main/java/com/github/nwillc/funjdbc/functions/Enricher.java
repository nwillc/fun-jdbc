package com.github.nwillc.funjdbc.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enrich an object with values from the current row of a result set.
 *
 * @since 0.8.0
 */
public interface Enricher<E> {

    /**
     * Enrich an entity with values from the current row of a result set.
     *
     * @param entity the entity to enrich
     * @param rs     the result set, at the current row
     * @throws SQLException
     */
    void enrich(E entity, ResultSet rs) throws SQLException;
}
