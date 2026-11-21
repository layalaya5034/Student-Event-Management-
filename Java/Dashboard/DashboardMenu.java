package com.smartcampus.dashboard;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardMenu {

    public static void showDashboard() {
        DualLogger.setModule("SmartCampusEventManagement");
        while (true) {
            renderDashboardScreen();
            DualLogger.println();
            DualLogger.println("1. Refresh Dashboard");
            DualLogger.println("2. View Student Summary");
            DualLogger.println("3. View Event Summary");
            DualLogger.println("4. View Registration Summary");
            DualLogger.println("5. View Attendance Summary");
            DualLogger.println("6. View Department-wise Summary");
            DualLogger.println("7. View Event Popularity Summary");
            DualLogger.println("8. View Top Participating Students");
            DualLogger.println("9. View Upcoming Events");
            DualLogger.println("10. Return to Main Menu");
            DualLogger.println("============================================================");

            int choice = InputReader.readInt("Enter your choice: ", 1, 10);
            if (choice == 10) {
                DualLogger.println("[INFO] Returning to Main Menu...");
                break;
            }

            handleSubOption(choice);
        }
    }

    private static void renderDashboardScreen() {
        DualLogger.println();
        DualLogger.println("============================================================");
        DualLogger.println("          SMART CAMPUS EVENT MANAGEMENT DASHBOARD");
        DualLogger.println("============================================================");

        try (Connection conn = DatabaseManager.getConnection()) {
            // 1. Overall Summary
            DualLogger.println();
            DualLogger.println("                OVERALL SUMMARY");
            DualLogger.println("------------------------------------------------------------");

            int totalStudents = getSingleCount(conn, "SELECT COUNT(*) FROM students");
            int totalEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events");
            int totalRegs = getSingleCount(conn, "SELECT COUNT(*) FROM registrations");
            int activeRegs = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status = 'REGISTERED'");
            int cancelledRegs = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status = 'CANCELLED'");
            int waitlistedRegs = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status = 'WAITLISTED'");
            int totalPresent = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_status = 'PRESENT'");
            int totalAbsent = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_status = 'ABSENT'");
            int totalParts = getSingleCount(conn, "SELECT COUNT(*) FROM participations");
            int completedEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_status = 'COMPLETED'");
            int upcomingEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_status IN ('UPCOMING', 'OPEN')");
            int ongoingEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_status = 'ONGOING'");
            int cancelledEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_status = 'CANCELLED'");

            DualLogger.printf("%-40s : %d\n", "Total Registered Students", totalStudents);
            DualLogger.printf("%-40s : %d\n", "Total Registered Events", totalEvents);
            DualLogger.printf("%-40s : %d\n", "Total Event Registrations", totalRegs);
            DualLogger.printf("%-40s : %d\n", "Total Attended Students", totalPresent);
            DualLogger.printf("%-40s : %d\n", "Total Absent Students", totalAbsent);
            DualLogger.printf("%-40s : %d\n", "Total Participation Records", totalParts);
            DualLogger.printf("%-40s : %d\n", "Total Completed Events", completedEvents);
            DualLogger.printf("%-40s : %d\n", "Total Upcoming Events", upcomingEvents);
            DualLogger.printf("%-40s : %d\n", "Total Ongoing Events", ongoingEvents);
            DualLogger.printf("%-40s : %d\n", "Total Cancelled Events", cancelledEvents);

            // 2. Event Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("                EVENT OVERVIEW");
            DualLogger.println("---");
            LocalDate today = LocalDate.now();
            String todayStr = today.toString();
            String nextWeekStr = today.plusDays(7).toString();
            String monthPrefix = today.toString().substring(0, 7);

            int todayEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date = '" + todayStr + "'");
            int weekEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date >= '" + todayStr + "' AND event_date <= '" + nextWeekStr + "'");
            int monthEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date LIKE '" + monthPrefix + "%'");

            DualLogger.printf("%-40s : %d\n", "Today's Events", todayEvents);
            DualLogger.printf("%-40s : %d\n", "This Week's Events", weekEvents);
            DualLogger.printf("%-40s : %d\n", "This Month's Events", monthEvents);
            DualLogger.println();
            DualLogger.printf("%-40s : %d\n", "Completed Events", completedEvents);
            DualLogger.printf("%-40s : %d\n", "Upcoming Events", upcomingEvents);
            DualLogger.printf("%-40s : %d\n", "Ongoing Events", ongoingEvents);
            DualLogger.printf("%-40s : %d\n", "Cancelled Events", cancelledEvents);

            // 3. Student Participation Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("            STUDENT PARTICIPATION OVERVIEW");
            DualLogger.println("---");
            int stuWithReg = getSingleCount(conn, "SELECT COUNT(DISTINCT student_id) FROM registrations WHERE status = 'REGISTERED'");
            int stuZeroReg = Math.max(0, totalStudents - stuWithReg);
            int stuParticipated = getSingleCount(conn, "SELECT COUNT(DISTINCT student_id) FROM participations WHERE participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT')");
            int stuAbsent = getSingleCount(conn, "SELECT COUNT(DISTINCT student_id) FROM attendance WHERE attendance_status = 'ABSENT'");
            int stuMulti = getSingleCount(conn, "SELECT COUNT(*) FROM (SELECT student_id FROM participations GROUP BY student_id HAVING COUNT(*) > 1)");

            DualLogger.printf("%-40s : %d\n", "Total Registered Students", totalStudents);
            DualLogger.printf("%-40s : %d\n", "Students with Event Registrations", stuWithReg);
            DualLogger.printf("%-40s : %d\n", "Students with Zero Registrations", stuZeroReg);
            DualLogger.printf("%-40s : %d\n", "Students Participated", stuParticipated);
            DualLogger.printf("%-40s : %d\n", "Students Not Attended", stuAbsent);
            DualLogger.printf("%-40s : %d\n", "Students with Multiple Participations", stuMulti);

            // 4. Department-wise Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("            DEPARTMENT-WISE OVERVIEW");
            DualLogger.println("---");
            String deptSql = "SELECT s.department, COUNT(DISTINCT s.student_id) as count " +
                             "FROM students s GROUP BY s.department ORDER BY count DESC";
            try (PreparedStatement ps = conn.prepareStatement(deptSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DualLogger.printf("%-40s : %d\n", rs.getString("department"), rs.getInt("count"));
                }
            }

            // 5. Event Registration Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("              EVENT REGISTRATION OVERVIEW");
            DualLogger.println("---");
            int availableSeats = getSingleCount(conn, 
                "SELECT SUM(MAX(0, e.max_participants - IFNULL(r.cnt, 0))) FROM events e " +
                "LEFT JOIN (SELECT event_id, COUNT(*) as cnt FROM registrations WHERE status='REGISTERED' GROUP BY event_id) r " +
                "ON e.event_id = r.event_id WHERE e.event_status IN ('OPEN', 'UPCOMING', 'ONGOING')");

            DualLogger.printf("%-40s : %d\n", "Total Registrations", totalRegs);
            DualLogger.printf("%-40s : %d\n", "Active Registrations", activeRegs);
            DualLogger.printf("%-40s : %d\n", "Cancelled Registrations", cancelledRegs);
            DualLogger.printf("%-40s : %d\n", "Waitlisted Registrations", waitlistedRegs);
            DualLogger.printf("%-40s : %d\n", "Available Event Seats", availableSeats);

            // 6. Attendance Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("               ATTENDANCE OVERVIEW");
            DualLogger.println("---");
            double attPct = (totalPresent + totalAbsent) > 0 ? 
                (totalPresent * 100.0) / (totalPresent + totalAbsent) : 0.0;
            DualLogger.printf("%-40s : %d\n", "Total Present", totalPresent);
            DualLogger.printf("%-40s : %d\n", "Total Absent", totalAbsent);
            DualLogger.printf("%-40s : %.2f%%\n", "Overall Attendance Percentage", attPct);

            // 7. Event Popularity Overview
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("             EVENT POPULARITY OVERVIEW");
            DualLogger.println("---");
            String popSql = "SELECT e.event_name, COUNT(r.registration_id) as cnt " +
                            "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                            "GROUP BY e.event_id ORDER BY cnt DESC, e.event_name ASC LIMIT 2";
            try (PreparedStatement ps = conn.prepareStatement(popSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DualLogger.printf("%-40s : %s (%d registrations)\n", "Most Registered Event", rs.getString("event_name"), rs.getInt("cnt"));
                }
                if (rs.next()) {
                    DualLogger.printf("%-40s : %s (%d registrations)\n", "Second Most Registered Event", rs.getString("event_name"), rs.getInt("cnt"));
                }
            }
            String leastSql = "SELECT e.event_name, COUNT(r.registration_id) as cnt " +
                              "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                              "WHERE e.event_status != 'CANCELLED' " +
                              "GROUP BY e.event_id ORDER BY cnt ASC, e.event_name ASC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(leastSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DualLogger.printf("%-40s : %s (%d registrations)\n", "Least Registered Event", rs.getString("event_name"), rs.getInt("cnt"));
                }
            }

            // 8. Top Participating Students
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("              TOP PARTICIPATING STUDENTS");
            DualLogger.println("---");
            DualLogger.printf("%-14s %-22s %-12s\n", "Student ID", "Student Name", "Total Events");
            DualLogger.println("------------------------------------------------------------");
            String topStuSql = "SELECT s.student_id, s.student_name, COUNT(DISTINCT p.event_id) as cnt " +
                               "FROM students s JOIN participations p ON s.student_id = p.student_id " +
                               "WHERE p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT') " +
                               "GROUP BY s.student_id ORDER BY cnt DESC, s.student_name ASC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(topStuSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DualLogger.printf("%-14s %-22s %-12d\n", 
                        rs.getString("student_id"), 
                        rs.getString("student_name"), 
                        rs.getInt("cnt"));
                }
            }

            // 9. Quick Alerts
            DualLogger.println();
            DualLogger.println("---");
            DualLogger.println("                 QUICK ALERTS");
            DualLogger.println("---");
            int fullEvents = getSingleCount(conn, 
                "SELECT COUNT(*) FROM (SELECT e.event_id FROM events e " +
                "JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                "GROUP BY e.event_id HAVING COUNT(*) >= e.max_participants)");
            int unattenedStudents = getSingleCount(conn, 
                "SELECT COUNT(DISTINCT student_id) FROM attendance WHERE attendance_status='ABSENT'");
            String tomorrowStr = today.plusDays(1).toString();
            int tmrwEvents = getSingleCount(conn, "SELECT COUNT(*) FROM events WHERE event_date = '" + tomorrowStr + "'");
            int lowRegEvents = getSingleCount(conn, 
                "SELECT COUNT(*) FROM events e WHERE e.event_status IN ('OPEN', 'UPCOMING') " +
                "AND (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id AND r.status='REGISTERED') < 2");
            int highActiveStudents = getSingleCount(conn, 
                "SELECT COUNT(*) FROM (SELECT student_id FROM participations GROUP BY student_id HAVING COUNT(*) >= 3)");

            DualLogger.printf("[WARNING] %d event(s) have reached maximum participant capacity.\n", fullEvents);
            DualLogger.printf("[WARNING] %d student(s) have not attended registered events.\n", unattenedStudents);
            DualLogger.printf("[WARNING] %d event(s) are scheduled tomorrow (%s).\n", tmrwEvents, tomorrowStr);
            DualLogger.printf("[WARNING] %d event(s) have low registration.\n", lowRegEvents);
            DualLogger.printf("[ALERT]   %d student(s) have participated in 3 or more campus events.\n", highActiveStudents);
            DualLogger.println("---");

        } catch (SQLException e) {
            DualLogger.error("Failed to load dashboard metrics: " + e.getMessage());
        }
    }

    private static void handleSubOption(int option) {
        try (Connection conn = DatabaseManager.getConnection()) {
            switch (option) {
                case 1:
                    DualLogger.success("Dashboard metrics refreshed successfully.");
                    break;
                case 2:
                    DualLogger.subHeader("STUDENT SUMMARY");
                    DualLogger.printf("%-10s %-12s %-22s %-25s %-6s\n", "ID", "Reg No", "Name", "Department", "Year");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM students ORDER BY student_id LIMIT 15");
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DualLogger.printf("%-10s %-12s %-22s %-25s %-6d\n",
                                rs.getString("student_id"),
                                rs.getString("register_number"),
                                rs.getString("student_name"),
                                rs.getString("department"),
                                rs.getInt("year_of_study"));
                        }
                    }
                    InputReader.pause();
                    break;
                case 3:
                    DualLogger.subHeader("EVENT SUMMARY");
                    DualLogger.printf("%-10s %-26s %-14s %-12s %-10s\n", "Event ID", "Name", "Type", "Date", "Status");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM events ORDER BY event_date ASC");
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DualLogger.printf("%-10s %-26s %-14s %-12s %-10s\n",
                                rs.getString("event_id"),
                                rs.getString("event_name"),
                                rs.getString("event_type"),
                                rs.getString("event_date"),
                                rs.getString("event_status"));
                        }
                    }
                    InputReader.pause();
                    break;
                case 4:
                    DualLogger.subHeader("REGISTRATION SUMMARY");
                    int total = getSingleCount(conn, "SELECT COUNT(*) FROM registrations");
                    int active = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status='REGISTERED'");
                    int cancel = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status='CANCELLED'");
                    int wait = getSingleCount(conn, "SELECT COUNT(*) FROM registrations WHERE status='WAITLISTED'");
                    DualLogger.printf("Total: %d | Active: %d | Cancelled: %d | Waitlisted: %d\n", total, active, cancel, wait);
                    InputReader.pause();
                    break;
                case 5:
                    DualLogger.subHeader("ATTENDANCE SUMMARY");
                    int pres = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_status='PRESENT'");
                    int abs = getSingleCount(conn, "SELECT COUNT(*) FROM attendance WHERE attendance_status='ABSENT'");
                    double pct = (pres + abs > 0) ? (pres * 100.0) / (pres + abs) : 0.0;
                    DualLogger.printf("Present: %d | Absent: %d | Attendance Rate: %.2f%%\n", pres, abs, pct);
                    InputReader.pause();
                    break;
                case 6:
                    DualLogger.subHeader("DEPARTMENT-WISE PARTICIPATION SUMMARY");
                    DualLogger.printf("%-32s %-10s %-15s %-15s\n", "Department", "Students", "Registrations", "Participations");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM v_department_summary ORDER BY total_participations DESC");
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DualLogger.printf("%-32s %-10d %-15d %-15d\n",
                                rs.getString("department"),
                                rs.getInt("total_students"),
                                rs.getInt("total_registrations"),
                                rs.getInt("total_participations"));
                        }
                    }
                    InputReader.pause();
                    break;
                case 7:
                    DualLogger.subHeader("EVENT POPULARITY SUMMARY");
                    DualLogger.printf("%-10s %-26s %-12s %-12s\n", "Event ID", "Event Name", "Capacity", "Registered");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT e.event_id, e.event_name, e.max_participants, COUNT(r.registration_id) as cnt " +
                            "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                            "GROUP BY e.event_id ORDER BY cnt DESC LIMIT 8");
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DualLogger.printf("%-10s %-26s %-12d %-12d\n",
                                rs.getString("event_id"),
                                rs.getString("event_name"),
                                rs.getInt("max_participants"),
                                rs.getInt("cnt"));
                        }
                    }
                    InputReader.pause();
                    break;
                case 8:
                    DualLogger.subHeader("TOP PARTICIPATING STUDENTS");
                    DualLogger.printf("%-10s %-12s %-22s %-20s %-8s\n", "Rank", "Student ID", "Student Name", "Department", "Events");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT s.student_id, s.student_name, s.department, COUNT(DISTINCT p.event_id) as cnt " +
                            "FROM students s JOIN participations p ON s.student_id = p.student_id " +
                            "GROUP BY s.student_id ORDER BY cnt DESC, s.student_name ASC LIMIT 10");
                         ResultSet rs = ps.executeQuery()) {
                        int rk = 1;
                        while (rs.next()) {
                            DualLogger.printf("%-10d %-12s %-22s %-20s %-8d\n",
                                rk++,
                                rs.getString("student_id"),
                                rs.getString("student_name"),
                                rs.getString("department"),
                                rs.getInt("cnt"));
                        }
                    }
                    InputReader.pause();
                    break;
                case 9:
                    DualLogger.subHeader("UPCOMING CAMPUS EVENTS");
                    DualLogger.printf("%-10s %-26s %-12s %-15s %-10s\n", "ID", "Name", "Date", "Venue", "Seats Left");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT e.event_id, e.event_name, e.event_date, e.venue, " +
                            "(e.max_participants - COUNT(CASE WHEN r.status='REGISTERED' THEN 1 END)) as available " +
                            "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id " +
                            "WHERE e.event_status IN ('OPEN', 'UPCOMING') " +
                            "GROUP BY e.event_id ORDER BY e.event_date ASC");
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DualLogger.printf("%-10s %-26s %-12s %-15s %-10d\n",
                                rs.getString("event_id"),
                                rs.getString("event_name"),
                                rs.getString("event_date"),
                                rs.getString("venue"),
                                rs.getInt("available"));
                        }
                    }
                    InputReader.pause();
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Error executing dashboard view: " + e.getMessage());
            InputReader.pause();
        }
    }

    private static int getSingleCount(Connection conn, String query) {
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Return 0 on error
        }
        return 0;
    }
}
