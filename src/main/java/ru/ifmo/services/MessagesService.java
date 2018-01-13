package ru.ifmo.services;

import ru.ifmo.entity.Message;

import java.sql.*;
import java.util.*;

public class MessagesService {

    private int limitMessagesInPage;

    public MessagesService(int limitMessagesInPage) {
        this.limitMessagesInPage = limitMessagesInPage;
    }

    /*public int countOfPagesWithMessagesByChatId(int chatId, Connection connection) throws SQLException {
        try (Connection con = connection) {
            int countOfMessages;
            String sql = "SELECT count(*) FROM messages WHERE chatId = ? ;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)){
                pstmt.setInt(1,chatId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    resultSet.next();
                    countOfMessages = resultSet.getInt("count(*)");
                    int result = (countOfMessages / limitMessagesInPage) + 1;
                    return result;
                }
            }
        }
    }*/

    public List<Message> getMessagesByChatId(int chatId, int pageNum, Connection connection) throws SQLException {
        try (Connection con = connection) {
            int offset = (pageNum -1) * limitMessagesInPage;
            List<Message> result = new ArrayList<>();
            String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp limit ?,?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
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
                    return result;
                }
            }
        }
    }

    public List<Message> getMessagesByChatId(int chatId, Connection connection) throws SQLException {
        try (Connection con = connection) {
            List<Message> result = new ArrayList<>();
            String sql = "SELECT * FROM messages WHERE chatId = ? ORDER BY timestamp;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
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
    }

    public boolean insertMessage(Message message, Connection connection) throws SQLException {
        try (Connection con = connection) {
            String sql = "INSERT INTO messages(messageId, text, timestamp, userId, chatId) VALUES($next_messageId,?,?,?,?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(2, message.getText());
                preparedStatement.setLong(3, message.getTimestamp());
                preparedStatement.setString(4, message.getUserId());
                preparedStatement.setInt(5, message.getChatId());
                preparedStatement.executeUpdate();
                return true;
            }
        }
    }

}
