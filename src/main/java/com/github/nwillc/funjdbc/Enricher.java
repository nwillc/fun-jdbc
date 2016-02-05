package com.github.nwillc.funjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  Enrich an object with values from the current row of a result set.
 */
@FunctionalInterface
public interface Enricher<E> {
	void enrich(E entity, ResultSet rs) throws SQLException;
}
