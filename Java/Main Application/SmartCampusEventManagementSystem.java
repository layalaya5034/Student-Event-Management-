package com.smartcampus.main;

import com.smartcampus.attendance.AttendanceMenu;
import com.smartcampus.dashboard.DashboardMenu;
import com.smartcampus.database.DatabaseManager;
import com.smartcampus.database.DatabaseMenu;
import com.smartcampus.event.EventMenu;
import com.smartcampus.participation.ParticipationMenu;
import com.smartcampus.python.PythonMenu;
import com.smartcampus.registration.RegistrationMenu;
import com.smartcampus.reports.ReportsMenu;
import com.smartcampus.student.StudentMenu;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

/**
 * SMART CAMPUS EVENT MANAGEMENT SYSTEM & STUDENT PARTICIPATION ANALYSIS
 * Scenario 6 | Register Numbers: 6, 36, 66, 96
 * Centralized Main Application Entry Point
 */
public class SmartCampusEventManagementSystem {

    public static void main(String[] args) {
        // Initialize logging
        DualLogger.setModule("SmartCampusEventManagement");
        
        // Check database connection at startup
        if (!DatabaseManager.testConnection()) {
            DualLogger.warning("Could not establish immediate database connection to: " + DatabaseManager.getDatabasePath());
        }

        // Direct module launcher support for BAT files
        if (args != null && args.length > 0) {
            String fullArgs = String.join(" ", args).toLowerCase();
            if (fullArgs.contains("student")) {
                StudentMenu.showMenu();
            } else if (fullArgs.contains("event") && !fullArgs.contains("registration") && !fullArgs.contains("analysis") && !fullArgs.contains("report")) {
                EventMenu.showMenu();
            } else if (fullArgs.contains("registration")) {
                RegistrationMenu.showMenu();
            } else if (fullArgs.contains("attendance")) {
                AttendanceMenu.showMenu();
            } else if (fullArgs.contains("participation")) {
                ParticipationMenu.showMenu();
            } else if (fullArgs.contains("report") || fullArgs.contains("analysis")) {
                ReportsMenu.showMenu();
            } else if (fullArgs.contains("python")) {
                PythonMenu.showMenu();
            } else if (fullArgs.contains("database") || fullArgs.contains("dbms")) {
                DatabaseMenu.showMenu();
            }
        }

        // Centralized Main Menu Loop
        runMainMenu();
    }

    public static void runMainMenu() {
        while (true) {
            DualLogger.setModule("SmartCampusEventManagement");
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("            SMART CAMPUS EVENT MANAGEMENT SYSTEM");
            DualLogger.println("               STUDENT PARTICIPATION ANALYSIS");
            DualLogger.println();
            DualLogger.println("                         MAIN MENU");
            DualLogger.println();
            DualLogger.println("1.  Dashboard");
            DualLogger.println("2.  Student Management");
            DualLogger.println("3.  Event Management");
            DualLogger.println("4.  Event Registration Management");
            DualLogger.println("5.  Attendance Management");
            DualLogger.println("6.  Participation Management");
            DualLogger.println("7.  Event Analysis & Reports");
            DualLogger.println("8.  Python Data Analysis");
            DualLogger.println("9.  Database Management");
            DualLogger.println("10. Exit");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 10);
            DualLogger.println();

            switch (choice) {
                case 1:
                    DashboardMenu.showDashboard();
                    break;
                case 2:
                    StudentMenu.showMenu();
                    break;
                case 3:
                    EventMenu.showMenu();
                    break;
                case 4:
                    RegistrationMenu.showMenu();
                    break;
                case 5:
                    AttendanceMenu.showMenu();
                    break;
                case 6:
                    ParticipationMenu.showMenu();
                    break;
                case 7:
                    ReportsMenu.showMenu();
                    break;
                case 8:
                    PythonMenu.showMenu();
                    break;
                case 9:
                    DatabaseMenu.showMenu();
                    break;
                case 10:
                    DualLogger.println("============================================================");
                    DualLogger.println("Exiting Smart Campus Event Management System. Goodbye!");
                    DualLogger.println("============================================================");
                    System.exit(0);
                    return;
                default:
                    DualLogger.error("Invalid selection. Please choose an option from 1 to 10.");
                    break;
            }
        }
    }
}
