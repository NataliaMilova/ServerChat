package ru.ifmo.services;


import ru.ifmo.entity.Chat;

import java.sql.*;


public class ChatsService {


    public void deleteChat(int chatId, Connection connection) throws SQLException {
        try (Connection con = connection) {
            String sql = "DELETE FROM chats WHERE chatId = ?";
            try(PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, chatId);
                pstmt.executeUpdate();
            }
        }
    }

    public Boolean checkOfChatExistence(int chatId, Connection connection) throws SQLException {
        try (Connection con = connection) {
            String sql = "SELECT * FROM chats WHERE chatId = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, chatId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    return resultSet.next();
                }
            }
        }
    }

    public Chat getChatById(int chatId, Connection connection) throws SQLException {
        try (Connection con = connection) {
            Chat chat = new Chat();
            String sql = "SELECT * FROM chats WHERE chatId = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
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
    }


    public int insertChat(String chatName, Connection connection) throws SQLException {
        try (Connection con = connection) {
            String sql = "INSERT INTO chats(chatId, chatName) VALUES($next_chatId,?);";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(2, chatName);
                preparedStatement.executeUpdate();
            }
           return getIdOfLastAddChat(connection);
        }

    }

    private int getIdOfLastAddChat(Connection connection) throws SQLException {
        String sql2 = "SELECT chatId FROM chats WHERE rowid=last_insert_rowid();";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql2)) {
                resultSet.next();
                return resultSet.getInt("chatId");
            }
        }
    }
}
