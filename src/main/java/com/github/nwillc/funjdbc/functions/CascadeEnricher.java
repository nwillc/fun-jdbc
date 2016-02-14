package com.github.nwillc.funjdbc.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An Enricher that cascdes through a set of provided Enrichers, allowing for
 * an entity to be enriched in multiple ways.
 *
 * @param <E> the entity type
 * @since 0.8.1
 */
public class CascadeEnricher<E> implements Enricher<E> {
    private final Enricher<E>[] enrichers;

    /**
     * Create an enricher based on multiple other enrichers.
     *
     * @param enrichers the enrichers to cascade through
     */
    @SafeVarargs
	public CascadeEnricher(Enricher<E>... enrichers) {
        this.enrichers = enrichers;
    }

    @Override
    public void enrich(E entity, ResultSet rs) throws SQLException {
        if (enrichers != null) {
            for (Enricher<E> enricher : enrichers) {
                enricher.enrich(entity, rs);
            }
        }
    }
}
