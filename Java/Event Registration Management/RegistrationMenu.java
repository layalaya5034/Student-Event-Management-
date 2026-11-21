package com.smartcampus.registration;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrationMenu {

    public static void showMenu() {
        DualLogger.setModule("EventRegistration");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                EVENT REGISTRATION MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Register Student for Event");
            DualLogger.println("2.  View All Registrations");
            DualLogger.println("3.  Search Registration");
            DualLogger.println("4.  Update Registration Details");
            DualLogger.println("5.  Cancel Registration");
            DualLogger.println("6.  View Event-wise Registrations");
            DualLogger.println("7.  View Student-wise Registrations");
            DualLogger.println("8.  View Today's Registrations");
            DualLogger.println("9.  View Upcoming Event Registrations");
            DualLogger.println("10. View Active Registrations");
            DualLogger.println("11. View Cancelled Registrations");
            DualLogger.println("12. View Waitlisted Registrations");
            DualLogger.println("13. Check Event Availability");
            DualLogger.println("14. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 14);
            if (choice == 14) {
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
                    registerStudentForEvent(conn);
                    break;
                case 2:
                    viewAllRegistrations(conn);
                    break;
                case 3:
                    searchRegistration(conn);
                    break;
                case 4:
                    updateRegistration(conn);
                    break;
                case 5:
                    cancelRegistration(conn);
                    break;
                case 6:
                    viewEventWiseRegistrations(conn);
                    break;
                case 7:
                    viewStudentWiseRegistrations(conn);
                    break;
                case 8:
                    viewTodayRegistrations(conn);
                    break;
                case 9:
                    viewUpcomingEventRegistrations(conn);
                    break;
                case 10:
                    viewByStatus(conn, "REGISTERED");
                    break;
                case 11:
                    viewByStatus(conn, "CANCELLED");
                    break;
                case 12:
                    viewByStatus(conn, "WAITLISTED");
                    break;
                case 13:
                    checkEventAvailability(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void registerStudentForEvent(Connection conn) throws SQLException {
        DualLogger.subHeader("REGISTER STUDENT FOR EVENT");
        String studentId = InputReader.readString("Enter Student ID (e.g. STU006): ", false).toUpperCase();

        // 1. Verify student exists
        String stuName = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT student_name FROM students WHERE student_id = ?")) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Student ID '" + studentId + "' does not exist!");
                return;
            }
            stuName = rs.getString("student_name");
        }
        DualLogger.println("Student Verified: " + stuName);

        // 2. Verify event exists and accepts registrations
        String eventId = InputReader.readString("Enter Event ID (e.g. EVT108): ", false).toUpperCase();
        String eventName = null;
        int maxCap = 0;
        String status = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT event_name, max_participants, event_status FROM events WHERE event_id = ?")) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Event ID '" + eventId + "' does not exist!");
                return;
            }
            eventName = rs.getString("event_name");
            maxCap = rs.getInt("max_participants");
            status = rs.getString("event_status");
        }

        if ("CANCELLED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
            DualLogger.error("Cannot register: Event is already " + status + "!");
            return;
        }

        // 3. Verify student has not already registered
        try (PreparedStatement ps = conn.prepareStatement("SELECT registration_id, status FROM registrations WHERE student_id = ? AND event_id = ?")) {
            ps.setString(1, studentId);
            ps.setString(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String existingStatus = rs.getString("status");
                if ("REGISTERED".equalsIgnoreCase(existingStatus)) {
                    DualLogger.warning("Student '" + studentId + "' is ALREADY actively registered for this event (Registration ID: " + rs.getString("registration_id") + ")!");
                    return;
                } else if ("WAITLISTED".equalsIgnoreCase(existingStatus)) {
                    DualLogger.warning("Student '" + studentId + "' is currently WAITLISTED for this event.");
                    return;
                }
            }
        }

        // 4. Check available capacity
        int activeCount = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status='REGISTERED'")) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) activeCount = rs.getInt(1);
        }

        String assignedStatus = "REGISTERED";
        if (activeCount >= maxCap) {
            DualLogger.warning("Event has reached maximum participant capacity (" + maxCap + "/" + maxCap + ")!");
            String wlChoice = InputReader.readString("Would you like to register on the WAITLIST? (Y/N): ", false);
            if (wlChoice.equalsIgnoreCase("Y")) {
                assignedStatus = "WAITLISTED";
            } else {
                DualLogger.println("[INFO] Registration aborted due to capacity limit.");
                return;
            }
        }

        // 5. Generate Registration ID
        String nextRegId = generateNextRegId(conn);
        String regDate = LocalDate.now().toString();

        // 6. Transactional insert
        try {
            DatabaseManager.beginTransaction(conn);
            String insertSql = "INSERT INTO registrations (registration_id, student_id, event_id, registration_date, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, nextRegId);
                ps.setString(2, studentId);
                ps.setString(3, eventId);
                ps.setString(4, regDate);
                ps.setString(5, assignedStatus);
                ps.executeUpdate();
            }
            DatabaseManager.commit(conn);
            DualLogger.success("Student registered for the event successfully! [Reg ID: " + nextRegId + ", Status: " + assignedStatus + "]");
        } catch (SQLException e) {
            DatabaseManager.rollback(conn);
            DualLogger.error("Failed to commit event registration: " + e.getMessage());
        }
    }

    private static String generateNextRegId(Connection conn) throws SQLException {
        String sql = "SELECT registration_id FROM registrations WHERE registration_id LIKE 'REG%' ORDER BY registration_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("registration_id");
                try {
                    int num = Integer.parseInt(lastId.replaceAll("\\D+", ""));
                    return String.format("REG%04d", num + 1);
                } catch (Exception ignored) {}
            }
        }
        return "REG1201";
    }

    private static void viewAllRegistrations(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT REGISTRATIONS");
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, r.event_id, e.event_name, r.registration_date, r.status " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id ORDER BY r.registration_id DESC LIMIT 30";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-12s %-10s %-20s %-10s %-24s %-12s %-10s\n", "Reg ID", "Student ID", "Student Name", "Event ID", "Event Name", "Reg Date", "Status");
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            int cnt = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-10s %-20s %-10s %-24s %-12s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("registration_date"),
                    rs.getString("status"));
                cnt++;
            }
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            DualLogger.println("Total Registrations displayed: " + cnt);
        }
    }

    private static void searchRegistration(Connection conn) throws SQLException {
        DualLogger.subHeader("SEARCH REGISTRATION");
        String query = InputReader.readString("Enter Registration ID, Student ID, or Event ID: ", false).toUpperCase();
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, r.event_id, e.event_name, r.registration_date, r.status " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id " +
                     "WHERE UPPER(r.registration_id) LIKE ? OR UPPER(r.student_id) LIKE ? OR UPPER(r.event_id) LIKE ? OR UPPER(s.student_name) LIKE ? " +
                     "ORDER BY r.registration_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + query + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    DualLogger.printf("%-12s %-10s %-20s %-10s %-24s %-12s %-10s\n", "Reg ID", "Student ID", "Student Name", "Event ID", "Event Name", "Date", "Status");
                    DualLogger.println("-------------------------------------------------------------------------------------------------");
                    found = true;
                }
                DualLogger.printf("%-12s %-10s %-20s %-10s %-24s %-12s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("registration_date"),
                    rs.getString("status"));
            }
            if (!found) {
                DualLogger.warning("No registration matching '" + query + "' was found.");
            }
        }
    }

    private static void updateRegistration(Connection conn) throws SQLException {
        DualLogger.subHeader("UPDATE REGISTRATION STATUS");
        String regId = InputReader.readString("Enter Registration ID: ", false).toUpperCase();
        String sql = "SELECT status FROM registrations WHERE registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Registration ID not found.");
                return;
            }
            String cur = rs.getString("status");
            DualLogger.println("Current Status: " + cur);
            DualLogger.println("Select New Status:\n1. REGISTERED\n2. CANCELLED\n3. WAITLISTED");
            int choice = InputReader.readInt("Choice (1-3): ", 1, 3);
            String[] st = {"REGISTERED", "CANCELLED", "WAITLISTED"};
            String newSt = st[choice - 1];

            try (PreparedStatement ups = conn.prepareStatement("UPDATE registrations SET status = ? WHERE registration_id = ?")) {
                ups.setString(1, newSt);
                ups.setString(2, regId);
                ups.executeUpdate();
                DualLogger.success("Registration status updated to " + newSt);
            }
        }
    }

    private static void cancelRegistration(Connection conn) throws SQLException {
        DualLogger.subHeader("CANCEL REGISTRATION");
        String regId = InputReader.readString("Enter Registration ID to cancel: ", false).toUpperCase();
        String sql = "SELECT status FROM registrations WHERE registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Registration ID not found.");
                return;
            }
            if ("CANCELLED".equalsIgnoreCase(rs.getString("status"))) {
                DualLogger.warning("Registration is already marked as CANCELLED.");
                return;
            }

            try (PreparedStatement ups = conn.prepareStatement("UPDATE registrations SET status = 'CANCELLED' WHERE registration_id = ?")) {
                ups.setString(1, regId);
                ups.executeUpdate();
                DualLogger.success("Registration " + regId + " cancelled successfully. Historical audit preserved.");
            }
        }
    }

    private static void viewEventWiseRegistrations(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT-WISE REGISTRATIONS");
        String eventId = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, s.department, r.status " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "WHERE r.event_id = ? ORDER BY r.status ASC, s.student_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-12s %-12s %-22s %-25s %-10s\n", "Reg ID", "Student ID", "Student Name", "Department", "Status");
            DualLogger.println("--------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-12s %-22s %-25s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getString("status"));
                c++;
            }
            DualLogger.println("Total registrations for event " + eventId + ": " + c);
        }
    }

    private static void viewStudentWiseRegistrations(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT-WISE REGISTRATIONS");
        String studentId = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT r.registration_id, r.event_id, e.event_name, e.event_date, r.status " +
                     "FROM registrations r JOIN events e ON r.event_id = e.event_id " +
                     "WHERE r.student_id = ? ORDER BY e.event_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-12s %-10s %-26s %-12s %-10s\n", "Reg ID", "Event ID", "Event Name", "Date", "Status");
            DualLogger.println("---------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-10s %-26s %-12s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("event_date"),
                    rs.getString("status"));
                c++;
            }
            DualLogger.println("Total registered events for student " + studentId + ": " + c);
        }
    }

    private static void viewTodayRegistrations(Connection conn) throws SQLException {
        DualLogger.subHeader("TODAY'S REGISTRATIONS");
        String today = LocalDate.now().toString();
        renderRegistrationsByDate(conn, today);
    }

    private static void viewUpcomingEventRegistrations(Connection conn) throws SQLException {
        DualLogger.subHeader("UPCOMING EVENT REGISTRATIONS");
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, e.event_name, e.event_date, r.status " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id " +
                     "WHERE e.event_status IN ('OPEN', 'UPCOMING') ORDER BY e.event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-12s %-10s %-20s %-24s %-12s %-10s\n", "Reg ID", "Student ID", "Student Name", "Event Name", "Date", "Status");
            DualLogger.println("-------------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-10s %-20s %-24s %-12s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("event_date"),
                    rs.getString("status"));
                c++;
            }
            DualLogger.println("Total Upcoming Event Registrations: " + c);
        }
    }

    private static void renderRegistrationsByDate(Connection conn, String date) throws SQLException {
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, e.event_name, r.status " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id WHERE r.registration_date = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-12s %-10s %-20s %-24s %-10s\n", "Reg ID", "Student ID", "Student Name", "Event Name", "Status");
            DualLogger.println("---------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-10s %-20s %-24s %-10s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("status"));
                c++;
            }
            DualLogger.println("Total registrations on " + date + ": " + c);
        }
    }

    private static void viewByStatus(Connection conn, String status) throws SQLException {
        DualLogger.subHeader(status + " REGISTRATIONS");
        String sql = "SELECT r.registration_id, r.student_id, s.student_name, e.event_name, r.registration_date " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id WHERE r.status = ? ORDER BY r.registration_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-12s %-10s %-20s %-24s %-12s\n", "Reg ID", "Student ID", "Student Name", "Event Name", "Reg Date");
            DualLogger.println("--------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-10s %-20s %-24s %-12s\n",
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("registration_date"));
                c++;
            }
            DualLogger.println("Total " + status + " Registrations: " + c);
        }
    }

    private static void checkEventAvailability(Connection conn) throws SQLException {
        DualLogger.subHeader("CHECK EVENT SEAT AVAILABILITY");
        String id = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String sql = "SELECT * FROM v_event_overview WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DualLogger.println("Event ID             : " + rs.getString("event_id"));
                DualLogger.println("Event Name           : " + rs.getString("event_name"));
                DualLogger.println("Status               : " + rs.getString("event_status"));
                DualLogger.println("Maximum Capacity     : " + rs.getInt("max_participants"));
                DualLogger.println("Active Registrations : " + rs.getInt("active_registrations"));
                DualLogger.println("Available Seats      : " + rs.getInt("available_seats"));
                DualLogger.printf("Capacity Utilization : %.2f%%\n", rs.getDouble("capacity_utilization_pct"));
                if (rs.getInt("available_seats") == 0) {
                    DualLogger.warning("This event is FULL! Registrations will be waitlisted.");
                } else {
                    DualLogger.success("Seats are currently available for registration.");
                }
            } else {
                DualLogger.error("Event ID not found.");
            }
        }
    }
}
