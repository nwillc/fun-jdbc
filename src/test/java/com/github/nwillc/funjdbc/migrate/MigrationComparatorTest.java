package com.github.nwillc.funjdbc.migrate;

import com.github.nwillc.contracts.ComparatorContract;

import java.util.Comparator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationComparatorTest extends ComparatorContract<Migration> {

    @Override
    protected Comparator<Migration> getComparator() {
        return new Manager.MigrationComparator();
    }

    @Override
    protected Migration getValue() {
        Migration migration = mock(Migration.class);
        when(migration.getIdentifier()).thenReturn("1");
        return migration;
    }

    @Override
    protected Migration getLesserValue() {
        Migration migration = mock(Migration.class);
        when(migration.getIdentifier()).thenReturn("0");
        return migration;
    }

}