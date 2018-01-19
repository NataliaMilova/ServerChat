package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;
import ru.ifmo.entity.Chat;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ChatsServiceTest {

    private static SQLiteDataSource dataSource = new SQLiteDataSource();
    private static ChatsService chatsService;
    private static File resourcesDirectory = new File("src/test/resources");
    private static Chat existsChat1 = new Chat(1, "test1");
    private static Chat existsChat2 = new Chat(2, "test2");
    private static Connection connection;


    @BeforeClass
    public static void beforeClass() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        connection = dataSource.getConnection();
        chatsService = new ChatsService(connection);
    }


    @Test
    public void getChatById() throws SQLException {
        assertEquals(existsChat1, chatsService.getChatById(existsChat1.getChatId()));
        assertEquals(existsChat2, chatsService.getChatById(existsChat2.getChatId()));
    }


    @Test
    public void insertChat() throws SQLException {
        String chatNameInsertChat1 = "test3";
        String chatNameInsertChat2 = "test4";
        Chat chat1 = new Chat();
        chat1.setChatName(chatNameInsertChat1);
        long chatIdInsertChat1 = chatsService.insertChat(chatNameInsertChat1);
        chat1.setChatId(chatIdInsertChat1);
        Chat chat2 = new Chat();
        chat2.setChatName(chatNameInsertChat2);
        long chatIdInsertChat2 = chatsService.insertChat(chatNameInsertChat2);
        chat2.setChatId(chatIdInsertChat2);
        assertEquals(chat1, chatsService.getChatById(chatIdInsertChat1));
        assertEquals(chat2, chatsService.getChatById(chatIdInsertChat2));
        chatsService.deleteChat(chatIdInsertChat1);
        chatsService.deleteChat(chatIdInsertChat2);
    }

    @Test
    public void deleteChat() throws SQLException {
        Chat chat = new Chat();
        long id1 = chatsService.insertChat("hello");
        long id2 = chatsService.insertChat("hello");
        chatsService.deleteChat(id1);
        chatsService.deleteChat(id2);
        assertEquals(chat, chatsService.getChatById(id1));
        assertEquals(chat, chatsService.getChatById(id2));
    }

    @Test
    public void checkOfChatExistence() throws SQLException {
        assertEquals(true, chatsService.checkOfChatExistence(existsChat1.getChatId()));
        assertEquals(true, chatsService.checkOfChatExistence(existsChat2.getChatId()));
    }

}
