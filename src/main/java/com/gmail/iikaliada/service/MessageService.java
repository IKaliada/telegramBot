package com.gmail.iikaliada.service;

import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.exception.ErrorException;
import com.gmail.iikaliada.model.UserMessage;
import com.gmail.iikaliada.repository.MessageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class MessageService {
    private Logger logger = LogManager.getLogger(MessageService.class);
    private MessageRepository messageRepository;
    private DatabaseConnection connection;

    public MessageService(DatabaseConnection connection) {
        this.connection = connection;
        messageRepository = new MessageRepository(connection);
    }

    public boolean saveMessage(int userId, String mesasage) throws SQLException {
        boolean isMessageSaved;
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                isMessageSaved = messageRepository.saveMessage(userId, mesasage);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error(e.getMessage(), e);
                throw new SQLException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e.getMessage(), e);
        }
        return isMessageSaved;
    }

    public UserMessage getMesssgeByUserId(int userId) {
        UserMessage userMessage = null;
        try (Connection conn = connection.getConnection()) {
            userMessage = messageRepository.getMesssgeByUserId(userId);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return userMessage;
    }
}
