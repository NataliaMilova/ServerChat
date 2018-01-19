package ru.ifmo.services;


import ru.ifmo.entity.Chat;

import java.sql.*;



public class ChatsService {

    private Connection connection;

    public ChatsService(Connection connection) {
        this.connection = connection;
    }

    public void deleteChat(long chatId) {
        String sql = "DELETE FROM chats WHERE chatId = ?";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, chatId);
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

    public Boolean checkOfChatExistence(long chatId) throws SQLException {
        String sql = "SELECT * FROM chats WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public Chat getChatById(long chatId) throws SQLException {
        Chat chat = new Chat();
        String sql = "SELECT * FROM chats WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    chat.setChatId(resultSet.getInt("chatId"));
                    chat.setChatName(resultSet.getString("chatName"));
                }
                return chat;
            }
        }
    }

    public long insertChat(String chatName) {
        String sql = "INSERT INTO chats(chatName) VALUES(?);";
        PreparedStatement pstmt = null;
        long chatId = -1;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, chatName);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                chatId = rs.getInt(1);
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
        return chatId;
    }

}
