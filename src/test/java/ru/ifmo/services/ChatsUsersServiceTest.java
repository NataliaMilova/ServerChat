package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;
import ru.ifmo.entity.Chat;
import ru.ifmo.entity.User;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChatsUsersServiceTest {
    private static ChatsUsersService chatsUsersService;
    private static File resourcesDirectory = new File("src/test/resources");
    private static SQLiteDataSource dataSource = new SQLiteDataSource();
    private static Connection connection;
    private static User existsUser1 = new User("+79141385421", "Julian", "12345");
    private static User existsUser2 = new User("+79141385422", "Melman", "12345");
    private static Chat existsChat1 = new Chat(1, "test1");
    private static Chat existsChat2 = new Chat(2, "test2");

    @BeforeClass
    public static void beforeClass() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        connection = dataSource.getConnection();
        chatsUsersService = new ChatsUsersService(connection);
    }


    @Test
    public void getChatsByUserId() throws SQLException {
        List<Chat> result = new ArrayList<>();
        result.add(existsChat2);
        result.add(existsChat1);
        chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat1.getChatId());
        chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat2.getChatId());
        assertEquals(result, chatsUsersService.getChatsByUserId(existsUser1.getUserId()));
        chatsUsersService.outUserFromChat(existsChat1.getChatId(), existsUser1.getUserId());
        chatsUsersService.outUserFromChat(existsChat2.getChatId(), existsUser1.getUserId());
    }

    @Test
    public void getUsersIdByChatId() throws SQLException {
        List<String> result = new ArrayList<>();
        result.add(existsUser1.getUserId());
        result.add(existsUser1.getUserId());
        chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat1.getChatId());
        chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat1.getChatId());
        assertEquals(result, chatsUsersService.getUsersIdByChatId(existsChat1.getChatId()));
        chatsUsersService.outUserFromChat(existsChat1.getChatId(), existsUser1.getUserId());
        chatsUsersService.outUserFromChat(existsChat2.getChatId(), existsUser1.getUserId());
    }

    @Test
    public void outUserFromChat() throws SQLException {
        List<Chat> result = new ArrayList<>();
        chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat1.getChatId());
        chatsUsersService.outUserFromChat(existsChat1.getChatId(), existsUser1.getUserId());
        assertEquals(result, chatsUsersService.getChatsByUserId(existsUser1.getUserId()));
    }

    @Test
    public void insertChatsUsers() throws SQLException {
        assertEquals(true, chatsUsersService.insertChatsUsers(existsUser1.getUserId(), existsChat1.getChatId()));
        assertEquals(true, chatsUsersService.insertChatsUsers(existsUser2.getUserId(), existsChat2.getChatId()));
        chatsUsersService.outUserFromChat(existsChat1.getChatId(), existsUser1.getUserId());
        chatsUsersService.outUserFromChat(existsChat2.getChatId(), existsUser2.getUserId());

    }

}