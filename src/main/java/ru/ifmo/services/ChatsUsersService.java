package ru.ifmo.services;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ChatsUsersService {

    public Set<Integer> getChatsIdByUserId(String userId, Connection connection) throws SQLException {
        Set<Integer> result = new HashSet<>();
        String sql = "SELECT chatId FROM chats_users WHERE userId = " + "'" + userId + "'" + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
            result.add(resultSet.getInt("chatId"));
        statement.close();
        connection.close();
        return result;
    }

    public Set<String> getUsersByChatId(int chatId, Connection connection) throws SQLException {
        Set<String> result = new HashSet<>();
        String sql = "SELECT userId FROM chats_users WHERE chatId = " + "'" + chatId + "'"+";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
            result.add(resultSet.getString("userId"));
        statement.close();
        connection.close();
        return result;
    }

    public void insertChatsUsers(String userId, int chatId, Connection connection){
        try {
            String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, chatId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }
}
