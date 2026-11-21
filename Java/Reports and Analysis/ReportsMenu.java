package com.smartcampus.reports;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportsMenu {

    public static void showMenu() {
        DualLogger.setModule("EventAnalysis");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                  EVENT ANALYSIS & REPORTS");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Daily Event Report");
            DualLogger.println("2.  Weekly Event Report");
            DualLogger.println("3.  Monthly Event Report");
            DualLogger.println("4.  Student-wise Participation Report");
            DualLogger.println("5.  Department-wise Participation Report");
            DualLogger.println("6.  Year-wise Participation Report");
            DualLogger.println("7.  Event-wise Registration Report");
            DualLogger.println("8.  Event-wise Attendance Report");
            DualLogger.println("9.  Event Popularity Analysis");
            DualLogger.println("10. Most Registered Events");
            DualLogger.println("11. Least Registered Events");
            DualLogger.println("12. Top Participating Students");
            DualLogger.println("13. Students with Zero Participation");
            DualLogger.println("14. Attendance Percentage Analysis");
            DualLogger.println("15. Event Capacity Utilization Analysis");
            DualLogger.println("16. Event Type Distribution");
            DualLogger.println("17. Student Participation Ranking");
            DualLogger.println("18. Certificate Eligibility Report");
            DualLogger.println("19. Registration Trend Analysis");
            DualLogger.println("20. Attendance Trend Analysis");
            DualLogger.println("21. Event Workload Analysis");
            DualLogger.println("22. Generate Complete Event Report");
            DualLogger.println("23. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 23);
            if (choice == 23) {
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
                    generateDailyReport(conn);
                    break;
                case 2:
                    generateWeeklyReport(conn);
                    break;
                case 3:
                    generateMonthlyReport(conn);
                    break;
                case 4:
                    generateStudentParticipationReport(conn);
                    break;
                case 5:
                    generateDepartmentParticipationReport(conn);
                    break;
                case 6:
                    generateYearParticipationReport(conn);
                    break;
                case 7:
                    generateEventRegistrationReport(conn);
                    break;
                case 8:
                    generateEventAttendanceReport(conn);
                    break;
                case 9:
                case 10:
                    generateMostRegisteredEvents(conn);
                    break;
                case 11:
                    generateLeastRegisteredEvents(conn);
                    break;
                case 12:
                case 17:
                    generateTopParticipatingStudents(conn);
                    break;
                case 13:
                    generateZeroParticipationReport(conn);
                    break;
                case 14:
                    generateAttendancePercentageAnalysis(conn);
                    break;
                case 15:
                    generateCapacityUtilizationAnalysis(conn);
                    break;
                case 16:
                    generateEventTypeDistribution(conn);
                    break;
                case 18:
                    generateCertificateEligibilityReport(conn);
                    break;
                case 19:
                    generateRegistrationTrends(conn);
                    break;
                case 20:
                    generateAttendanceTrends(conn);
                    break;
                case 21:
                    generateWorkloadAnalysis(conn);
                    break;
                case 22:
                    generateCompleteReport(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Report generation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void generateDailyReport(Connection conn) throws SQLException {
        String date = InputReader.readDate("Enter Report Date (YYYY-MM-DD)", LocalDate.now().toString());
        DualLogger.println();
        DualLogger.subHeader("DAILY EVENT REPORT");
        DualLogger.println("Date                       : " + date);

        int totalEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date = '" + date + "'");
        int totalReg = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE registration_date = '" + date + "'");
        int activeReg = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE registration_date = '" + date + "' AND status='REGISTERED'");
        int cancelReg = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE registration_date = '" + date + "' AND status='CANCELLED'");
        int pres = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_date = '" + date + "' AND attendance_status='PRESENT'");
        int abs = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_date = '" + date + "' AND attendance_status='ABSENT'");
        double pct = (pres + abs > 0) ? (pres * 100.0) / (pres + abs) : 0.0;
        int parts = getSingleCount(conn, "SELECT COUNT(*) FROM participations p JOIN events e ON p.event_id = e.event_id WHERE e.event_date = '" + date + "'");

        DualLogger.println("Total Events               : " + totalEvents);
        DualLogger.println("Total Registrations        : " + totalReg);
        DualLogger.println("Active Registrations       : " + activeReg);
        DualLogger.println("Cancelled Registrations    : " + cancelReg);
        DualLogger.println();
        DualLogger.println("Present Students           : " + pres);
        DualLogger.println("Absent Students            : " + abs);
        DualLogger.printf("Attendance Percentage      : %.2f%%\n", pct);
        DualLogger.println();
        DualLogger.println("Total Participations       : " + parts);
        DualLogger.println("------------------------------------------------------------");
    }

    private static void generateWeeklyReport(Connection conn) throws SQLException {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        DualLogger.subHeader("WEEKLY EVENT REPORT (" + start + " to " + end + ")");
        String sql = "SELECT event_id, event_name, event_date, venue, event_status FROM events " +
                     "WHERE event_date BETWEEN '" + start + "' AND '" + end + "' ORDER BY event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-26s %-12s %-18s %-10s\n", "Event ID", "Event Name", "Date", "Venue", "Status");
            DualLogger.println("--------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-12s %-18s %-10s\n",
                    rs.getString("event_id"), rs.getString("event_name"),
                    rs.getString("event_date"), rs.getString("venue"), rs.getString("event_status"));
                c++;
            }
            DualLogger.println("Total Events Scheduled This Week: " + c);
        }
    }

    private static void generateMonthlyReport(Connection conn) throws SQLException {
        String month = InputReader.readString("Enter Month (YYYY-MM) [e.g. 2026-11]: ", true);
        if (month.isEmpty()) month = LocalDate.now().toString().substring(0, 7);
        DualLogger.subHeader("MONTHLY EVENT REPORT FOR " + month);
        int evCnt = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date LIKE '" + month + "%'");
        int regCnt = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE registration_date LIKE '" + month + "%'");
        int attCnt = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_date LIKE '" + month + "%' AND attendance_status='PRESENT'");
        int partCnt = getSingleCount(conn, "SELECT COUNT(*) FROM participations p JOIN events e ON p.event_id = e.event_id WHERE e.event_date LIKE '" + month + "%'");

        DualLogger.println("Total Events Scheduled : " + evCnt);
        DualLogger.println("Total Registrations    : " + regCnt);
        DualLogger.println("Total Present Students : " + attCnt);
        DualLogger.println("Total Participations   : " + partCnt);
        DualLogger.println("------------------------------------------------------------");
    }

    private static void generateStudentParticipationReport(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT-WISE PARTICIPATION REPORT");
        String sql = "SELECT * FROM v_student_participation_summary ORDER BY total_participated_events DESC LIMIT 15";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-20s %-20s %-10s %-10s %-10s\n", "ID", "Name", "Department", "Regs", "Attended", "Won");
            DualLogger.println("--------------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-10s %-20s %-20s %-10d %-10d %-10d\n",
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("total_registered_events"),
                    rs.getInt("total_attended_events"),
                    rs.getInt("wins_count"));
            }
        }
    }

    private static void generateDepartmentParticipationReport(Connection conn) throws SQLException {
        DualLogger.subHeader("DEPARTMENT PARTICIPATION REPORT");
        String sql = "SELECT department, total_participations FROM v_department_summary ORDER BY total_participations DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-35s %-20s\n", "Department", "Total Participation");
            DualLogger.println("--------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-35s %-20d\n", rs.getString("department"), rs.getInt("total_participations"));
            }
        }
    }

    private static void generateYearParticipationReport(Connection conn) throws SQLException {
        DualLogger.subHeader("YEAR-WISE PARTICIPATION REPORT");
        String sql = "SELECT s.year_of_study, COUNT(p.participation_id) as count " +
                     "FROM students s LEFT JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.year_of_study ORDER BY s.year_of_study ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-15s %-20s\n", "Year of Study", "Total Participations");
            DualLogger.println("-----------------------------------");
            while (rs.next()) {
                DualLogger.printf("Year %-10d %-20d\n", rs.getInt("year_of_study"), rs.getInt("count"));
            }
        }
    }

    private static void generateEventRegistrationReport(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT-WISE REGISTRATION REPORT");
        String sql = "SELECT e.event_id, e.event_name, e.max_participants, " +
                     "COUNT(CASE WHEN r.status='REGISTERED' THEN 1 END) as active, " +
                     "COUNT(CASE WHEN r.status='WAITLISTED' THEN 1 END) as wait, " +
                     "COUNT(CASE WHEN r.status='CANCELLED' THEN 1 END) as cancel " +
                     "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id " +
                     "GROUP BY e.event_id ORDER BY active DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-26s %-10s %-10s %-10s %-10s\n", "Event ID", "Event Name", "Capacity", "Active", "Waitlist", "Cancelled");
            DualLogger.println("--------------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-10d %-10d %-10d %-10d\n",
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getInt("max_participants"),
                    rs.getInt("active"),
                    rs.getInt("wait"),
                    rs.getInt("cancel"));
            }
        }
    }

    private static void generateEventAttendanceReport(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT ATTENDANCE ANALYSIS");
        String sql = "SELECT * FROM v_attendance_overview WHERE total_registered > 0 ORDER BY attendance_percentage DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-26s %-12s %-10s %-12s\n", "Event Name", "Registered", "Present", "Percentage");
            DualLogger.println("----------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-26s %-12d %-10d %-12.2f%%\n",
                    rs.getString("event_name"),
                    rs.getInt("total_registered"),
                    rs.getInt("present_count"),
                    rs.getDouble("attendance_percentage"));
            }
        }
    }

    private static void generateMostRegisteredEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("MOST REGISTERED EVENTS");
        String sql = "SELECT e.event_name, COUNT(r.registration_id) as cnt " +
                     "FROM events e JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                     "GROUP BY e.event_id ORDER BY cnt DESC LIMIT 5";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-8s %-32s %-15s\n", "Rank", "Event Name", "Registrations");
            DualLogger.println("---------------------------------------------------------");
            int rank = 1;
            String mostPop = null;
            while (rs.next()) {
                if (mostPop == null) mostPop = rs.getString("event_name");
                DualLogger.printf("%-8d %-32s %-15d\n", rank++, rs.getString("event_name"), rs.getInt("cnt"));
            }
            DualLogger.println("---");
            DualLogger.println("MOST POPULAR EVENT: " + (mostPop != null ? mostPop : "N/A"));
        }
    }

    private static void generateLeastRegisteredEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("LEAST REGISTERED EVENTS");
        String sql = "SELECT e.event_name, COUNT(r.registration_id) as cnt " +
                     "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                     "WHERE e.event_status != 'CANCELLED' " +
                     "GROUP BY e.event_id ORDER BY cnt ASC LIMIT 5";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-8s %-32s %-15s\n", "Rank", "Event Name", "Registrations");
            DualLogger.println("---------------------------------------------------------");
            int rank = 1;
            while (rs.next()) {
                DualLogger.printf("%-8d %-32s %-15d\n", rank++, rs.getString("event_name"), rs.getInt("cnt"));
            }
        }
    }

    private static void generateTopParticipatingStudents(Connection conn) throws SQLException {
        DualLogger.subHeader("TOP PARTICIPATING STUDENTS");
        String sql = "SELECT s.student_id, s.student_name, COUNT(DISTINCT p.event_id) as cnt " +
                     "FROM students s JOIN participations p ON s.student_id = p.student_id " +
                     "GROUP BY s.student_id ORDER BY cnt DESC, s.student_name ASC LIMIT 5";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-8s %-12s %-25s %-12s\n", "Rank", "Student ID", "Student Name", "Total Events");
            DualLogger.println("-------------------------------------------------------------");
            int rank = 1;
            while (rs.next()) {
                DualLogger.printf("%-8d %-12s %-25s %-12d\n", rank++, rs.getString("student_id"), rs.getString("student_name"), rs.getInt("cnt"));
            }
        }
    }

    private static void generateZeroParticipationReport(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENTS WITH ZERO PARTICIPATION");
        String sql = "SELECT student_id, register_number, student_name, department FROM students " +
                     "WHERE student_id NOT IN (SELECT DISTINCT student_id FROM participations) ORDER BY student_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-12s %-22s %-25s\n", "Student ID", "Register No", "Student Name", "Department");
            DualLogger.println("----------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-12s %-22s %-25s\n",
                    rs.getString("student_id"), rs.getString("register_number"), rs.getString("student_name"), rs.getString("department"));
                c++;
            }
            DualLogger.println("Total Students: " + c);
        }
    }

    private static void generateAttendancePercentageAnalysis(Connection conn) throws SQLException {
        generateEventAttendanceReport(conn);
    }

    private static void generateCapacityUtilizationAnalysis(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT CAPACITY ANALYSIS");
        String sql = "SELECT event_name, max_participants, active_registrations, available_seats, capacity_utilization_pct " +
                     "FROM v_event_overview ORDER BY capacity_utilization_pct DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-26s %-10s %-12s %-11s %-12s\n", "Event Name", "Capacity", "Registered", "Available", "Utilization");
            DualLogger.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-26s %-10d %-12d %-11d %-12.2f%%\n",
                    rs.getString("event_name"),
                    rs.getInt("max_participants"),
                    rs.getInt("active_registrations"),
                    rs.getInt("available_seats"),
                    rs.getDouble("capacity_utilization_pct"));
            }
        }
    }

    private static void generateEventTypeDistribution(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT TYPE DISTRIBUTION");
        String sql = "SELECT event_type, COUNT(*) as count FROM events GROUP BY event_type ORDER BY count DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-20s %-10s\n", "Category", "Event Count");
            DualLogger.println("------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-20s %-10d\n", rs.getString("event_type"), rs.getInt("count"));
            }
        }
    }

    private static void generateCertificateEligibilityReport(Connection conn) throws SQLException {
        DualLogger.subHeader("CERTIFICATE ELIGIBILITY");
        String sql = "SELECT s.student_name, e.event_name, p.participation_status, p.certificate_status " +
                     "FROM participations p JOIN students s ON p.student_id = s.student_id " +
                     "JOIN events e ON p.event_id = e.event_id WHERE p.certificate_status = 'ISSUED' " +
                     "ORDER BY s.student_name ASC LIMIT 15";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-20s %-25s %-15s %-10s\n", "Student Name", "Event Name", "Outcome", "Certificate");
            DualLogger.println("------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-20s %-25s %-15s %-10s\n",
                    rs.getString("student_name"),
                    rs.getString("event_name"),
                    rs.getString("participation_status"),
                    rs.getString("certificate_status"));
            }
        }
    }

    private static void generateRegistrationTrends(Connection conn) throws SQLException {
        DualLogger.subHeader("DAILY REGISTRATION TRENDS");
        String sql = "SELECT registration_date, COUNT(*) as cnt FROM registrations GROUP BY registration_date ORDER BY registration_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-15s %-15s\n", "Date", "Registrations");
            DualLogger.println("------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-15s %-15d\n", rs.getString("registration_date"), rs.getInt("cnt"));
            }
        }
    }

    private static void generateAttendanceTrends(Connection conn) throws SQLException {
        DualLogger.subHeader("DAILY ATTENDANCE TRENDS");
        String sql = "SELECT attendance_date, COUNT(*) as cnt FROM attendance GROUP BY attendance_date ORDER BY attendance_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-15s %-15s\n", "Date", "Present Count");
            DualLogger.println("------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-15s %-15d\n", rs.getString("attendance_date"), rs.getInt("cnt"));
            }
        }
    }

    private static void generateWorkloadAnalysis(Connection conn) throws SQLException {
        DualLogger.subHeader("CAMPUS EVENT WORKLOAD & VENUE ALLOCATION");
        String sql = "SELECT venue, COUNT(*) as count FROM events GROUP BY venue ORDER BY count DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-25s %-15s\n", "Venue", "Allocated Events");
            DualLogger.println("----------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-25s %-15d\n", rs.getString("venue"), rs.getInt("count"));
            }
        }
    }

    private static void generateCompleteReport(Connection conn) throws SQLException {
        DualLogger.println();
        DualLogger.println("================================================================================");
        DualLogger.println("                 COMPLETE CAMPUS EVENT MANAGEMENT REPORT");
        DualLogger.println("================================================================================");
        int stu = getSingleCount(conn, "SELECT COUNT(*) FROM students");
        int ev = getSingleCount(conn, "SELECT COUNT(*) FROM events");
        int reg = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status='REGISTERED'");
        int att = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_status='PRESENT'");
        int part = getSingleCount(conn, "SELECT COUNT(*) FROM participations");

        DualLogger.printf("%-35s : %d\n", "Total Enrolled Students", stu);
        DualLogger.printf("%-35s : %d\n", "Total Scheduled Events", ev);
        DualLogger.printf("%-35s : %d\n", "Total Active Registrations", reg);
        DualLogger.printf("%-35s : %d\n", "Total Attended Students", att);
        DualLogger.printf("%-35s : %d\n", "Total Logged Participations", part);
        DualLogger.println("================================================================================");
        DualLogger.success("Complete event report generated successfully.");
    }

    private static int getSingleCount(Connection conn, String query) {
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) {}
        return 0;
    }
}
