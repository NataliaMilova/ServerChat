package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.File;


public class UsersServiceTest {

    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    //private static UsersService usersService = new UsersService();
    private static File resourcesDirectory = new File("src/test/resources");

    @BeforeClass
    public static void beforeClass() {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
    }


    @Test
    public void getUserById() {
    }

    @Test
    public void insertUser() {
    }

    @Test
    public void updateUserLastVisit() {
    }

    @Test
    public void checkOfUsersExistence() {
    }


}