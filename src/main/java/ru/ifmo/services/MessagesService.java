package ru.ifmo.services;

import ru.ifmo.entity.Chat;
import ru.ifmo.entity.Message;
import ru.ifmo.entity.User;


import java.sql.*;
import java.util.*;

public class MessagesService {

    private Connection connection;

    private int limitMessagesInPage;

    public MessagesService(Connection connection, int limitMessagesInPage) {
        this.connection = connection;
        this.limitMessagesInPage = limitMessagesInPage;
    }

    public Message getMessageById(long messageId) throws SQLException {
        Message message = new Message();
        String sql = "SELECT * FROM messages WHERE messageId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, messageId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()){
                    message.setMessageId(resultSet.getInt("messageId"));
                    message.setTimestamp(resultSet.getLong("timestamp"));
                    message.setText(resultSet.getString("text"));
                    message.setChatId(resultSet.getInt("chatId"));
                    message.setUserId(resultSet.getString("userId"));
                }
                return message;
            }
        }
    }

    public List<Message> getMessagesByChatId(long chatId, int pageNum, int messageId, int off) throws SQLException {
        int offset = (pageNum - 1) * limitMessagesInPage + off + getCountOfNextMessagesInChat(messageId, chatId);
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp DESC limit ?,?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
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

    private int getCountOfNextMessagesInChat(int messageId, long chatId) throws SQLException {
        String sql1 = "SELECT count(*) FROM messages WHERE chatId = ? AND  messageId > ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.setLong(1, chatId);
            pstmt.setInt(2, messageId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public List<Message> getMessagesByChatId(long chatId, int pageNum) throws SQLException {
        int offset = (pageNum - 1) * limitMessagesInPage;
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp DESC limit ?,?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
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

    public List<Message> getMessagesByChatId(long chatId) throws SQLException {
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
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

    public long insertMessage(Message message) {
        String sql = "INSERT INTO messages(timestamp, text, userId, chatId) VALUES(?,?,?,?)";
        PreparedStatement pstmt = null;
        long messageId = -1;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, message.getText());
            pstmt.setString(3, message.getUserId());
            pstmt.setLong(4, message.getChatId());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                messageId = rs.getInt(1);
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return messageId;
    }

    public List<Long> getChatsWithNewMessagesByUserId(User user, List<Chat> chats) throws SQLException {
        List<Long> result = new ArrayList<>();
        String sql = "SELECT count(messageId) FROM messages WHERE chatId = ? AND timestamp >= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Chat chat : chats) {
                pstmt.setLong(1, chat.getChatId());
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

    public void deleteMessageById(long messageId) {
        String sql = "DELETE FROM messages WHERE messageId = ?";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, messageId);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
