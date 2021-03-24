package com.gmail.iikaliada.repository;

import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.exception.ErrorException;
import com.gmail.iikaliada.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                users.add(user);
            }
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
            throw new ErrorException(sqlException.getMessage(), sqlException);
        }
        logger.info(users);
        return users;
    }

    public int addUser(User user) {
        int userId = -1;
        String query = "INSERT INTO users (Name, Lastname, Username, userId, roleId, kicked) " +
                "values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLastname());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getUserId());
            statement.setInt(5, user.getRoleId());
            statement.setInt(6, user.getKicked());
            int id = statement.executeUpdate();
            if (id > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                    logger.info("User " + user.getUsername() + " was added to database");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return userId;
    }

    public User getUserById(String userId) {
        logger.info("trying to get user... " + userId);
        User user = null;
        String query = "SELECT * FROM users WHERE userId = ?";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("Name"));
                user.setLastname(resultSet.getString("Lastname"));
                user.setUsername(resultSet.getString("Username"));
                user.setUserId(resultSet.getString("userId"));
                user.setRoleId(resultSet.getInt("roleId"));
                user.setKicked(resultSet.getInt("kicked"));
            }
            logger.info(user);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return user;
    }

    public void deleteUser(String userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e.getMessage(), e);
        }
    }

    public void kickUser(String userId) throws SQLException {
        String query = "UPDATE users set kicked = 1 WHERE userId = ?";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e.getMessage(), e);
        }
    }

    public int getKicked(String userId) {
        logger.info("trying to get kick user... " + userId);
        int kicked = 0;
        String query = "SELECT kicked FROM users WHERE userId = ?";
        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                kicked = resultSet.getInt("kicked");
            }
            logger.info("Kicked = " + kicked);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ErrorException(e.getMessage(), e);
        }
        return kicked;
    }
}
