package ru.ifmo.services;


import ru.ifmo.entity.Chat;

import java.sql.*;


public class ChatsService {

    public void deleteChat(int chatId, Connection connection) throws SQLException {
        String sql = "DELETE FROM chats WHERE chatId = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, chatId);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    public Boolean chekOfChatExistence(int chatId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM chats WHERE chatId = " + "'" + chatId + "'" + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next())
            return true;
        else
            return false;
    }

    public Chat getChatById(int chatId, Connection connection) throws SQLException {
        Chat chat = new Chat();
        String sql = "SELECT * FROM chats WHERE chatId = " + "'" + chatId + "'" + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            chat.setChatId(resultSet.getInt("chatId"));
            chat.setChatName(resultSet.getString("chatName"));
        }
        statement.close();
        connection.close();
        return chat;
    }


    public int insertChat(String chatName, Connection connection) throws SQLException {
        int result = -1;
        String sql = "INSERT INTO chats(chatId, chatName) VALUES($next_chatId,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(2, chatName);
        preparedStatement.executeUpdate();
        String sql2 = "SELECT chatId FROM chats WHERE rowid=last_insert_rowid();";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql2);
        while (resultSet.next()) {
            result = resultSet.getInt("chatId");
        }
        preparedStatement.close();
        connection.close();
        return result;
    }

}
