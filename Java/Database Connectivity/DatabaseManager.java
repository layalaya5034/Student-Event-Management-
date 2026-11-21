package com.smartcampus.database;

import com.smartcampus.util.DualLogger;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager provides centralized JDBC connection management,
 * transaction coordination, and SQLite database discovery.
 */
public class DatabaseManager {
    private static Connection connection = null;
    private static String dbPath = null;

    static {
        findAndSetDatabasePath();
    }

    private static void findAndSetDatabasePath() {
        String[] candidates = {
            "DBMS/smart_campus_events.db",
            "../DBMS/smart_campus_events.db",
            "../../DBMS/smart_campus_events.db",
            "smart_campus_events.db",
            "C:\\Student event management\\DBMS\\smart_campus_events.db"
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists()) {
                try {
                    dbPath = f.getCanonicalPath();
                    return;
                } catch (Exception e) {
                    dbPath = f.getAbsolutePath();
                    return;
                }
            }
        }
        // If not found, default to canonical DBMS folder
        File def = new File("C:\\Student event management\\DBMS\\smart_campus_events.db");
        dbPath = def.getAbsolutePath();
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            DualLogger.error("SQLite JDBC Driver not found on classpath!");
            throw new SQLException("SQLite Driver missing", e);
        }

        String jdbcUrl = "jdbc:sqlite:" + dbPath;
        connection = DriverManager.getConnection(jdbcUrl);
        
        // Enforce Foreign Key integrity
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        
        return connection;
    }

    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }

    public static void commit(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                DualLogger.error("Error during transaction rollback: " + e.getMessage());
            }
        }
    }

    public static String getDatabasePath() {
        return dbPath;
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            DualLogger.error("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
