package ru.ifmo.services;

import ru.ifmo.entity.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessagesService {

    public void insertMessage(Message message, Connection connection){
        try {
            String sql = "INSERT INTO messages(messageId, text, timestamp, userId, chatId) VALUES($next_messageId,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(2, message.getText());
            preparedStatement.setLong(3, message.getTimestamp());
            preparedStatement.setString(4, message.getUserId());
            preparedStatement.setInt(5, message.getChatId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
