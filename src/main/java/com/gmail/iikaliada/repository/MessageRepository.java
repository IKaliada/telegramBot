package com.gmail.iikaliada.repository;

import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.exception.ErrorException;
import com.gmail.iikaliada.model.UserMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageRepository {
    private final Logger logger = LogManager.getLogger(MessageRepository.class);
    private final DatabaseConnection databaseConnection;

    public MessageRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public boolean saveMessage(int userId, String message) throws SQLException {
        String query = "INSERT INTO messages (userId, message) values (?,?)";
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, message);
            int count = preparedStatement.executeUpdate();
            if (count > 0) {
                logger.info("Message '" + message + "' was saved");
            }
            return count > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e.getMessage(), e);
        }
    }

    public UserMessage getMesssgeByUserId(int userId) {
        UserMessage message = null;
        String query = "SELECT message FROM messages where userId = ?";
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                message = new UserMessage();
                message.setMessage(resultSet.getString("message"));
                message.setUserId(userId);
            }
            return message;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
    }
}
