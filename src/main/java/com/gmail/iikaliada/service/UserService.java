package com.gmail.iikaliada.service;

import com.gmail.iikaliada.ErrorException;
import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.model.User;
import com.gmail.iikaliada.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private Logger logger = LogManager.getLogger(UserService.class);
    private UserRepository userRepository;
    private DatabaseConnection connection;

    public UserService(DatabaseConnection connection) {
        this.connection = connection;
        userRepository = new UserRepository(connection);
    }

    public List<User> getUsers() {
        List<User> users = null;
        try (Connection con = connection.getConnection()) {
            users = userRepository.getUsers();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return users;
    }

    public boolean addUser(User user) {
        boolean isUserSaved;
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                isUserSaved = userRepository.addUser(user);
                conn.commit();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                conn.rollback();
                throw new ErrorException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return isUserSaved;
    }

    public User getUserById(String userId) {
        User userById = null;
        try (Connection conn = connection.getConnection()) {
            userById = userRepository.getUserById(userId);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return userById;
    }
}
