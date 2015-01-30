/*
 * Copyright (c) 2015, nwillc@gmail.com
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


import com.github.nwillc.contracts.SingletonContract;
import com.github.nwillc.funjdbc.InMemWordsDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ManagerTest extends SingletonContract {
    private Manager manager;
    private InMemWordsDatabase dao;

    @Override
    protected Class<?> getClassToTest() {
        return Manager.class;
    }

    @Before
    public void setUp() throws Exception {
        manager = Manager.getInstance();
        assertThat(manager).isNotNull();
        dao = new InMemWordsDatabase();
        manager.setConnectionProvider(dao);
        dao.create();
    }

    @After
    public void tearDown() throws Exception {
        manager.clear();
        dao.drop();
    }

    @Test
    public void testRegisterClass() throws Exception {
        manager.add(DummyMigration.class);
        assertThat(manager.getMigrations()).hasAtLeastOneElementOfType(DummyMigration.class);
    }

    @Test
    public void testRegisterInstance() throws Exception {
        manager.add(new DummyMigration());
        assertThat(manager.getMigrations()).hasAtLeastOneElementOfType(DummyMigration.class);
    }

    @Test
    public void testRegistrationOrder() throws Exception {
        DummyMigration one = new DummyMigration("1");
        DummyMigration two = new DummyMigration("2");
        DummyMigration three = new DummyMigration("3");

        manager.add(two);
        manager.add(three);
        manager.add(one);
        assertThat(manager.getMigrations()).containsExactly(one, two, three);
    }

    @Test
    public void testMigrationsEnabled() throws Exception {
        assertThat(manager.migrationsEnabled()).isFalse();
        manager.enableMigrations();
        assertThat(manager.migrationsEnabled()).isTrue();
    }

    @Test
    public void testSimpleMigration() throws Exception {
        manager.enableMigrations();
        assertThat(manager.migrationsEnabled()).isTrue();
        manager.add(new DummyMigration("first"));
        assertThat(manager.migrated("first")).isFalse();
        manager.doMigrations();
        assertThat(manager.migrated("first")).isTrue();
    }

    public static class DummyMigration extends MigrationBase {
        private String identifier;

        public DummyMigration() {
            this(null);
        }

        public DummyMigration(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public boolean perform() {
            return true;
        }
    }
}