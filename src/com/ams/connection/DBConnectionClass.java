package com.ams.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;

public class DBConnectionClass {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "1234");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found.");
        }
    }

   
    }

