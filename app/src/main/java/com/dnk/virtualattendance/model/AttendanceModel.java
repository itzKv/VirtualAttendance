package com.dnk.virtualattendance.model;

public class AttendanceModel {
    private int id;
    private String userId;
    private String date;  // Attendance date in string format (e.g., "2024-11-05")
    private String status;  // Status (e.g., "present" or "absent")
    private String checkin;
    private String checkout;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCheckin(String date) {
        this.checkin = checkin;
    }
    public void setCheckout(String date) {
        this.checkout = checkout;
    }
}