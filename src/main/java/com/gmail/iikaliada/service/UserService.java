package com.gmail.iikaliada.service;

import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.exception.ErrorException;
import com.gmail.iikaliada.model.User;
import com.gmail.iikaliada.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;

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

    public int addUser(Message inputMessage) throws SQLException {
        int userId;
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                User user = createUser(inputMessage);
                userId = userRepository.addUser(user);
                conn.commit();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                conn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e.getMessage(), e);
        }
        return userId;
    }

    private User createUser(Message inputMessage) {
        User user = new User();
        String name = inputMessage.getFrom().getFirstName();
        String lastName = inputMessage.getFrom().getLastName();
        String userName = inputMessage.getFrom().getUserName();
        user.setName(name != null ? name : "");
        user.setLastname(lastName != null ? lastName : "");
        user.setUsername(userName != null ? userName : "");
        user.setUserId(inputMessage.getFrom().getId().toString());
        user.setRoleId(3);//стандартная роль 'User'
        user.setKicked(0);//0 - пользователь активен, 1 - пользователь был кикнут из чата
        return user;
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

    public void deleteUser(String userId) {
        try (Connection conn = connection.getConnection()) {
            try {
                conn.setAutoCommit(false);
                userRepository.deleteUser(userId);
                conn.commit();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                conn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
    }

    public void kickUser(String userId) {
        try (Connection conn = connection.getConnection()) {
            try {
                conn.setAutoCommit(false);
                userRepository.kickUser(userId);
                conn.commit();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                conn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
    }

    public int getKicked(String userId) {
        int kicked;
        try (Connection conn = connection.getConnection()) {
            kicked = userRepository.getKicked(userId);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return kicked;
    }
}
