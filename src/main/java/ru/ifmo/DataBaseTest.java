package ru.ifmo;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DataBaseTest {

    public static void createNewDatabase(String fileName) {
        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl("jdbc:sqlite:./" + fileName);

            try {
                Connection connection = dataSource.getPooledConnection().getConnection();
                /*Class.forName("org.sqlite.JDBC");
                String dbURL = "jdbc:sqlite:./" + fileName;
                Connection conn = DriverManager.getConnection(dbURL);*/
                if (connection != null) {
                    System.out.println("Connected to the database");
                    DatabaseMetaData dm = connection.getMetaData();
                    System.out.println("Driver name: " + dm.getDriverName());
                    System.out.println("Driver version: " + dm.getDriverVersion());
                    System.out.println("Product name: " + dm.getDatabaseProductName());
                    System.out.println("Product version: " + dm.getDatabaseProductVersion());
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }


    public static void main(String[] args) {
        createNewDatabase("ChatServer.db");
    }
}
