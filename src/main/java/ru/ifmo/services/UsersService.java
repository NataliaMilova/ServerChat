package ru.ifmo.services;

import ru.ifmo.entity.User;

import java.sql.*;

public class UsersService {

    public User getUserById(String userId, Connection connection){
        User user = new User();
        try {
            String sql = "SELECT * FROM users WHERE userId = " + "'" + userId + "'"+";";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                user.setUserId(resultSet.getString("userId"));
                user.setLastVisit(resultSet.getLong("lastVisit"));
                user.setNickname(resultSet.getString("nickname"));
                user.setPassword(resultSet.getString("password"));
            }
            statement.close();
            connection.close();
            while (resultSet.next())
                System.out.println(resultSet.getInt("nickname"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void insertUser(User user, Connection connection){
        try {
            String sql = "INSERT INTO users(userId, nickName, password) VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserLastVisit(User user, Connection connection){
        try {
            String sql = "UPDATE users SET lastVisit = ? WHERE userId = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getLastVisit());
            preparedStatement.setString(2, user.getUserId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
