package ru.ifmo.services;

import ru.ifmo.entity.User;

import javax.sql.PooledConnection;
import java.sql.*;

public class UsersService {

    private PooledConnection pc;

    public UsersService(PooledConnection pc) {
        this.pc = pc;
    }

    public boolean checkOfUsersExistence(String userId) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "SELECT * FROM users WHERE userId = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    return  resultSet.next();
                }
            }
        }
    }

    public User getUserById(String userId) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            User user = new User();
            String sql = "SELECT * FROM users WHERE userId = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
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
    }

    public void insertUser(User user) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "INSERT INTO users(userId, nickName, password, lastVisit) VALUES(?,?,?,?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getUserId());
                preparedStatement.setString(2, user.getNickname());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setLong(4, 0);
                preparedStatement.executeUpdate();
            }
        }
    }

    public void updateUserLastVisit(User user) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "UPDATE users SET lastVisit = ? WHERE userId = ?;";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setLong(1, user.getLastVisit());
                preparedStatement.setString(2, user.getUserId());
                preparedStatement.executeUpdate();
            }
        }
    }
}
