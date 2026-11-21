package com.smartcampus.model;

public class Attendance {
    private String attendanceId;
    private String registrationId;
    private String studentId;
    private String eventId;
    private String attendanceDate;
    private String attendanceStatus;

    public Attendance() {}

    public Attendance(String attendanceId, String registrationId, String studentId, String eventId, String attendanceDate, String attendanceStatus) {
        this.attendanceId = attendanceId;
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(String attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
}
