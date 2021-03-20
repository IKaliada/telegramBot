package com.gmail.iikaliada.repository;

import com.gmail.iikaliada.ErrorException;
import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private DatabaseConnection databaseConnection;
    private Logger logger = LogManager.getLogger(UserRepository.class);

    public UserRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<User> getUsers() throws SQLException {
        logger.info("trying to get users");
        List<User> users = new ArrayList<>();
        String userQuery = "SELECT * FROM users";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(userQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getString("userId"));
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("Name"));
                user.setUsername(resultSet.getString("Username"));
                user.setLastname(resultSet.getString("Lastname"));
                user.setMessageId(resultSet.getString("messageId"));
                users.add(user);
            }
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
            throw new ErrorException(sqlException.getMessage(), sqlException);
        }
        logger.info(users);
        return users;
    }

    public boolean addUser(User user) {
        boolean isUserSaved = false;
        String query = "INSERT INTO users (Name, Lastname, Username, userId) " +
                "values (?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLastname());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getUserId());
            int id = statement.executeUpdate();
            if (id > 0) {
                isUserSaved = true;
                logger.info("User " + user.getUsername() + " was added to database");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return isUserSaved;
    }

    public User getUserById(String userId) {
        logger.info("trying to get user... " + userId);
        User user = null;
        String query = "SELECT Name, Lastname, Username, userId FROM users WHERE userId = ?";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setName(resultSet.getString("Name"));
                user.setLastname(resultSet.getString("Lastname"));
                user.setUsername(resultSet.getString("Username"));
                user.setUserId(resultSet.getString("userId"));
            }
            logger.info(user);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return user;
    }
}
