package com.gmail.iikaliada.connection;

import com.gmail.iikaliada.PropUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.gmail.iikaliada.constant.SQLConstant.*;

public class DatabaseConnection {
    private static Logger logger = LogManager.getLogger(DatabaseConnection.class);
    private PropUtil propUtil = PropUtil.getInstance();

    public DatabaseConnection() {
        try {
            Class.forName(propUtil.getProperties(JDBC_DRIVER));
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage() + e);
        }
    }

    public Connection getConnection() {
        logger.info("Connecting to database...");
        Connection connection = null;
        try {
            String url = propUtil.getProperties(JDBC_URL);
            String username = propUtil.getProperties(JDBC_USERNAME);
            String password = propUtil.getProperties(JDBC_PASSWORD);
            connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                logger.info("Connected");
            } else logger.error("Connection failed");
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
        }
        return connection;
    }
}
