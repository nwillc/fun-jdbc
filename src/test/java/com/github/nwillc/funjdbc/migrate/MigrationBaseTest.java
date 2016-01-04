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
 */

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