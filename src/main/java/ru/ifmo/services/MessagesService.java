package ru.ifmo.services;

import ru.ifmo.entity.Message;

import java.sql.*;
import java.util.*;

public class MessagesService {

    public List<Message> getMessagesByChatId(int chatId, Connection connection) throws SQLException {
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chatId = " + "'" + chatId + "'"+"ORDER BY timestamp;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            Message message = new Message();
            message.setMessageId(resultSet.getInt("messageId"));
            message.setTimestamp(resultSet.getLong("timestamp"));
            message.setUserId(resultSet.getString("userId"));
            message.setChatId(resultSet.getInt("chatId"));
            message.setText(resultSet.getString("text"));
            result.add(message);
        }
        statement.close();
        connection.close();
        return result;
    }

    public boolean insertMessage(Message message, Connection connection) throws SQLException {
        String sql = "INSERT INTO messages(messageId, text, timestamp, userId, chatId) VALUES($next_messageId,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(2, message.getText());
        preparedStatement.setLong(3, message.getTimestamp());
        preparedStatement.setString(4, message.getUserId());
        preparedStatement.setInt(5, message.getChatId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
        return true;
    }

}
