package com.smartcampus.model;

public class Student {
    private String studentId;
    private Integer registerNumber;
    private String studentName;
    private String department;
    private int yearOfStudy;
    private String email;
    private String phone;

    public Student() {}

    public Student(String studentId, Integer registerNumber, String studentName, String department, int yearOfStudy, String email, String phone) {
        this.studentId = studentId;
        this.registerNumber = registerNumber;
        this.studentName = studentName;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
        this.phone = phone;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Integer getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(Integer registerNumber) { this.registerNumber = registerNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
