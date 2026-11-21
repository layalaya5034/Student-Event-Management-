package com.smartcampus.event;

import com.smartcampus.database.DatabaseManager;
import com.smartcampus.util.DualLogger;
import com.smartcampus.util.InputReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMenu {

    public static void showMenu() {
        DualLogger.setModule("EventManagement");
        while (true) {
            DualLogger.println();
            DualLogger.println("============================================================");
            DualLogger.println("                      EVENT MANAGEMENT");
            DualLogger.println("============================================================");
            DualLogger.println("1.  Create New Event");
            DualLogger.println("2.  View All Events");
            DualLogger.println("3.  Search Event");
            DualLogger.println("4.  Update Event Details");
            DualLogger.println("5.  Delete Event");
            DualLogger.println("6.  View Event Details");
            DualLogger.println("7.  View Events by Category");
            DualLogger.println("8.  View Upcoming Events");
            DualLogger.println("9.  View Completed Events");
            DualLogger.println("10. View Cancelled Events");
            DualLogger.println("11. View Event Capacity");
            DualLogger.println("12. View Most Popular Events");
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
                    createEvent(conn);
                    break;
                case 2:
                    viewAllEvents(conn);
                    break;
                case 3:
                    searchEvent(conn);
                    break;
                case 4:
                    updateEvent(conn);
                    break;
                case 5:
                    deleteEvent(conn);
                    break;
                case 6:
                    viewEventDetails(conn);
                    break;
                case 7:
                    viewByCategory(conn);
                    break;
                case 8:
                    viewUpcomingEvents(conn);
                    break;
                case 9:
                    viewCompletedEvents(conn);
                    break;
                case 10:
                    viewCancelledEvents(conn);
                    break;
                case 11:
                    viewEventCapacity(conn);
                    break;
                case 12:
                    viewPopularEvents(conn);
                    break;
            }
        } catch (SQLException e) {
            DualLogger.error("Database operation failed: " + e.getMessage());
        }
        InputReader.pause();
    }

    private static void createEvent(Connection conn) throws SQLException {
        DualLogger.subHeader("CREATE NEW EVENT");
        String eventId = InputReader.readString("Enter Event ID (e.g. EVT121): ", false).toUpperCase();

        // Check unique ID
        String checkSql = "SELECT COUNT(*) FROM events WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                DualLogger.error("Event ID '" + eventId + "' already exists! Event IDs must be unique.");
                return;
            }
        }

        String eventName = InputReader.readString("Enter Event Name: ", false);
        
        DualLogger.println("Select Event Category:");
        DualLogger.println("1. Technical\n2. Workshop\n3. Seminar\n4. Cultural\n5. Sports\n6. Competition\n7. Symposium\n8. Other");
        int typeChoice = InputReader.readInt("Choose Category (1-8): ", 1, 8);
        String[] types = {"Technical", "Workshop", "Seminar", "Cultural", "Sports", "Competition", "Symposium", "Other"};
        String eventType = types[typeChoice - 1];

        String eventDate = InputReader.readDate("Enter Event Date (YYYY-MM-DD)", "2026-12-01");
        String venue = InputReader.readString("Enter Venue: ", false);
        int maxPart = InputReader.readInt("Enter Maximum Participant Count (> 0): ", 1, 5000);
        String desc = InputReader.readString("Enter Event Description (optional): ", true);

        String insertSql = "INSERT INTO events (event_id, event_name, event_type, event_date, venue, max_participants, event_status, description) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'OPEN', ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, eventId);
            ps.setString(2, eventName);
            ps.setString(3, eventType);
            ps.setString(4, eventDate);
            ps.setString(5, venue);
            ps.setInt(6, maxPart);
            ps.setString(7, desc.isEmpty() ? null : desc);
            ps.executeUpdate();
            DualLogger.success("Event created successfully with ID: " + eventId);
        }
    }

    private static void viewAllEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("ALL CAMPUS EVENTS");
        DualLogger.printf("%-10s %-26s %-14s %-12s %-18s %-10s %-10s\n", "Event ID", "Event Name", "Type", "Date", "Venue", "Capacity", "Status");
        DualLogger.println("----------------------------------------------------------------------------------------------------------");
        String sql = "SELECT * FROM events ORDER BY event_date ASC";
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-14s %-12s %-18s %-10d %-10s\n",
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("event_type"),
                    rs.getString("event_date"),
                    rs.getString("venue"),
                    rs.getInt("max_participants"),
                    rs.getString("event_status"));
                count++;
            }
        }
        DualLogger.println("----------------------------------------------------------------------------------------------------------");
        DualLogger.println("Total Events: " + count);
    }

    private static void searchEvent(Connection conn) throws SQLException {
        DualLogger.subHeader("SEARCH EVENT");
        String term = InputReader.readString("Enter Event ID or Name to search: ", false);
        String sql = "SELECT * FROM events WHERE UPPER(event_id) LIKE ? OR UPPER(event_name) LIKE ? ORDER BY event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + term.toUpperCase() + "%");
            ps.setString(2, "%" + term.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    DualLogger.printf("%-10s %-26s %-14s %-12s %-18s %-10s\n", "Event ID", "Name", "Type", "Date", "Venue", "Status");
                    DualLogger.println("---------------------------------------------------------------------------------------------");
                    found = true;
                }
                DualLogger.printf("%-10s %-26s %-14s %-12s %-18s %-10s\n",
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getString("event_type"),
                    rs.getString("event_date"),
                    rs.getString("venue"),
                    rs.getString("event_status"));
            }
            if (!found) {
                DualLogger.warning("No event matching '" + term + "' was found.");
            }
        }
    }

    private static void updateEvent(Connection conn) throws SQLException {
        DualLogger.subHeader("UPDATE EVENT DETAILS");
        String id = InputReader.readString("Enter Event ID to update: ", false).toUpperCase();
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                DualLogger.error("Event ID '" + id + "' not found.");
                return;
            }
            String curName = rs.getString("event_name");
            String curDate = rs.getString("event_date");
            String curVenue = rs.getString("venue");
            int curCap = rs.getInt("max_participants");
            String curStatus = rs.getString("event_status");

            // Check active registrations so new capacity cannot be lower than existing active registrations
            int activeRegs = 0;
            try (PreparedStatement regPs = conn.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status='REGISTERED'")) {
                regPs.setString(1, id);
                ResultSet rrs = regPs.executeQuery();
                if (rrs.next()) activeRegs = rrs.getInt(1);
            }

            DualLogger.println("Current active registrations: " + activeRegs);
            DualLogger.println("Leave blank to keep existing values.");
            String newName = InputReader.readString("New Event Name [" + curName + "]: ", true);
            String newDate = InputReader.readDate("New Event Date (YYYY-MM-DD)", curDate);
            String newVenue = InputReader.readString("New Venue [" + curVenue + "]: ", true);
            
            int newCap = curCap;
            String capInput = InputReader.readString("New Capacity [" + curCap + "]: ", true);
            if (!capInput.isEmpty()) {
                try {
                    int parsed = Integer.parseInt(capInput);
                    if (parsed < activeRegs) {
                        DualLogger.error("Capacity cannot be set lower than active registrations (" + activeRegs + ")!");
                        return;
                    }
                    newCap = parsed;
                } catch (NumberFormatException ex) {
                    DualLogger.warning("Invalid capacity. Keeping current capacity " + curCap);
                }
            }

            DualLogger.println("Select Status: 1. OPEN 2. UPCOMING 3. ONGOING 4. COMPLETED 5. CANCELLED 6. Keep [" + curStatus + "]");
            int stChoice = InputReader.readInt("Option: ", 1, 6);
            String newStatus = curStatus;
            if (stChoice == 1) newStatus = "OPEN";
            else if (stChoice == 2) newStatus = "UPCOMING";
            else if (stChoice == 3) newStatus = "ONGOING";
            else if (stChoice == 4) newStatus = "COMPLETED";
            else if (stChoice == 5) newStatus = "CANCELLED";

            String updateSql = "UPDATE events SET event_name = ?, event_date = ?, venue = ?, max_participants = ?, event_status = ? WHERE event_id = ?";
            try (PreparedStatement ups = conn.prepareStatement(updateSql)) {
                ups.setString(1, newName.isEmpty() ? curName : newName);
                ups.setString(2, newDate);
                ups.setString(3, newVenue.isEmpty() ? curVenue : newVenue);
                ups.setInt(4, newCap);
                ups.setString(5, newStatus);
                ups.setString(6, id);
                ups.executeUpdate();
                DualLogger.success("Event updated successfully.");
            }
        }
    }

    private static void deleteEvent(Connection conn) throws SQLException {
        DualLogger.subHeader("DELETE EVENT");
        String id = InputReader.readString("Enter Event ID to delete: ", false).toUpperCase();

        // Referential integrity check
        String refSql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(refSql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                DualLogger.error("Cannot delete event '" + id + "': Existing student registrations or attendance records depend on it!");
                DualLogger.warning("To cancel the event safely, update its status to 'CANCELLED'.");
                return;
            }
        }

        String delSql = "DELETE FROM events WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                DualLogger.success("Event deleted successfully.");
            } else {
                DualLogger.error("Event ID '" + id + "' not found.");
            }
        }
    }

    private static void viewEventDetails(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT DETAILS");
        String id = InputReader.readString("Enter Event ID: ", false).toUpperCase();
        String sql = "SELECT * FROM v_event_overview WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DualLogger.println();
                DualLogger.printf("%-10s %-25s %-15s\n", "Event ID", "Event Name", "Event Type");
                DualLogger.println("------------------------------------------------------------");
                DualLogger.printf("%-10s %-25s %-15s\n", rs.getString("event_id"), rs.getString("event_name"), rs.getString("event_type"));
                DualLogger.println("---");
                DualLogger.printf("%-10s %-12s %-18s %-20s\n", "Event ID", "Event Date", "Venue", "Maximum Participants");
                DualLogger.println("------------------------------------------------------------");
                DualLogger.printf("%-10s %-12s %-18s %-20d\n", rs.getString("event_id"), rs.getString("event_date"), rs.getString("venue"), rs.getInt("max_participants"));
                DualLogger.println("---");
                DualLogger.printf("%-10s %-12s %-16s %-10s\n", "Event ID", "Registered", "Available Seats", "Status");
                DualLogger.println("------------------------------------------------------------");
                DualLogger.printf("%-10s %-12d %-16d %-10s\n", rs.getString("event_id"), rs.getInt("active_registrations"), rs.getInt("available_seats"), rs.getString("event_status"));
            } else {
                DualLogger.error("Event ID not found.");
            }
        }
    }

    private static void viewByCategory(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENTS BY CATEGORY");
        DualLogger.println("1. Technical 2. Workshop 3. Seminar 4. Cultural 5. Sports 6. Competition 7. Symposium 8. Other");
        int c = InputReader.readInt("Select category (1-8): ", 1, 8);
        String[] types = {"Technical", "Workshop", "Seminar", "Cultural", "Sports", "Competition", "Symposium", "Other"};
        String chosen = types[c - 1];

        String sql = "SELECT * FROM events WHERE event_type = ? ORDER BY event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chosen);
            ResultSet rs = ps.executeQuery();
            DualLogger.printf("%-10s %-26s %-12s %-18s %-10s\n", "Event ID", "Event Name", "Date", "Venue", "Status");
            DualLogger.println("--------------------------------------------------------------------------------");
            int cnt = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-12s %-18s %-10s\n",
                    rs.getString("event_id"), rs.getString("event_name"),
                    rs.getString("event_date"), rs.getString("venue"), rs.getString("event_status"));
                cnt++;
            }
            DualLogger.println("Total " + chosen + " Events: " + cnt);
        }
    }

    private static void viewUpcomingEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("UPCOMING CAMPUS EVENTS");
        renderEventsByStatus(conn, "'UPCOMING', 'OPEN'");
    }

    private static void viewCompletedEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("COMPLETED CAMPUS EVENTS");
        renderEventsByStatus(conn, "'COMPLETED'");
    }

    private static void viewCancelledEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("CANCELLED CAMPUS EVENTS");
        renderEventsByStatus(conn, "'CANCELLED'");
    }

    private static void renderEventsByStatus(Connection conn, String statuses) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_status IN (" + statuses + ") ORDER BY event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            DualLogger.printf("%-10s %-26s %-14s %-12s %-15s %-10s\n", "Event ID", "Event Name", "Type", "Date", "Venue", "Status");
            DualLogger.println("------------------------------------------------------------------------------------------");
            int c = 0;
            while (rs.next()) {
                DualLogger.printf("%-10s %-26s %-14s %-12s %-15s %-10s\n",
                    rs.getString("event_id"), rs.getString("event_name"),
                    rs.getString("event_type"), rs.getString("event_date"),
                    rs.getString("venue"), rs.getString("event_status"));
                c++;
            }
            DualLogger.println("Count: " + c);
        }
    }

    private static void viewEventCapacity(Connection conn) throws SQLException {
        DualLogger.subHeader("EVENT CAPACITY & UTILIZATION");
        DualLogger.printf("%-10s %-26s %-10s %-12s %-12s %-10s\n", "Event ID", "Event Name", "Capacity", "Registered", "Available", "Status");
        DualLogger.println("---------------------------------------------------------------------------------------");
        String sql = "SELECT * FROM v_event_overview ORDER BY capacity_utilization_pct DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String capStatus = rs.getInt("available_seats") == 0 ? "FULL" : "OPEN";
                DualLogger.printf("%-10s %-26s %-10d %-12d %-12d %-10s\n",
                    rs.getString("event_id"),
                    rs.getString("event_name"),
                    rs.getInt("max_participants"),
                    rs.getInt("active_registrations"),
                    rs.getInt("available_seats"),
                    capStatus);
            }
        }
    }

    private static void viewPopularEvents(Connection conn) throws SQLException {
        DualLogger.subHeader("MOST POPULAR EVENTS BY REGISTRATION");
        DualLogger.printf("%-10s %-26s %-14s %-12s %-12s\n", "Rank", "Event Name", "Type", "Registered", "Capacity");
        DualLogger.println("--------------------------------------------------------------------------------");
        String sql = "SELECT e.event_name, e.event_type, e.max_participants, COUNT(r.registration_id) as cnt " +
                     "FROM events e LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status='REGISTERED' " +
                     "GROUP BY e.event_id ORDER BY cnt DESC, e.event_name ASC LIMIT 6";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int rank = 1;
            while (rs.next()) {
                DualLogger.printf("%-10d %-26s %-14s %-12d %-12d\n",
                    rank++,
                    rs.getString("event_name"),
                    rs.getString("event_type"),
                    rs.getInt("cnt"),
                    rs.getInt("max_participants"));
            }
        }
    }
}
