package ru.ifmo.old.services;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.ifmo.old.entity.User;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class UsersServiceTest {

    private static UsersService usersService;
    private static Connection connection;
    private static User existsUser1 = new User("+111111", "1", "12345");
    private static User existsUser2 = new User("+222222", "2", "12345");
    private static User insertUser1 = new User ("1","1","1");
    private static User updateUser1 = new User ("2","2","2");

    @BeforeClass
    public static void beforeClass() throws SQLException {
        connection = DataSource.getConnection();
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