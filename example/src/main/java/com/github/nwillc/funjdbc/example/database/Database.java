package com.github.nwillc.funjdbc.example.database;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Database implements ConnectionProvider {
    private final static String DRIVER = "org.h2.Driver";
    private final static String URL = "jdbc:h2:mem:sample";
    private final DataSource dataSource;

    public Database() throws ClassNotFoundException {
        Class.forName(DRIVER);
        dataSource = setupDataSource(URL);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static DataSource setupDataSource(String connectURI) {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }
}
