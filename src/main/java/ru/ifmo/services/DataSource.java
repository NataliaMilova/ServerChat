package ru.ifmo.services;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static volatile DataSource dataSource;
    private ComboPooledDataSource cpds;
    private DataSource() throws PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/chatserver");
        cpds.setUser("julian");
        cpds.setPassword("gbdrdflhfnt");
        cpds.setMinPoolSize(1);
        cpds.setMaxPoolSize(5);
    }
    public static DataSource instance() {
        if (dataSource == null) {
            synchronized (DataSource.class) {
                if (dataSource == null) {
                    try {
                        dataSource = new DataSource();
                    }
                    catch (PropertyVetoException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return dataSource;
    }
    public Connection connection() throws SQLException {
        return cpds.getConnection();
    }
    public static Connection getConnection() throws SQLException {
        return instance().connection();
    }
}
