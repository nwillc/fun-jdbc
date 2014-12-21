/*
 * Copyright (c) 2014, nwillc@gmail.com
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


import almost.functional.utils.LogFactory;
import almost.functional.utils.Preconditions;
import com.github.nwillc.funjdbc.ConnectionProvider;
import com.github.nwillc.funjdbc.DbAccessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager implements DbAccessor {
    private static final Logger LOGGER = LogFactory.getLogger();
    private static final Manager INSTANCE = new Manager();

    private static final String CREATE = "CREATE TABLE MIGRATIONS ( IDENTIFIER CHAR(40) PRIMARY KEY, DESCRIPTION CHAR(120))";
    private static final String INSERT = "INSERT INTO MIGRATIONS (IDENTIFIER, DESCRIPTION) VALUES('%s', '%s')";
    private static final String FIND = "SELECT * FROM MIGRATIONS WHERE IDENTIFIER = '%s'";

    private Set<Migration> migrations = new TreeSet<>(new MigrationComparator());
    private ConnectionProvider connectionProvider;

    public static Manager getInstance() {
        return INSTANCE;
    }

    private Manager() {
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionProvider == null) {
            throw new IllegalStateException("No ConnectionProvider available.");
        }
        return connectionProvider.getConnection();
    }

    // TODO: Test for single no arg constructor
    void add(Class<? extends Migration> aMigration) throws IllegalArgumentException {
        try {
            migrations.add(aMigration.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Unable to add " + aMigration.getName(), e);
            throw new IllegalArgumentException(e);
        }
    }

    void add(Migration migration) {
        migrations.add(migration);
    }

    public Set<Migration> getMigrations() {
        return migrations;
    }

    public void clear() {
        migrations.clear();
    }

    public boolean migrationsEnabled() {
        try (Connection connection = getConnection()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "MIGRATIONS", null);
            return stream(rs -> rs.getString(3), resultSet).count() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void enableMigrations() {
        try (Connection c = getConnection();
             Statement statement = c.createStatement()) {
             statement.execute(CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean migrated(String first) {
        try {
            return dbFind(rs -> rs.getString(1), FIND, first).isPresent();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doMigrations() {
       migrations.stream().forEach(migration -> {
           if ((!migrated(migration.getIdentifier()) || migration.runAlways())
            && migration.perform()) {
               try {
                   dbUpdate(INSERT, migration.getIdentifier(), migration.getDescription());
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    private static class MigrationComparator implements Comparator<Migration> {
        @Override
        public int compare(Migration o1, Migration o2) {
            Preconditions.checkNotNull(o1, "Can not compare null Migration instance");
            Preconditions.checkNotNull(o2, "Can not compare null Migration instance");

            if (o1.getIdentifier() == null ^ o2.getIdentifier() == null) {
                return (o1.getIdentifier() == null) ? -1 : 1;
            }

            if (o1.getIdentifier() == null) {
                return 0;
            }

            return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
        }
    }
}
