package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;
import ru.ifmo.entity.User;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class UsersServiceTest {

    private static UsersService usersService;
    private static File resourcesDirectory = new File("src/test/resources");
    private static SQLiteDataSource dataSource = new SQLiteDataSource();
    private static Connection connection;
    private static User existsUser1 = new User("+79141385421", "Julian", "12345");
    private static User existsUser2 = new User("+79141385422", "Melman", "12345");
    private static User insertUser1 = new User ("1","1","1");
    private static User updateUser1 = new User ("2","2","2");

    @BeforeClass
    public static void beforeClass() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        connection = dataSource.getConnection();
        usersService = new UsersService(connection);
    }


    @Test
    public void getUserById() throws SQLException {
        assertEquals(existsUser1, usersService.getUserById(existsUser1.getUserId()));
        assertEquals(existsUser2, usersService.getUserById(existsUser2.getUserId()));
    }

    @Test
    public void insertUser() throws SQLException {
        usersService.insertUser(insertUser1);
        assertEquals(insertUser1, usersService.getUserById(insertUser1.getUserId()));
    }

    @Test
    public void updateUserLastVisit() throws SQLException {
        usersService.insertUser(updateUser1);
        updateUser1.setLastVisit(1);
        usersService.updateUserLastVisit(updateUser1);
        assertEquals(updateUser1, usersService.getUserById(updateUser1.getUserId()));
        usersService.deleteUserById(updateUser1.getUserId());
    }

    @Test
    public void deleteUserById() throws SQLException {
        User user = new User();
        usersService.deleteUserById(insertUser1.getUserId());
        assertEquals(user, usersService.getUserById(insertUser1.getUserId()));
    }


    @Test
    public void checkOfUsersExistence() throws SQLException {
        assertEquals(true, usersService.checkOfUsersExistence(existsUser1.getUserId()));
        assertEquals(true, usersService.checkOfUsersExistence(existsUser2.getUserId()));
    }


}