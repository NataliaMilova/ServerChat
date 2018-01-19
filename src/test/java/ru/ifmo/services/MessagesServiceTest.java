package ru.ifmo.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteDataSource;
import ru.ifmo.entity.Chat;
import ru.ifmo.entity.Message;
import ru.ifmo.entity.User;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MessagesServiceTest {

    private static SQLiteDataSource dataSource = new SQLiteDataSource();
    private static MessagesService messagesService;
    private static File resourcesDirectory = new File("src/test/resources");
    private static Chat existsChat1 = new Chat(1, "test1");
    private static Chat existsChat2 = new Chat(2, "test2");
    private static User existsUser1 = new User("+79141385421", "Julian", "12345");
    private static Message existsMessage1 = new Message(1,"1", "+79141385421", 3);
    private static Connection connection;


    @BeforeClass
    public static void beforeClass() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:" + resourcesDirectory.getAbsolutePath() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        connection = dataSource.getConnection();
        messagesService = new MessagesService(connection, 5);
    }

    @Test
    public void getMessageById() throws SQLException {
        assertEquals(existsMessage1, messagesService.getMessageById(1));
    }

    @Test
    public void getMessagesByChatId() throws SQLException {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Message message = new Message((""+ i), existsUser1.getUserId() , existsChat1.getChatId());
            messages.add(messagesService.getMessageById(messagesService.insertMessage(message)));
        }
        assertEquals(messages, messagesService.getMessagesByChatId(existsChat1.getChatId()));
        for (Message message : messages)
            messagesService.deleteMessageById(message.getMessageId());
    }

    @Test
    public void getMessagesByChatId1() throws SQLException {
        List<Message> messages = new ArrayList<>();
        List<Message> messages1 = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Message message = new Message((""+ i), existsUser1.getUserId() , existsChat1.getChatId());
            messages.add(messagesService.getMessageById(messagesService.insertMessage(message)));
        }
        for (int i = 0; i < 5; i++){
            Message message = new Message((""+ i), existsUser1.getUserId() , existsChat1.getChatId());
            messages1.add(messagesService.getMessageById(messagesService.insertMessage(message)));
        }
        assertEquals(messages1, messagesService.getMessagesByChatId(existsChat1.getChatId(), 1));
        assertEquals(messages, messagesService.getMessagesByChatId(existsChat1.getChatId(), 2));
        for (Message message : messages)
            messagesService.deleteMessageById(message.getMessageId());
        for (Message message : messages1)
            messagesService.deleteMessageById(message.getMessageId());
    }

    @Test
    public void insertMessage() throws SQLException {
        Message message = new Message("2", "+79141385421", 3);
        long messageId = messagesService.insertMessage(message);
        Message result = messagesService.getMessageById(messageId);
        message.setTimestamp(result.getTimestamp());
        message.setMessageId(messageId);
        assertEquals(message, result);
        messagesService.deleteMessageById(messageId);
    }

    @Test
    public void getChatsWithNewMessagesByUserId() throws SQLException {
        existsUser1.setLastVisit(System.currentTimeMillis());
        List<Chat> chats = new ArrayList<>();
        List<Long> result = new ArrayList<>();
        result.add(existsChat1.getChatId());
        chats.add(existsChat1);
        chats.add(existsChat2);
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Message message = new Message((""+ i), existsUser1.getUserId() , existsChat1.getChatId());
            messages.add(messagesService.getMessageById(messagesService.insertMessage(message)));
        }
        assertEquals(result, messagesService.getChatsWithNewMessagesByUserId(existsUser1, chats));
        for (Message message : messages)
            messagesService.deleteMessageById(message.getMessageId());
    }

    @Test
    public void deleteMessageByMessageId() throws SQLException {
        Message message = new Message("2", "+79141385421", 3);
        Message result = new Message();
        long messageId = messagesService.insertMessage(message);
        messagesService.deleteMessageById(messageId);
        assertEquals(result, messagesService.getMessageById(messageId));
    }
}