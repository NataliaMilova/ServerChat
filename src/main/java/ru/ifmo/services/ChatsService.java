package ru.ifmo.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.entity.Chat;

import java.sql.*;



public class ChatsService {

    private Connection connection;

    public ChatsService(Connection connection) {
        this.connection = connection;
    }

    public void deleteChat(int chatId) throws SQLException {
        String sql = "DELETE FROM chats WHERE chatId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            pstmt.executeUpdate();
        }
    }

    public Boolean checkOfChatExistence(int chatId) throws SQLException {
        String sql = "SELECT * FROM chats WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public Chat getChatById(int chatId) throws SQLException {
        Chat chat = new Chat();
        String sql = "SELECT * FROM chats WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    chat.setChatId(resultSet.getInt("chatId"));
                    chat.setChatName(resultSet.getString("chatName"));
                }
                return chat;
            }
        }
    }


    public int insertChat(String chatName) throws SQLException {
        String sql = "INSERT INTO chats(chatId, chatName) VALUES($next_chatId,?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(2, chatName);
            preparedStatement.executeUpdate();
        }
        return getIdOfLastAddChat();
    }

    private int getIdOfLastAddChat() throws SQLException {
        String sql2 = "SELECT chatId FROM chats WHERE rowid=last_insert_rowid();";
        try (PreparedStatement statement = connection.prepareStatement(sql2)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("chatId");
            }
        }
    }
}
