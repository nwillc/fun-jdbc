package com.github.nwillc.funjdbc.migrate;

import com.github.nwillc.funjdbc.ConnectionProvider;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MigrationBaseTest {
    private Migration migration;

    @Before
    public void setUp() throws Exception {
        migration = new DummyMigration();
    }

    @Test
    public void testRunAlwaysDefaultsFalse() throws Exception {
       assertThat(migration.runAlways()).isFalse();
    }

    @Test
    public void testShouldProvideConnection() throws Exception {
        ConnectionProvider connectionProvider = mock(ConnectionProvider.class);
        Manager manager = Manager.getInstance();

        manager.setConnectionProvider(connectionProvider);
        Connection connection = migration.getConnection();
        verify(connectionProvider).getConnection();
    }

    private static class DummyMigration extends MigrationBase {
        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public boolean perform() {
            return false;
        }
    }
}