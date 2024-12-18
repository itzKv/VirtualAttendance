package com.dnk.virtualattendance.model;

import androidx.annotation.NonNull;

public class RoleModel {
    private int id;
    private String roleName;
    private String workingStartTime;
    private String workingEndTime;
    private String workingSpareTime;
    private String workingLocation;
    private String salary;

    public RoleModel(){}

    public RoleModel(int id, String roleName, String workingStartTime, String workingEndTime, String workingSpareTime, String workingLocation, String salary){
        this.id = id;
        this.roleName = roleName;
        this.workingStartTime = workingStartTime;
        this.workingEndTime = workingEndTime;
        this.workingSpareTime = workingSpareTime;
        this.workingLocation = workingLocation;
        this.salary = salary;
    }

    // Getter and Setter methods for each field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getWorkingStartTime() {
        return workingStartTime;
    }

    public void setWorkingStartTime(String workingStartTime) {
        this.workingStartTime = workingStartTime;
    }

    public String getWorkingEndTime() {
        return workingEndTime;
    }

    public void setWorkingEndTime(String workingEndTime) {
        this.workingEndTime = workingEndTime;
    }

    public String getWorkingSpareTime() {
        return workingSpareTime;
    }

    public void setWorkingSpareTime(String workingSpareTime) {
        this.workingSpareTime = workingSpareTime;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    @Override
    public String toString(){
        return this.roleName;
    }
}
