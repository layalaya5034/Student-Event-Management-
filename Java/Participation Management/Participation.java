package com.smartcampus.model;

public class Participation {
    private String participationId;
    private String registrationId;
    private String studentId;
    private String eventId;
    private String participationStatus;
    private String remarks;
    private String certificateStatus;

    public Participation() {}

    public Participation(String participationId, String registrationId, String studentId, String eventId, String participationStatus, String remarks, String certificateStatus) {
        this.participationId = participationId;
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.participationStatus = participationStatus;
        this.remarks = remarks;
        this.certificateStatus = certificateStatus;
    }

    public String getParticipationId() { return participationId; }
    public void setParticipationId(String participationId) { this.participationId = participationId; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getParticipationStatus() { return participationStatus; }
    public void setParticipationStatus(String participationStatus) { this.participationStatus = participationStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String certificateStatus) { this.certificateStatus = certificateStatus; }
}
