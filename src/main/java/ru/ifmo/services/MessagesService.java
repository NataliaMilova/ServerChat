package ru.ifmo.services;

import ru.ifmo.entity.Chat;
import ru.ifmo.entity.Message;
import ru.ifmo.entity.User;

import javax.sql.PooledConnection;
import java.sql.*;
import java.util.*;

public class MessagesService {

    private Connection connection;

    private int limitMessagesInPage;

    public MessagesService(Connection connection, int limitMessagesInPage) {
        this.connection = connection;
        this.limitMessagesInPage = limitMessagesInPage;
    }

    public List<Message> getMessagesByChatId(int chatId, int pageNum, int messageId, int off) throws SQLException {
        int offset = (pageNum - 1) * limitMessagesInPage + off + getCountOfNextMessagesInChat(messageId, chatId);
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp DESC limit ?,?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limitMessagesInPage);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setMessageId(resultSet.getInt("messageId"));
                    message.setTimestamp(resultSet.getLong("timestamp"));
                    message.setUserId(resultSet.getString("userId"));
                    message.setChatId(resultSet.getInt("chatId"));
                    message.setText(resultSet.getString("text"));
                    result.add(message);
                }
                Collections.reverse(result);
                return result;
            }
        }
    }

    private int getCountOfNextMessagesInChat(int messageId, int chatId) throws SQLException {
        String sql1 = "SELECT count(*) FROM messages WHERE chatId = ? AND  messageId > ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.setInt(1, chatId);
            pstmt.setInt(2, messageId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public List<Message> getMessagesByChatId(int chatId, int pageNum) throws SQLException {
        int offset = (pageNum - 1) * limitMessagesInPage;
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp DESC limit ?,?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limitMessagesInPage);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setMessageId(resultSet.getInt("messageId"));
                    message.setTimestamp(resultSet.getLong("timestamp"));
                    message.setUserId(resultSet.getString("userId"));
                    message.setChatId(resultSet.getInt("chatId"));
                    message.setText(resultSet.getString("text"));
                    result.add(message);
                }
                Collections.reverse(result);
                return result;
            }
        }
    }

    public List<Message> getMessagesByChatId(int chatId) throws SQLException {
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setMessageId(resultSet.getInt("messageId"));
                    message.setTimestamp(resultSet.getLong("timestamp"));
                    message.setUserId(resultSet.getString("userId"));
                    message.setChatId(resultSet.getInt("chatId"));
                    message.setText(resultSet.getString("text"));
                    result.add(message);
                }
                return result;
            }
        }
    }

    public int insertMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages(messageId, text, timestamp, userId, chatId) VALUES($next_messageId,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(2, message.getText());
            preparedStatement.setLong(3, message.getTimestamp());
            preparedStatement.setString(4, message.getUserId());
            preparedStatement.setInt(5, message.getChatId());
            preparedStatement.executeUpdate();
        }
        return getIdOfLastAddMessage();
    }

    private int getIdOfLastAddMessage() throws SQLException {
        String sql2 = "SELECT messageId FROM messages WHERE rowid=last_insert_rowid();";
        try (PreparedStatement statement = connection.prepareStatement(sql2)) {
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("messageId");
        }
    }


    public List<Integer> getChatsWithNewMessagesByUserId(User user, List<Chat> chats) throws SQLException {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT count(messageId) FROM messages WHERE chatId = ? AND timestamp >= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Chat chat : chats) {
                pstmt.setInt(1, chat.getChatId());
                pstmt.setLong(2, user.getLastVisit());
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        if (resultSet.getInt(1) != 0) {
                            result.add(chat.getChatId());
                        }
                    }
                }

            }
            return result;
        }

    }
}
