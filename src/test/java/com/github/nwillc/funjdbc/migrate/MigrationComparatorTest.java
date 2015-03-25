package com.github.nwillc.funjdbc.migrate;

import com.github.nwillc.contracts.ComparatorContract;
import org.junit.Before;

import java.util.Comparator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationComparatorTest extends ComparatorContract<Migration> {

    @Before
    public void setUp() throws Exception {
        setNulls(Nulls.NULLS_FIRST);
    }

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