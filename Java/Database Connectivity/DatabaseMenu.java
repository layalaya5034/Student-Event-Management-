package com.smartcampus.database;

import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMenu {

    public static void showMenu() {
        DualLogger.setModule("DatabaseManagement");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                    DATABASE MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Test Database Connection");
            DualLogger.println("2.  View Database Summary");
            DualLogger.println("3.  View Student Records");
            DualLogger.println("4.  View Event Records");
            DualLogger.println("5.  View Registration Records");
            DualLogger.println("6.  View Attendance Records");
            DualLogger.println("7.  View Participation Records");
            DualLogger.println("8.  View Database Relationships");
            DualLogger.println("9.  View Event Statistics");
            DualLogger.println("10. View Department Statistics");
            DualLogger.println("11. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 11);
            if (choice == 11) {
                DualLogger.println("[INFO] Returning to Main Menu...");
                DualLogger.setModule("SmartCampusEventManagement");
                break;
            }

            executeOption(choice);
        }
    }

    private static void executeOption(int choice) {
        try (Connection conn = DatabaseManager.getConnection()) {
            switch (choice) {
                case 1:
                    testConnection(conn);
                    break;
                case 2:
                    viewDatabaseSummary(conn);
                    break;
                case 3:
                    viewRawRecords(conn, "students", 15);
                    break;
                case 4:
                    viewRawRecords(conn, "events", 15);
                    break;
                case 5:
                    viewRawRecords(conn, "registrations", 15);
                    break;
                case 6:
                    viewRawRecords(conn, "attendance", 15);
                    break;
                case 7:
                    viewRawRecords(conn, "participations", 15);
                    break;
                case 8:
                    viewRelationships(conn);
                    break;
                case 9:
                    viewEventStats(conn);
                    break;
                case 10:
                    viewDepartmentStats(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void testConnection(Connection conn) throws SQLException {
        DualLogger.subHeader("TEST DATABASE CONNECTION");
        if (conn != null && !conn.isClosed()) {
            DatabaseMetaData meta = conn.getMetaData();
            DualLogger.success("Database connection established successfully!");
            DualLogger.println("Database Product : " + meta.getDatabaseProductName());
            DualLogger.println("Product Version  : " + meta.getDatabaseProductVersion());
            DualLogger.println("Driver Name      : " + meta.getDriverName());
            DualLogger.println("Driver Version   : " + meta.getDriverVersion());
            DualLogger.println("Database URL     : " + meta.getURL());
            DualLogger.println("Database File    : " + DatabaseManager.getDatabasePath());
        } else {
            DualLogger.error("Database connection failed or closed.");
        }
    }

    private static void viewDatabaseSummary(Connection conn) throws SQLException {
        DualLogger.subHeader("DATABASE SUMMARY & STORAGE");
        String dbPath = DatabaseManager.getDatabasePath();
        File dbFile = new File(dbPath);
        long sizeBytes = dbFile.exists() ? dbFile.length() : 0;
        double sizeKb = sizeBytes / 1024.0;

        DualLogger.printf("%-30s : %s\n", "Database File Location", dbPath);
        DualLogger.printf("%-30s : %.2f KB (%d bytes)\n", "Physical File Size", sizeKb, sizeBytes);
        DualLogger.println("------------------------------------------------------------------");
        DualLogger.printf("%-20s %-15s\n", "Table / Entity", "Total Records");
        DualLogger.println("------------------------------------------------------------------");

        String[] tables = {"students", "events", "registrations", "attendance", "participations"};
        for (String t : tables) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + t);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DualLogger.printf("%-20s %-15d\n", t, rs.getInt(1));
                }
            }
        }
        DualLogger.println("------------------------------------------------------------------");
    }

    private static void viewRawRecords(Connection conn, String table, int limit) throws SQLException {
        DualLogger.subHeader("RAW RECORDS: " + table.toUpperCase() + " (First " + limit + ")");
        String sql = "SELECT * FROM " + table + " LIMIT " + limit;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            java.sql.ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                DualLogger.printf("%-18s ", md.getColumnName(i));
            }
            DualLogger.println();
            DualLogger.println("-".repeat(Math.min(18 * cols, 100)));

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String val = rs.getString(i);
                    if (val != null && val.length() > 16) val = val.substring(0, 14) + "..";
                    DualLogger.printf("%-18s ", val != null ? val : "NULL");
                }
                DualLogger.println();
            }
        }
    }

    private static void viewRelationships(Connection conn) throws SQLException {
        DualLogger.subHeader("DATABASE TABLE RELATIONSHIPS & CONSTRAINTS");
        DualLogger.println("1. STUDENTS -> REGISTRATIONS   : 1 to N (Foreign Key: student_id ON UPDATE CASCADE)");
        DualLogger.println("2. EVENTS -> REGISTRATIONS     : 1 to N (Foreign Key: event_id ON UPDATE CASCADE)");
        DualLogger.println("3. REGISTRATIONS               : UNIQUE(student_id, event_id)");
        DualLogger.println("4. REGISTRATIONS -> ATTENDANCE : 1 to 1 (Foreign Key: registration_id [UNIQUE])");
        DualLogger.println("5. REGISTRATIONS -> PARTICIPATION: 1 to 1 (Foreign Key: registration_id [UNIQUE])");
        DualLogger.println("--------------------------------------------------------------------------------");
        DualLogger.println("Foreign Keys Enabled           : PRAGMA foreign_keys = ON (Active & Enforced)");
        DualLogger.println("Transaction Mode               : Atomic ACID Compliant with rollback support");
    }

    private static void viewEventStats(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT METRICS & STATS");
        String sql = "SELECT event_status, COUNT(*) as cnt, SUM(max_participants) as cap FROM events GROUP BY event_status";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-15s %-12s %-15s\n", "Status", "Events Count", "Total Capacity");
            DualLogger.println("-----------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-15s %-12d %-15d\n", rs.getString("event_status"), rs.getInt("cnt"), rs.getInt("cap"));
            }
        }
    }

    private static void viewDepartmentStats(Connection conn) throws SQLException {
        DualLogger.subHeader("DEPARTMENT METRICS & STATS");
        String sql = "SELECT * FROM v_department_summary ORDER BY total_students DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-30s %-10s %-15s %-15s\n", "Department", "Students", "Registrations", "Participations");
            DualLogger.println("------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-30s %-10d %-15d %-15d\n",
                    rs.getString("department"),
                    rs.getInt("total_students"),
                    rs.getInt("total_registrations"),
                    rs.getInt("total_participations"));
            }
        }
    }
}
