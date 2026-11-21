package com.smartcampus.attendance;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AttendanceMenu {

    public static void showMenu() {
        DualLogger.setModule("AttendanceManagement");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                  ATTENDANCE MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Mark Student Attendance");
            DualLogger.println("2.  View All Attendance Records");
            DualLogger.println("3.  Search Attendance Record");
            DualLogger.println("4.  Update Attendance");
            DualLogger.println("5.  Mark Bulk Event Attendance");
            DualLogger.println("6.  View Event-wise Attendance");
            DualLogger.println("7.  View Student-wise Attendance");
            DualLogger.println("8.  View Present Students");
            DualLogger.println("9.  View Absent Students");
            DualLogger.println("10. View Attendance Percentage");
            DualLogger.println("11. View Event Attendance Summary");
            DualLogger.println("12. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 12);
            if (choice == 12) {
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
                    markStudentAttendance(conn);
                    break;
                case 2:
                    viewAllAttendance(conn);
                    break;
                case 3:
                    searchAttendance(conn);
                    break;
                case 4:
                    updateAttendance(conn);
                    break;
                case 5:
                    markBulkAttendance(conn);
                    break;
                case 6:
                    viewEventAttendance(conn);
                    break;
                case 7:
                    viewStudentAttendance(conn);
                    break;
                case 8:
                    viewByStatus(conn, "PRESENT");
                    break;
                case 9:
                    viewByStatus(conn, "ABSENT");
                    break;
                case 10:
                    viewAttendancePercentage(conn);
                    break;
                case 11:
                    viewEventAttendanceSummary(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void markStudentAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("MARK STUDENT ATTENDANCE");
        String eventId = InputReader.readString("Enter Event ID (e.g. EVT101): ", false).toUpperCase();
        String studentId = InputReader.readString("Enter Student ID (e.g. STU006): ", false).toUpperCase();

        // Check registration
        String regId = null;
        String regSql = "SELECT registration_id, status FROM registrations WHERE student_id = ? AND event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(regSql)) {
            ps.setString(1, studentId);
            ps.setString(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Student '" + studentId + "' is NOT registered for event '" + eventId + "'! Attendance cannot be marked.");
                return;
            }
            if (!"REGISTERED".equalsIgnoreCase(rs.getString("status"))) {
                DualLogger.error("Registration for student is '" + rs.getString("status") + "' (not active REGISTERED).");
                return;
            }
            regId = rs.getString("registration_id");
        }

        // Check duplicate attendance
        String checkAtt = "SELECT attendance_id, attendance_status FROM attendance WHERE registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkAtt)) {
            ps.setString(1, regId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DualLogger.warning("Attendance already recorded for this registration (ID: " + rs.getString("attendance_id") + ", Status: " + rs.getString("attendance_status") + ")!");
                DualLogger.println("Use Option 4 (Update Attendance) to modify existing records.");
                return;
            }
        }

        DualLogger.println("Select Status: 1. PRESENT  2. ABSENT");
        int stChoice = InputReader.readInt("Enter choice (1-2): ", 1, 2);
        String attStatus = (stChoice == 1) ? "PRESENT" : "ABSENT";
        String attDate = LocalDate.now().toString();
        String attId = generateNextAttId(conn);

        try {
            DatabaseManager.beginTransaction(conn);
            String insertSql = "INSERT INTO attendance (attendance_id, registration_id, student_id, event_id, attendance_date, attendance_status) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, attId);
                ps.setString(2, regId);
                ps.setString(3, studentId);
                ps.setString(4, eventId);
                ps.setString(5, attDate);
                ps.setString(6, attStatus);
                ps.executeUpdate();
            }
            DatabaseManager.commit(conn);
            DualLogger.success("Attendance recorded successfully! [Attendance ID: " + attId + ", Status: " + attStatus + "]");
        } catch (SQLException e) {
            DatabaseManager.rollback(conn);
            DualLogger.error("Failed to commit attendance: " + e.getMessage());
        }
    }

    private static String generateNextAttId(Connection conn) throws SQLException {
        String sql = "SELECT attendance_id FROM attendance WHERE attendance_id LIKE 'ATT%' ORDER BY attendance_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("attendance_id");
                try {
                    int num = Integer.parseInt(lastId.replaceAll("\\D+", ""));
                    return String.format("ATT%04d", num + 1);
                } catch (Exception ignored) {}
            }
        }
        return "ATT1101";
    }

    private static void viewAllAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("ATTENDANCE DETAILS");
        String sql = "SELECT a.attendance_id, a.registration_id, a.student_id, s.student_name, e.event_name, a.attendance_status, a.attendance_date " +
                     "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                     "JOIN events e ON a.event_id = e.event_id ORDER BY a.attendance_id DESC LIMIT 30";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-12s %-10s %-20s %-24s %-10s %-12s\n", "Att ID", "Reg ID", "Student ID", "Student Name", "Event Name", "Status", "Date");
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-12s %-10s %-20s %-24s %-10s %-12s\n",
                    rs.getString("attendance_id"),
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("attendance_status"),
                    rs.getString("attendance_date"));
                c++;
            }
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            DualLogger.println("Total Attendance Records: " + c);
        }
    }

    private static void searchAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("SEARCH ATTENDANCE RECORD");
        String term = InputReader.readString("Enter Attendance ID, Reg ID, Student ID, or Event ID: ", false).toUpperCase();
        String sql = "SELECT a.attendance_id, a.registration_id, a.student_id, s.student_name, e.event_name, a.attendance_status, a.attendance_date " +
                     "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                     "JOIN events e ON a.event_id = e.event_id " +
                     "WHERE UPPER(a.attendance_id) LIKE ? OR UPPER(a.registration_id) LIKE ? OR UPPER(a.student_id) LIKE ? OR UPPER(a.event_id) LIKE ? " +
                     "ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + term + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    DualLogger.printf("%-10s %-12s %-10s %-20s %-24s %-10s %-12s\n", "Att ID", "Reg ID", "Student ID", "Name", "Event", "Status", "Date");
                    DualLogger.println("-------------------------------------------------------------------------------------------------");
                    found = true;
                }
                DualLogger.printf("%-10s %-12s %-10s %-20s %-24s %-10s %-12s\n",
                    rs.getString("attendance_id"),
                    rs.getString("registration_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("attendance_status"),
                    rs.getString("attendance_date"));
            }
            if (!found) DualLogger.warning("No attendance record matching '" + term + "' found.");
        }
    }

    private static void updateAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("UPDATE ATTENDANCE RECORD");
        String attId = InputReader.readString("Enter Attendance ID to update: ", false).toUpperCase();
        String sql = "SELECT attendance_status FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, attId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Attendance ID not found.");
                return;
            }
            String cur = rs.getString("attendance_status");
            DualLogger.println("Current Status: " + cur);
            DualLogger.println("Select New Status:\n1. PRESENT\n2. ABSENT");
            int c = InputReader.readInt("Choice (1-2): ", 1, 2);
            String newStatus = (c == 1) ? "PRESENT" : "ABSENT";

            try (PreparedStatement ups = conn.prepareStatement("UPDATE attendance SET attendance_status = ? WHERE attendance_id = ?")) {
                ups.setString(1, newStatus);
                ups.setString(2, attId);
                ups.executeUpdate();
                DualLogger.success("Attendance status updated to " + newStatus);
            }
        }
    }

    private static void markBulkAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("MARK BULK EVENT ATTENDANCE");
        String eventId = InputReader.readString("Enter Event ID for bulk attendance (e.g. EVT106): ", false).toUpperCase();
        
        String sql = "SELECT r.registration_id, r.student_id, s.student_name " +
                     "FROM registrations r JOIN students s ON r.student_id = s.student_id " +
                     "WHERE r.event_id = ? AND r.status = 'REGISTERED' " +
                     "AND r.registration_id NOT IN (SELECT registration_id FROM attendance WHERE event_id = ?) " +
                     "ORDER BY s.student_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, eventId);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            String today = LocalDate.now().toString();

            DatabaseManager.beginTransaction(conn);
            while (rs.next()) {
                String regId = rs.getString("registration_id");
                String stuId = rs.getString("student_id");
                String name = rs.getString("student_name");

                DualLogger.print(String.format("Student: %-10s %-20s -> Mark (P/A/S=Skip): ", stuId, name));
                String input = InputReader.readString("", false).toUpperCase();
                if ("S".equals(input)) continue;

                String status = "P".equals(input) ? "PRESENT" : "ABSENT";
                String attId = generateNextAttId(conn);

                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO attendance (attendance_id, registration_id, student_id, event_id, attendance_date, attendance_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
                    ins.setString(1, attId);
                    ins.setString(2, regId);
                    ins.setString(3, stuId);
                    ins.setString(4, eventId);
                    ins.setString(5, today);
                    ins.setString(6, status);
                    ins.executeUpdate();
                    count++;
                }
            }
            DatabaseManager.commit(conn);
            DualLogger.success("Bulk attendance recording completed for " + count + " students.");
        } catch (SQLException e) {
            DatabaseManager.rollback(conn);
            DualLogger.error("Bulk attendance recording failed: " + e.getMessage());
        }
    }

    private static void viewEventAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT-WISE ATTENDANCE");
        String eventId = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String sql = "SELECT a.attendance_id, a.student_id, s.student_name, s.department, a.attendance_status " +
                     "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                     "WHERE a.event_id = ? ORDER BY a.attendance_status ASC, s.student_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-12s %-22s %-25s %-10s\n", "Att ID", "Student ID", "Student Name", "Department", "Status");
            DualLogger.println("--------------------------------------------------------------------------------");
            int pres = 0, abs = 0;
            while (rs.next()) {
                String st = rs.getString("attendance_status");
                if ("PRESENT".equalsIgnoreCase(st)) pres++; else abs++;
                DualLogger.printf("%-10s %-12s %-22s %-25s %-10s\n",
                    rs.getString("attendance_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    st);
            }
            DualLogger.println("---");
            DualLogger.printf("Total Present: %d | Total Absent: %d\n", pres, abs);
            double pct = (pres + abs > 0) ? (pres * 100.0) / (pres + abs) : 0.0;
            DualLogger.printf("Attendance Percentage: %.2f%%\n", pct);
        }
    }

    private static void viewStudentAttendance(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT-WISE ATTENDANCE");
        String stuId = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT a.attendance_id, a.event_id, e.event_name, a.attendance_date, a.attendance_status " +
                     "FROM attendance a JOIN events e ON a.event_id = e.event_id " +
                     "WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stuId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-10s %-26s %-12s %-10s\n", "Att ID", "Event ID", "Event Name", "Date", "Status");
            DualLogger.println("---------------------------------------------------------------------------");
            int pres = 0, abs = 0;
            while (rs.next()) {
                String st = rs.getString("attendance_status");
                if ("PRESENT".equalsIgnoreCase(st)) pres++; else abs++;
                DualLogger.printf("%-10s %-10s %-26s %-12s %-10s\n",
                    rs.getString("attendance_id"),
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("attendance_date"),
                    st);
            }
            DualLogger.printf("Attended: %d | Absent: %d\n", pres, abs);
        }
    }

    private static void viewByStatus(Connection conn, String status) throws SQLException {
        DualLogger.subHeader(status + " STUDENTS LIST");
        String sql = "SELECT a.attendance_id, a.student_id, s.student_name, e.event_name, a.attendance_date " +
                     "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                     "JOIN events e ON a.event_id = e.event_id WHERE a.attendance_status = ? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-12s %-22s %-26s %-12s\n", "Att ID", "Student ID", "Student Name", "Event Name", "Date");
            DualLogger.println("-----------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-12s %-22s %-26s %-12s\n",
                    rs.getString("attendance_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("attendance_date"));
                c++;
            }
            DualLogger.println("Total " + status + " Students: " + c);
        }
    }

    private static void viewAttendancePercentage(Connection conn) throws SQLException {
        DualLogger.subHeader("OVERALL & EVENT-WISE ATTENDANCE PERCENTAGE");
        String sql = "SELECT * FROM v_attendance_overview WHERE total_registered > 0 ORDER BY attendance_percentage DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-26s %-12s %-10s %-10s %-14s\n", "Event ID", "Event Name", "Registered", "Present", "Absent", "Attendance %");
            DualLogger.println("----------------------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-12d %-10d %-10d %-14.2f%%\n",
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getInt("total_registered"),
                    rs.getInt("present_count"),
                    rs.getInt("absent_count"),
                    rs.getDouble("attendance_percentage"));
            }
        }
    }

    private static void viewEventAttendanceSummary(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT ATTENDANCE SUMMARY");
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM attendance WHERE attendance_status='PRESENT') as present, " +
                     "(SELECT COUNT(*) FROM attendance WHERE attendance_status='ABSENT') as absent, " +
                     "(SELECT COUNT(*) FROM attendance) as total";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int pres = rs.getInt("present");
                int abs = rs.getInt("absent");
                int tot = rs.getInt("total");
                double pct = tot > 0 ? (pres * 100.0) / tot : 0.0;
                DualLogger.println("Total Recorded Attendances : " + tot);
                DualLogger.println("Total Present Students     : " + pres);
                DualLogger.println("Total Absent Students      : " + abs);
                DualLogger.printf("Campus-wide Attendance Rate: %.2f%%\n", pct);
            }
        }
    }
}
