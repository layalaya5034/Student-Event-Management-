package com.smartcampus.participation;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipationMenu {

    public static void showMenu() {
        DualLogger.setModule("ParticipationManagement");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                 PARTICIPATION MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Record Student Participation");
            DualLogger.println("2.  View All Participation Records");
            DualLogger.println("3.  Search Participation Record");
            DualLogger.println("4.  Update Participation Details");
            DualLogger.println("5.  View Student Participation History");
            DualLogger.println("6.  View Event Participation Details");
            DualLogger.println("7.  View Top Participating Students");
            DualLogger.println("8.  View Students with Zero Participation");
            DualLogger.println("9.  View Department-wise Participation");
            DualLogger.println("10. View Year-wise Participation");
            DualLogger.println("11. View Participation Rankings");
            DualLogger.println("12. View Certificate Eligibility");
            DualLogger.println("13. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 13);
            if (choice == 13) {
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
                    recordParticipation(conn);
                    break;
                case 2:
                    viewAllParticipations(conn);
                    break;
                case 3:
                    searchParticipation(conn);
                    break;
                case 4:
                    updateParticipation(conn);
                    break;
                case 5:
                    viewStudentParticipationHistory(conn);
                    break;
                case 6:
                    viewEventParticipationDetails(conn);
                    break;
                case 7:
                    viewTopParticipatingStudents(conn);
                    break;
                case 8:
                    viewStudentsWithZeroParticipation(conn);
                    break;
                case 9:
                    viewDepartmentWiseParticipation(conn);
                    break;
                case 10:
                    viewYearWiseParticipation(conn);
                    break;
                case 11:
                    viewParticipationRankings(conn);
                    break;
                case 12:
                    viewCertificateEligibility(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void recordParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("RECORD STUDENT PARTICIPATION");
        String eventId = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String studentId = InputReader.readString("Enter Student ID: ", false).toUpperCase();

        // Check registration
        String regId = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT registration_id FROM registrations WHERE student_id = ? AND event_id = ? AND status='REGISTERED'")) {
            ps.setString(1, studentId);
            ps.setString(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Student '" + studentId + "' is not actively registered for event '" + eventId + "'!");
                return;
            }
            regId = rs.getString("registration_id");
        }

        // Check duplicate participation
        try (PreparedStatement ps = conn.prepareStatement("SELECT participation_id FROM participations WHERE registration_id = ?")) {
            ps.setString(1, regId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DualLogger.warning("Participation record already exists for this registration (ID: " + rs.getString("participation_id") + ")!");
                return;
            }
        }

        DualLogger.println("Select Participation Status:");
        DualLogger.println("1. PARTICIPATED\n2. WINNER\n3. RUNNER_UP\n4. PARTICIPANT\n5. NOT_PARTICIPATED");
        int stChoice = InputReader.readInt("Choice (1-5): ", 1, 5);
        String[] statuses = {"PARTICIPATED", "WINNER", "RUNNER_UP", "PARTICIPANT", "NOT_PARTICIPATED"};
        String partStatus = statuses[stChoice - 1];

        String remarks = InputReader.readString("Enter Remarks (optional): ", true);

        DualLogger.println("Select Certificate Status:\n1. ISSUED\n2. PENDING\n3. NOT_ELIGIBLE");
        int certChoice = InputReader.readInt("Choice (1-3): ", 1, 3);
        String[] certs = {"ISSUED", "PENDING", "NOT_ELIGIBLE"};
        String certStatus = certs[certChoice - 1];

        String partId = generateNextPartId(conn);

        try {
            DatabaseManager.beginTransaction(conn);
            String insertSql = "INSERT INTO participations (participation_id, registration_id, student_id, event_id, participation_status, remarks, certificate_status) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, partId);
                ps.setString(2, regId);
                ps.setString(3, studentId);
                ps.setString(4, eventId);
                ps.setString(5, partStatus);
                ps.setString(6, remarks.isEmpty() ? null : remarks);
                ps.setString(7, certStatus);
                ps.executeUpdate();
            }
            DatabaseManager.commit(conn);
            DualLogger.success("Participation record saved successfully! [ID: " + partId + ", Outcome: " + partStatus + ", Certificate: " + certStatus + "]");
        } catch (SQLException e) {
            DatabaseManager.rollback(conn);
            DualLogger.error("Failed to save participation record: " + e.getMessage());
        }
    }

    private static String generateNextPartId(Connection conn) throws SQLException {
        String sql = "SELECT participation_id FROM participations WHERE participation_id LIKE 'PAR%' ORDER BY participation_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("participation_id");
                try {
                    int num = Integer.parseInt(lastId.replaceAll("\\D+", ""));
                    return String.format("PAR%04d", num + 1);
                } catch (Exception ignored) {}
            }
        }
        return "PAR1101";
    }

    private static void viewAllParticipations(Connection conn) throws SQLException {
        DualLogger.subHeader("PARTICIPATION RECORDS");
        String sql = "SELECT p.participation_id, p.student_id, s.student_name, p.event_id, e.event_name, p.participation_status, p.certificate_status " +
                     "FROM participations p JOIN students s ON p.student_id = s.student_id " +
                     "JOIN events e ON p.event_id = e.event_id ORDER BY p.participation_id DESC LIMIT 30";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-10s %-20s %-10s %-24s %-15s %-12s\n", "Part ID", "Student ID", "Student Name", "Event ID", "Event Name", "Outcome", "Certificate");
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-10s %-20s %-10s %-24s %-15s %-12s\n",
                    rs.getString("participation_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
                c++;
            }
            DualLogger.println("---------------------------------------------------------------------------------------------------------");
            DualLogger.println("Total Participation Records: " + c);
        }
    }

    private static void searchParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("SEARCH PARTICIPATION RECORD");
        String term = InputReader.readString("Enter Participation ID, Student ID, or Event ID: ", false).toUpperCase();
        String sql = "SELECT p.participation_id, p.student_id, s.student_name, e.event_name, p.participation_status, p.remarks, p.certificate_status " +
                     "FROM participations p JOIN students s ON p.student_id = s.student_id " +
                     "JOIN events e ON p.event_id = e.event_id " +
                     "WHERE UPPER(p.participation_id) LIKE ? OR UPPER(p.student_id) LIKE ? OR UPPER(p.event_id) LIKE ? OR UPPER(s.student_name) LIKE ? " +
                     "ORDER BY p.participation_id DESC";
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
                    DualLogger.printf("%-10s %-10s %-20s %-24s %-15s %-12s\n", "Part ID", "Student ID", "Name", "Event", "Outcome", "Certificate");
                    DualLogger.println("-----------------------------------------------------------------------------------------------");
                    found = true;
                }
                DualLogger.printf("%-10s %-10s %-20s %-24s %-15s %-12s\n",
                    rs.getString("participation_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
            }
            if (!found) DualLogger.warning("No participation record matching '" + term + "' found.");
        }
    }

    private static void updateParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("UPDATE PARTICIPATION DETAILS");
        String partId = InputReader.readString("Enter Participation ID: ", false).toUpperCase();
        String sql = "SELECT participation_status, certificate_status, remarks FROM participations WHERE participation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, partId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Participation ID not found.");
                return;
            }
            DualLogger.println("Current Outcome    : " + rs.getString("participation_status"));
            DualLogger.println("Current Certificate: " + rs.getString("certificate_status"));
            DualLogger.println("Current Remarks    : " + rs.getString("remarks"));

            DualLogger.println("\nSelect New Outcome:\n1. PARTICIPATED 2. WINNER 3. RUNNER_UP 4. PARTICIPANT 5. NOT_PARTICIPATED 6. Keep");
            int c1 = InputReader.readInt("Choice (1-6): ", 1, 6);
            String[] st = {"PARTICIPATED", "WINNER", "RUNNER_UP", "PARTICIPANT", "NOT_PARTICIPATED"};
            String newOutcome = (c1 <= 5) ? st[c1 - 1] : rs.getString("participation_status");

            DualLogger.println("Select Certificate Status:\n1. ISSUED 2. PENDING 3. NOT_ELIGIBLE 4. Keep");
            int c2 = InputReader.readInt("Choice (1-4): ", 1, 4);
            String[] cert = {"ISSUED", "PENDING", "NOT_ELIGIBLE"};
            String newCert = (c2 <= 3) ? cert[c2 - 1] : rs.getString("certificate_status");

            String newRemarks = InputReader.readString("New Remarks: ", true);
            String finalRem = newRemarks.isEmpty() ? rs.getString("remarks") : newRemarks;

            try (PreparedStatement ups = conn.prepareStatement(
                    "UPDATE participations SET participation_status = ?, certificate_status = ?, remarks = ? WHERE participation_id = ?")) {
                ups.setString(1, newOutcome);
                ups.setString(2, newCert);
                ups.setString(3, finalRem);
                ups.setString(4, partId);
                ups.executeUpdate();
                DualLogger.success("Participation record updated successfully.");
            }
        }
    }

    private static void viewStudentParticipationHistory(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT PARTICIPATION HISTORY");
        String stuId = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT p.participation_id, e.event_name, e.event_date, p.participation_status, p.remarks, p.certificate_status " +
                     "FROM participations p JOIN events e ON p.event_id = e.event_id " +
                     "WHERE p.student_id = ? ORDER BY e.event_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stuId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-26s %-12s %-15s %-12s\n", "Part ID", "Event Name", "Date", "Outcome", "Certificate");
            DualLogger.println("---------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-12s %-15s %-12s\n",
                    rs.getString("participation_id"),
                    rs.getString("event_name"),
                    rs.getString("event_date"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
                c++;
            }
            DualLogger.println("Total Participated Events: " + c);
        }
    }

    private static void viewEventParticipationDetails(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT PARTICIPATION DETAILS");
        String eventId = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String sql = "SELECT p.participation_id, p.student_id, s.student_name, s.department, p.participation_status, p.certificate_status " +
                     "FROM participations p JOIN students s ON p.student_id = s.student_id " +
                     "WHERE p.event_id = ? ORDER BY p.participation_status ASC, s.student_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-12s %-22s %-22s %-15s %-12s\n", "Part ID", "Student ID", "Student Name", "Department", "Outcome", "Certificate");
            DualLogger.println("--------------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-12s %-22s %-22s %-15s %-12s\n",
                    rs.getString("participation_id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
                c++;
            }
            DualLogger.println("Total Participants for Event " + eventId + ": " + c);
        }
    }

    private static void viewTopParticipatingStudents(Connection conn) throws SQLException {
        DualLogger.subHeader("TOP PARTICIPATING STUDENTS");
        String sql = "SELECT s.student_id, s.student_name, s.department, " +
                     "COUNT(DISTINCT p.event_id) as total_events, " +
                     "COUNT(CASE WHEN p.participation_status='WINNER' THEN 1 END) as wins " +
                     "FROM students s JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.student_id ORDER BY total_events DESC, wins DESC, s.student_name ASC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-8s %-12s %-22s %-25s %-12s %-8s\n", "Rank", "Student ID", "Student Name", "Department", "Total Events", "Wins");
            DualLogger.println("---------------------------------------------------------------------------------------------");
            int rank = 1;
            while (rs.next()) {
                DualLogger.printf("%-8d %-12s %-22s %-25s %-12d %-8d\n",
                    rank++,
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("total_events"),
                    rs.getInt("wins"));
            }
        }
    }

    private static void viewStudentsWithZeroParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENTS WITH ZERO PARTICIPATION");
        String sql = "SELECT s.student_id, s.register_number, s.student_name, s.department, s.year_of_study " +
                     "FROM students s WHERE s.student_id NOT IN (SELECT DISTINCT student_id FROM participations) " +
                     "ORDER BY s.student_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-12s %-12s %-22s %-28s %-6s\n", "Student ID", "Register No", "Student Name", "Department", "Year");
            DualLogger.println("------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-12s %-12s %-22s %-28s %-6d\n",
                    rs.getString("student_id"),
                    rs.getString("register_number"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("year_of_study"));
                c++;
            }
            DualLogger.println("Total Students with Zero Participation: " + c);
        }
    }

    private static void viewDepartmentWiseParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("DEPARTMENT-WISE PARTICIPATION");
        String sql = "SELECT s.department, COUNT(DISTINCT s.student_id) as students, " +
                     "COUNT(p.participation_id) as participations " +
                     "FROM students s LEFT JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.department ORDER BY participations DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-35s %-15s %-15s\n", "Department", "Total Students", "Total Participations");
            DualLogger.println("-------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-35s %-15d %-15d\n",
                    rs.getString("department"),
                    rs.getInt("students"),
                    rs.getInt("participations"));
            }
        }
    }

    private static void viewYearWiseParticipation(Connection conn) throws SQLException {
        DualLogger.subHeader("YEAR-WISE PARTICIPATION");
        String sql = "SELECT s.year_of_study, COUNT(p.participation_id) as participations " +
                     "FROM students s LEFT JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.year_of_study ORDER BY s.year_of_study ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-15s %-20s\n", "Year of Study", "Total Participations");
            DualLogger.println("-----------------------------------");
            while (rs.next()) {
                DualLogger.printf("Year %-10d %-20d\n", rs.getInt("year_of_study"), rs.getInt("participations"));
            }
        }
    }

    private static void viewParticipationRankings(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT PARTICIPATION RANKINGS");
        String sql = "SELECT DENSE_RANK() OVER (ORDER BY COUNT(DISTINCT p.event_id) DESC, COUNT(CASE WHEN p.participation_status='WINNER' THEN 1 END) DESC) as rk, " +
                     "s.student_id, s.student_name, s.department, COUNT(DISTINCT p.event_id) as events, " +
                     "COUNT(CASE WHEN p.participation_status='WINNER' THEN 1 END) as wins " +
                     "FROM students s JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.student_id ORDER BY rk ASC, s.student_name ASC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-8s %-12s %-22s %-25s %-10s %-8s\n", "Rank", "Student ID", "Student Name", "Department", "Events", "Wins");
            DualLogger.println("-------------------------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-8d %-12s %-22s %-25s %-10d %-8d\n",
                    rs.getInt("rk"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("events"),
                    rs.getInt("wins"));
            }
        }
    }

    private static void viewCertificateEligibility(Connection conn) throws SQLException {
        DualLogger.subHeader("CERTIFICATE ELIGIBILITY REPORT");
        String sql = "SELECT s.student_id, s.student_name, e.event_name, p.participation_status, p.certificate_status " +
                     "FROM participations p JOIN students s ON p.student_id = s.student_id " +
                     "JOIN events e ON p.event_id = e.event_id " +
                     "JOIN attendance a ON p.registration_id = a.registration_id " +
                     "WHERE a.attendance_status = 'PRESENT' AND p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT') " +
                     "ORDER BY s.student_name ASC LIMIT 20";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-20s %-24s %-15s %-12s\n", "Student ID", "Student Name", "Event Name", "Participation", "Certificate");
            DualLogger.println("------------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-20s %-24s %-15s %-12s\n",
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
                c++;
            }
            DualLogger.println("Total Eligible Verified Records: " + c);
        }
    }
}
