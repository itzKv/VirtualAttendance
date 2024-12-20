package com.dnk.virtualattendance.model;

public class AttendanceModel {
    private String attendStartTime;
    private String attendCloseTime;
    private int user_id;
    private String date;

    public AttendanceModel(int user_id, String date, String attendStartTime, String attendCloseTime) {
        this.attendStartTime = attendStartTime;
        this.attendCloseTime = attendCloseTime;
        this.user_id = user_id;
        this.date = date;
    }

    public String getAttendStartTime() {
        return attendStartTime;
    }

    public void setAttendStartTime(String attendStartTime) {
        this.attendStartTime = attendStartTime;
    }

    public String getAttendCloseTime() {
        return attendCloseTime;
    }

    public void setAttendCloseTime(String attendCloseTime) {
        this.attendCloseTime = attendCloseTime;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
