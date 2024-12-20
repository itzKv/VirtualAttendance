package com.dnk.virtualattendance.model;

public class AttendanceModel {
    private String attendStartTime;
    private String attendCloseTime;
    private int user_id;
    private String date;
    private int isAttended;  // Status (1 = True || 0 = False)

    public AttendanceModel(int user_id, String date, String attendStartTime, String attendCloseTime) {
        this.attendStartTime = attendStartTime;
        this.attendCloseTime = attendCloseTime;
        this.user_id = user_id;
        this.date = date;
        this.isAttended = attendCloseTime != null ? 1 : 0;
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
  
    public void setIsAttended(String attendCloseTime) {
        this.isAttended = attendCloseTime != null ? 1:0;
    }
  
    public int getIsAttended() {
        return this.isAttended;
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
