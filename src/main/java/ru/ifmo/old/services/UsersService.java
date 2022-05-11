package ru.ifmo.old.services;

import ru.ifmo.old.entity.User;

import java.sql.*;

public class UsersService {

    private Connection connection;

    public UsersService(Connection connection) {
        this.connection = connection;
    }

    public boolean checkOfUsersExistence(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public User getUserById(String userId) throws SQLException {
        User user = new User();
        String sql = "SELECT * FROM users WHERE userId = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    user.setUserId(resultSet.getString("userId"));
                    user.setLastVisit(resultSet.getLong("lastVisit"));
                    user.setNickname(resultSet.getString("nickname"));
                    user.setPassword(resultSet.getString("password"));
                }
                return user;
            }
        }
    }

    public void deleteUserById(String userId) {
        String sql = "DELETE FROM users WHERE userId = ?";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, userId);
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

    public void insertUser(User user) {
        String sql = "INSERT INTO users(userId, nickName, password, lastVisit) VALUES(?,?,?,?)";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNickname());
            pstmt.setString(3, user.getPassword());
            pstmt.setLong(4, 0);
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

    public void updateUserLastVisit(User user) {
        String sql = "UPDATE users SET lastVisit = ? WHERE userId = ?;";
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, user.getLastVisit());
            pstmt.setString(2, user.getUserId());
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

}
