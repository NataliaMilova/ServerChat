package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import ru.ifmo.entity.Chat;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ChatsServiceTest {

    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private static ChatsService chatsService;
    private static File resourcesDirectory = new File("src/test/resources");
    private static Chat existsChat1 = new Chat(1, "test1");
    private static Chat existsChat2 = new Chat(2, "test2");
    private static int chatIdInsertChat1;
    private static int chatIdInsertChat2;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        chatsService = new ChatsService(dataSource.getPooledConnection());
    }


    @Test
    public void getChatById() throws SQLException {
        Chat chat1 = new Chat(existsChat1.getChatId(), existsChat1.getChatName());
        Chat chat2 = new Chat(existsChat2.getChatId(), existsChat2.getChatName());
        assertEquals(chat1, chatsService.getChatById(existsChat1.getChatId()));
        assertEquals(chat2, chatsService.getChatById(existsChat2.getChatId()));
    }


    @Test
    public void insertChat() throws SQLException {
        String chatNameInsertChat1 = "test3";
        String chatNameInsertChat2 = "test4";
        Chat chat1 = new Chat();
        chat1.setChatName(chatNameInsertChat1);
        chatIdInsertChat1 = chatsService.insertChat(chatNameInsertChat1);
        chat1.setChatId(chatIdInsertChat1);
        Chat chat2 = new Chat();
        chat2.setChatName(chatNameInsertChat2);
        chatIdInsertChat2 = chatsService.insertChat(chatNameInsertChat2);
        chat2.setChatId(chatIdInsertChat2);
        assertEquals(chat1, chatsService.getChatById(chatIdInsertChat1));
        assertEquals(chat2, chatsService.getChatById(chatIdInsertChat2));
    }

    @Test
    public void deleteChat() throws SQLException {
        Chat chat = new Chat();
        chatsService.deleteChat(chatIdInsertChat1);
        chatsService.deleteChat(chatIdInsertChat2);
        assertEquals(chat, chatsService.getChatById(chatIdInsertChat1));
        assertEquals(chat, chatsService.getChatById(chatIdInsertChat2));
    }

    @Test
    public void checkOfChatExistence() throws SQLException {
        assertEquals(true, chatsService.checkOfChatExistence(existsChat1.getChatId()));
        assertEquals(true, chatsService.checkOfChatExistence(existsChat2.getChatId()));
    }

}