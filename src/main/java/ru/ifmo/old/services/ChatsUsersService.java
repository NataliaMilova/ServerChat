package ru.ifmo.old.services;


import org.json.simple.JSONObject;
import ru.ifmo.old.entity.Chat;

import java.sql.*;
import java.util.*;

public class ChatsUsersService {

    private Connection connection;

    public ChatsUsersService(Connection connection) {
        this.connection = connection;
    }

    public List<Chat> getChatsByUserId(String userId) throws SQLException {
        List<Chat> result = new ArrayList<>();
        String sql = "SELECT chatId,chatName FROM chats_users NATURAL JOIN chats WHERE userId = ? ORDER BY chatId DESC;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Chat chat = new Chat();
                    chat.setChatId(resultSet.getInt("chatId"));
                    chat.setChatName(resultSet.getString("chatName"));
                    result.add(chat);
                }
                return result;
            }
        }
    }

    public List<String> getUsersIdByChatId(long chatId) throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT userId FROM chats_users WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next())
                    result.add(resultSet.getString("userId"));
                return result;
            }
        }
    }

    public void outUserFromChat(long chatId, String userId) {
        String sql = "DELETE FROM chats_users WHERE chatId = ? AND userId = ?";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, chatId);
            pstmt.setString(2, userId);
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

    public boolean insertChatsUsers(String userId, long chatId) {
        String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, chatId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException e1) {
                return false;
            }
            return false;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {

            }
        }
        return true;
    }

    public boolean insertChatsUsers(Iterator<JSONObject> userId, long chatId) {
        boolean result;
        String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            while (userId.hasNext()) {
                pstmt.setLong(1, chatId);
                pstmt.setString(2, (String) userId.next().get("userId"));
                pstmt.executeUpdate();
            }
            connection.commit();
            result = true;
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            result = false;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
