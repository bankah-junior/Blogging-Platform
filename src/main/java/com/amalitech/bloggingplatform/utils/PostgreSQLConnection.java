package com.amalitech.bloggingplatform.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection {

    // Database connection parameters
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/blogging_platform_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    // Instance for connection management
    private static PostgreSQLConnection instance;
    private Connection connection;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private PostgreSQLConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of PostgreSQLConnection.
     * Follows Singleton pattern for resource management.
     *
     * @return PostgreSQLConnection instance
     */
    public static synchronized PostgreSQLConnection getInstance() {
        if (instance == null) {
            instance = new PostgreSQLConnection();
        }
        return instance;
    }

    /**
     * Establishes a connection to the PostgreSQL database.
     * Uses connection pooling principles for efficient resource management.
     *
     * @return Connection object to the database
     * @throws SQLException if database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");

                // Set connection properties for optimal performance
                Properties props = new Properties();
                props.setProperty("user", DB_USER);
                props.setProperty("password", DB_PASSWORD);
                props.setProperty("ssl", "false");

                // Establish connection
                connection = DriverManager.getConnection(DB_URL, props);

                // Enable auto-commit for transaction management
                connection.setAutoCommit(true);

                System.out.println("Database connection established successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection.
     * Properly releases database resources.
     *
     * @throws SQLException if database access error occurs
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }

    /**
     * Tests the database connection.
     * Useful for connection validation during application startup.
     *
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
