package ru.ifmo.services;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ChatsService {

    public Set<Integer> getAllChatsId(Connection connection) {
        Set<Integer> resultSet = new HashSet<>();
        try {
            String sql = "SELECT chatId FROM chats;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next())
                resultSet.add(result.getInt("chatId"));
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void insertChat(String chatName, Connection connection){
        try {
            String sql = "INSERT INTO chats(chatId, chatName) VALUES($next_chatId,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(2, chatName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
