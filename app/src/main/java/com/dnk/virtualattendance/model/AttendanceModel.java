package com.dnk.virtualattendance.model;

public class AttendanceModel {
    private int id;
    private String userId;
    private String date;  // Attendance date in string format (e.g., "2024-11-05")
    private int isAttended;  // Status (1 = True || 0 = False)
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

    public int getIsAttended() {
        return isAttended;
    }

    public void setIsAttended(int isAttended) {
        this.isAttended = isAttended;
    }

    public String getCheckin() {
        return checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }
    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
}