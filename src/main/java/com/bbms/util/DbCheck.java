package com.bbms.util;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DbCheck {
    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream is = DbCheck.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is == null) {
                System.err.println("Could not find database.properties");
                return;
            }
            props.load(is);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");
            
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT user_id, password_hash FROM users")) {
                
                System.out.println("User ID | Hash Prefix | Length | Hash Content");
                System.out.println("----------------------------------------------");
                while (rs.next()) {
                    String id = rs.getString("user_id");
                    String hash = rs.getString("password_hash");
                    String prefix = (hash != null && hash.length() >= 4) ? hash.substring(0, 4) : "N/A";
                    int len = (hash != null) ? hash.length() : 0;
                    System.out.printf("%-10s | %-11s | %-6d | %s%n", id, prefix, len, hash);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
