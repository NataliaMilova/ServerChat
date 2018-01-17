package ru.ifmo.services;


import org.json.simple.JSONObject;
import ru.ifmo.entity.Chat;

import javax.sql.PooledConnection;
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

    public Set<String> getUsersIdByChatId(int chatId) throws SQLException {
        Set<String> result = new HashSet<>();
        String sql = "SELECT userId FROM chats_users WHERE chatId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next())
                    result.add(resultSet.getString("userId"));
                return result;
            }
        }
    }

    public void outUserFromChat(int chatId, String userId) throws SQLException {
        String sql = "DELETE FROM chats_users WHERE chatId = ? AND userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        }
    }

    public boolean insertChatsUsers(String userId, int chatId) throws SQLException {
        String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
            return true;
        }
    }

    public boolean insertChatsUsers(Iterator<JSONObject> userId, int chatId) {
        boolean result;
        String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            while (userId.hasNext()) {
                pstmt.setInt(1, chatId);
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
