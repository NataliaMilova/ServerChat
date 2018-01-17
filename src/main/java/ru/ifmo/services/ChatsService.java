package ru.ifmo.services;


import ru.ifmo.entity.Chat;

import javax.sql.PooledConnection;
import java.sql.*;


public class ChatsService {

    private PooledConnection pc;

    public ChatsService(PooledConnection pc) {
        this.pc = pc;
    }

    public void deleteChat(int chatId) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "DELETE FROM chats WHERE chatId = ?";
            try(PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, chatId);
                pstmt.executeUpdate();
            }
        }
    }

    public Boolean checkOfChatExistence(int chatId) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "SELECT * FROM chats WHERE chatId = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, chatId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    return resultSet.next();
                }
            }
        }
    }

    public Chat getChatById(int chatId) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
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


    public int insertChat(String chatName) throws SQLException {
        try (Connection con = this.pc.getConnection()) {
            String sql = "INSERT INTO chats(chatId, chatName) VALUES($next_chatId,?);";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(2, chatName);
                preparedStatement.executeUpdate();
            }
           return getIdOfLastAddChat(con);
        }

    }

    private int getIdOfLastAddChat(Connection con) throws SQLException {
        String sql2 = "SELECT chatId FROM chats WHERE rowid=last_insert_rowid();";
        try (PreparedStatement statement = con.prepareStatement(sql2)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("chatId");
            }
        }
    }
}
