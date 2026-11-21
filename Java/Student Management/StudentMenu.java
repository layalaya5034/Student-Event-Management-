package com.smartcampus.student;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMenu {

    public static void showMenu() {
        DualLogger.setModule("StudentManagement");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                     STUDENT MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Register New Student");
            DualLogger.println("2.  View All Students");
            DualLogger.println("3.  Search Student");
            DualLogger.println("4.  Update Student Details");
            DualLogger.println("5.  Delete Student");
            DualLogger.println("6.  View Student Details");
            DualLogger.println("7.  View Students by Department");
            DualLogger.println("8.  View Students by Year of Study");
            DualLogger.println("9.  View Student Event History");
            DualLogger.println("10. View Student Attendance History");
            DualLogger.println("11. View Student Participation Summary");
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
                    registerStudent(conn);
                    break;
                case 2:
                    viewAllStudents(conn);
                    break;
                case 3:
                    searchStudent(conn);
                    break;
                case 4:
                    updateStudent(conn);
                    break;
                case 5:
                    deleteStudent(conn);
                    break;
                case 6:
                    viewStudentDetails(conn);
                    break;
                case 7:
                    viewByDepartment(conn);
                    break;
                case 8:
                    viewByYear(conn);
                    break;
                case 9:
                    viewStudentEventHistory(conn);
                    break;
                case 10:
                    viewStudentAttendanceHistory(conn);
                    break;
                case 11:
                    viewStudentParticipationSummary(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void registerStudent(Connection conn) throws SQLException {
        DualLogger.subHeader("REGISTER NEW STUDENT");
        String studentId = InputReader.readString("Enter Student ID (e.g. STU150): ", false).toUpperCase();

        // Check if student ID exists
        String checkSql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                DualLogger.error("Student ID already exists! Duplicate IDs are not permitted.");
                return;
            }
        }

        int regNo = InputReader.readInt("Enter Register Number (unique): ", 1, 999999);
        String name = InputReader.readString("Enter Student Name: ", false);
        
        DualLogger.println("Select Department:");
        DualLogger.println("1. Information Technology\n2. Computer Science\n3. Electronics and Communication\n4. Electrical and Electronics\n5. Mechanical\n6. Civil");
        int deptChoice = InputReader.readInt("Choose Department (1-6): ", 1, 6);
        String[] depts = {
            "Information Technology", "Computer Science", 
            "Electronics and Communication", "Electrical and Electronics", 
            "Mechanical", "Civil"
        };
        String department = depts[deptChoice - 1];

        int year = InputReader.readInt("Enter Year of Study (1-4): ", 1, 4);
        String email = InputReader.readString("Enter Email address: ", true);
        String phone = InputReader.readString("Enter Contact Phone number: ", true);

        String insertSql = "INSERT INTO students (student_id, register_number, student_name, department, year_of_study, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, studentId);
            ps.setInt(2, regNo);
            ps.setString(3, name);
            ps.setString(4, department);
            ps.setInt(5, year);
            ps.setString(6, email.isEmpty() ? null : email);
            ps.setString(7, phone.isEmpty() ? null : phone);
            ps.executeUpdate();
            DualLogger.success("Student registered successfully with ID: " + studentId);
        }
    }

    private static void viewAllStudents(Connection conn) throws SQLException {
        DualLogger.subHeader("ALL REGISTERED STUDENTS");
        DualLogger.printf("%-10s %-12s %-22s %-28s %-6s %-25s\n", "Student ID", "Register No", "Student Name", "Department", "Year", "Email");
        DualLogger.println("---------------------------------------------------------------------------------------------------------");
        String sql = "SELECT * FROM students ORDER BY student_id ASC";
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DualLogger.printf("%-10s %-12s %-22s %-28s %-6d %-25s\n",
                    rs.getString("student_id"),
                    rs.getString("register_number"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("year_of_study"),
                    rs.getString("email") != null ? rs.getString("email") : "N/A");
                count++;
            }
        }
        DualLogger.println("---------------------------------------------------------------------------------------------------------");
        DualLogger.println("Total Registered Students: " + count);
    }

    private static void searchStudent(Connection conn) throws SQLException {
        DualLogger.subHeader("SEARCH STUDENT");
        String term = InputReader.readString("Enter Student ID or Name to search: ", false);
        String sql = "SELECT * FROM students WHERE UPPER(student_id) LIKE ? OR UPPER(student_name) LIKE ? ORDER BY student_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + term.toUpperCase() + "%");
            ps.setString(2, "%" + term.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    DualLogger.printf("%-10s %-12s %-22s %-28s %-6s\n", "Student ID", "Register No", "Student Name", "Department", "Year");
                    DualLogger.println("--------------------------------------------------------------------------------");
                    found = true;
                }
                DualLogger.printf("%-10s %-12s %-22s %-28s %-6d\n",
                    rs.getString("student_id"),
                    rs.getString("register_number"),
                    rs.getString("student_name"),
                    rs.getString("department"),
                    rs.getInt("year_of_study"));
            }
            if (!found) {
                DualLogger.warning("No student matching '" + term + "' was found.");
            }
        }
    }

    private static void updateStudent(Connection conn) throws SQLException {
        DualLogger.subHeader("UPDATE STUDENT DETAILS");
        String id = InputReader.readString("Enter Student ID to update: ", false).toUpperCase();
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Student ID '" + id + "' not found.");
                return;
            }
            String currentName = rs.getString("student_name");
            String currentDept = rs.getString("department");
            int currentYear = rs.getInt("year_of_study");
            String currentEmail = rs.getString("email");
            String currentPhone = rs.getString("phone");

            DualLogger.println("Leave field blank to keep current value.");
            String newName = InputReader.readString("New Name [" + currentName + "]: ", true);
            String newEmail = InputReader.readString("New Email [" + (currentEmail != null ? currentEmail : "N/A") + "]: ", true);
            String newPhone = InputReader.readString("New Phone [" + (currentPhone != null ? currentPhone : "N/A") + "]: ", true);

            String finalName = newName.isEmpty() ? currentName : newName;
            String finalEmail = newEmail.isEmpty() ? currentEmail : newEmail;
            String finalPhone = newPhone.isEmpty() ? currentPhone : newPhone;

            String updateSql = "UPDATE students SET student_name = ?, email = ?, phone = ? WHERE student_id = ?";
            try (PreparedStatement ups = conn.prepareStatement(updateSql)) {
                ups.setString(1, finalName);
                ups.setString(2, finalEmail);
                ups.setString(3, finalPhone);
                ups.setString(4, id);
                ups.executeUpdate();
                DualLogger.success("Student details updated successfully for ID: " + id);
            }
        }
    }

    private static void deleteStudent(Connection conn) throws SQLException {
        DualLogger.subHeader("DELETE STUDENT");
        String id = InputReader.readString("Enter Student ID to delete: ", false).toUpperCase();

        // Referential integrity check
        String refSql = "SELECT COUNT(*) FROM registrations WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(refSql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                DualLogger.error("Cannot delete student '" + id + "': Dependent event registration/attendance records exist!");
                DualLogger.warning("To preserve audit history, cancel the student's registrations instead of deleting.");
                return;
            }
        }

        String delSql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                DualLogger.success("Student record deleted successfully.");
            } else {
                DualLogger.error("Student ID '" + id + "' not found.");
            }
        }
    }

    private static void viewStudentDetails(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT DETAILS & ACTIVITY");
        String id = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT s.*, " +
                     "(SELECT COUNT(*) FROM registrations r WHERE r.student_id = s.student_id AND r.status='REGISTERED') as total_reg, " +
                     "(SELECT COUNT(*) FROM attendance a WHERE a.student_id = s.student_id AND a.attendance_status='PRESENT') as total_att " +
                     "FROM students s WHERE s.student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DualLogger.println();
                DualLogger.printf("%-14s %-20s %-25s %-6s\n", "Student ID", "Student Name", "Department", "Year");
                DualLogger.println("----------------------------------------------------------------");
                DualLogger.printf("%-14s %-20s %-25s %-6d\n",
                    rs.getString("student_id"), rs.getString("student_name"),
                    rs.getString("department"), rs.getInt("year_of_study"));
                DualLogger.println("---");
                DualLogger.printf("%-14s %-22s %-22s\n", "Student ID", "Total Registrations", "Total Attended Events");
                DualLogger.println("----------------------------------------------------------------");
                DualLogger.printf("%-14s %-22d %-22d\n",
                    rs.getString("student_id"), rs.getInt("total_reg"), rs.getInt("total_att"));
            } else {
                DualLogger.error("Student ID not found.");
            }
        }
    }

    private static void viewByDepartment(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENTS BY DEPARTMENT");
        String dept = InputReader.readString("Enter Department name (or part of name): ", false);
        String sql = "SELECT * FROM students WHERE UPPER(department) LIKE ? ORDER BY student_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + dept.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-22s %-28s %-6s\n", "Student ID", "Student Name", "Department", "Year");
            DualLogger.println("------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-22s %-28s %-6d\n",
                    rs.getString("student_id"), rs.getString("student_name"),
                    rs.getString("department"), rs.getInt("year_of_study"));
                c++;
            }
            DualLogger.println("Total Students in department filter: " + c);
        }
    }

    private static void viewByYear(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENTS BY YEAR OF STUDY");
        int yr = InputReader.readInt("Enter Year of Study (1-4): ", 1, 4);
        String sql = "SELECT * FROM students WHERE year_of_study = ? ORDER BY student_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, yr);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-22s %-28s\n", "Student ID", "Student Name", "Department");
            DualLogger.println("----------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-22s %-28s\n",
                    rs.getString("student_id"), rs.getString("student_name"), rs.getString("department"));
                c++;
            }
            DualLogger.println("Total Students in Year " + yr + ": " + c);
        }
    }

    private static void viewStudentEventHistory(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT EVENT HISTORY");
        String id = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT r.registration_id, r.registration_date, r.status, e.event_id, e.event_name, e.event_date, e.event_type " +
                     "FROM registrations r JOIN events e ON r.event_id = e.event_id " +
                     "WHERE r.student_id = ? ORDER BY e.event_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-24s %-12s %-12s %-12s\n", "Reg ID", "Event Name", "Event Date", "Category", "Reg Status");
            DualLogger.println("---------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-24s %-12s %-12s %-12s\n",
                    rs.getString("registration_id"),
                    rs.getString("event_name"),
                    rs.getString("event_date"),
                    rs.getString("event_type"),
                    rs.getString("status"));
                c++;
            }
            if (c == 0) DualLogger.warning("No event history found for student " + id);
            else DualLogger.println("Total Event Registrations: " + c);
        }
    }

    private static void viewStudentAttendanceHistory(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT ATTENDANCE HISTORY");
        String id = InputReader.readString("Enter Student ID: ", false).toUpperCase();
        String sql = "SELECT a.attendance_id, a.attendance_date, a.attendance_status, e.event_name " +
                     "FROM attendance a JOIN events e ON a.event_id = e.event_id " +
                     "WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-26s %-14s %-12s\n", "Att ID", "Event Name", "Date", "Status");
            DualLogger.println("-----------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-14s %-12s\n",
                    rs.getString("attendance_id"),
                    rs.getString("event_name"),
                    rs.getString("attendance_date"),
                    rs.getString("attendance_status"));
                c++;
            }
            if (c == 0) DualLogger.warning("No attendance records found for student " + id);
            else DualLogger.println("Total Attendance Records: " + c);
        }
    }

    private static void viewStudentParticipationSummary(Connection conn) throws SQLException {
        DualLogger.subHeader("STUDENT PARTICIPATION SUMMARY");
        String sql = "SELECT * FROM v_student_participation_summary ORDER BY total_participated_events DESC LIMIT 15";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-20s %-14s %-10s %-10s %-10s\n", "Student ID", "Name", "Department", "Regs", "Attended", "Won");
            DualLogger.println("--------------------------------------------------------------------------------");
            while (rs.next()) {
                DualLogger.printf("%-10s %-20s %-14s %-10d %-10d %-10d\n",
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("department").substring(0, Math.min(rs.getString("department").length(), 13)),
                    rs.getInt("total_registered_events"),
                    rs.getInt("total_attended_events"),
                    rs.getInt("wins_count"));
            }
        }
    }
}
