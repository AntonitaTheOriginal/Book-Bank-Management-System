package com.bbms.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing database connections.
 */
public class DBConnection {

    private static Properties props = new Properties();
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream is = DBConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            props.load(is);
            driver   = props.getProperty("db.driver");
            url      = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load DB configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a new database connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes a connection safely.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
