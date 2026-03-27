package com.bbms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;

public class TestDBConnection {
    public static void main(String[] args) {
        System.out.println("--- BBMS Database Connection Test ---");
        Properties props = new Properties();
        try (InputStream is = TestDBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is == null) {
                System.err.println("Error: database.properties not found on classpath.");
                return;
            }
            props.load(is);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            System.out.println("Attempting to load driver: " + driver);
            Class.forName(driver);

            System.out.println("Attempting to connect to: " + url);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                System.out.println("SUCCESS: Connected to database!");
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                    System.out.println("Tables found in database:");
                    while (rs.next()) {
                        System.out.println(" - " + rs.getString(1));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("ERROR: Connection failed!");
            System.err.println("Message: " + e.getMessage());
            System.err.println("Vendor Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
