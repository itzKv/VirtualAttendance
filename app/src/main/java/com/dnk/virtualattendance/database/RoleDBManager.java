package com.dnk.virtualattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.ui.rolesetting.RoleSettingViewModel;

import java.util.ArrayList;
import java.util.List;

public class RoleDBManager {
    private RoleDBHelper roleDBHelper;
    private Context context;
    private SQLiteDatabase database;

    public RoleDBManager(Context c) {
        context = c;
    }

    public RoleDBManager open() {
        roleDBHelper = new RoleDBHelper(context);
        database = roleDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        roleDBHelper.close();
    }

    public void addRole(RoleModel role){
        ContentValues values = new ContentValues();
        values.put(RoleDBHelper.FIELD_ROLE_NAME, role.getRoleName());
        values.put(RoleDBHelper.FIELD_WORKING_START_TIME, role.getWorkingStartTime());
        values.put(RoleDBHelper.FIELD_WORKING_END_TIME, role.getWorkingEndTime());
        values.put(RoleDBHelper.FIELD_WORKING_SPARE_TIME, role.getWorkingSpareTime());
        values.put(RoleDBHelper.FIELD_WORKING_LOCATION, role.getWorkingLocation());
        values.put(RoleDBHelper.FIELD_SALARY, role.getSalary());
        database.insert(RoleDBHelper.TABLE_ROLE, null, values);
    }

    public List<RoleModel> getAllRoles(){
        List<RoleModel> roleList = new ArrayList<>();

        String[] columns = new String[] {
                RoleDBHelper.FIELD_ID,
                RoleDBHelper.FIELD_ROLE_NAME,
                RoleDBHelper.FIELD_WORKING_START_TIME,
                RoleDBHelper.FIELD_WORKING_END_TIME,
                RoleDBHelper.FIELD_WORKING_SPARE_TIME,
                RoleDBHelper.FIELD_WORKING_LOCATION,
                RoleDBHelper.FIELD_SALARY
        };

        Cursor cursor = database.query(
                RoleDBHelper.TABLE_ROLE,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                do {
                    RoleModel role = new RoleModel();
                    role.setId(cursor.getInt(0));
                    role.setRoleName(cursor.getString(1));
                    role.setWorkingStartTime(cursor.getString(2));
                    role.setWorkingEndTime(cursor.getString(3));
                    role.setWorkingSpareTime(cursor.getString(4));
                    role.setWorkingLocation(cursor.getString(5));
                    role.setSalary(cursor.getString(6));
                    roleList.add(role);
                } while (cursor.moveToNext());
            }
        }

        return roleList;
    }

    public int updateRole(RoleModel role){
        ContentValues values = new ContentValues();
        values.put(RoleDBHelper.FIELD_ROLE_NAME, role.getRoleName());
        values.put(RoleDBHelper.FIELD_WORKING_START_TIME, role.getWorkingStartTime());
        values.put(RoleDBHelper.FIELD_WORKING_END_TIME, role.getWorkingEndTime());
        values.put(RoleDBHelper.FIELD_WORKING_SPARE_TIME, role.getWorkingSpareTime());
        values.put(RoleDBHelper.FIELD_WORKING_LOCATION, role.getWorkingLocation());
        values.put(RoleDBHelper.FIELD_SALARY, role.getSalary());

        return database.update(
                RoleDBHelper.TABLE_ROLE,
                values,
                RoleDBHelper.FIELD_ID + " = ?",
                new String[] {String.valueOf(role.getId())});
    }

    public void deleteRole(RoleModel role) {
        database.delete(
                RoleDBHelper.TABLE_ROLE,
                RoleDBHelper.FIELD_ID + " = ?",
                new String[]{String.valueOf(role.getId())});
    }

    public RoleModel getRoleById(int id){
        String[] columns = new String[] {
                RoleDBHelper.FIELD_ID,
                RoleDBHelper.FIELD_ROLE_NAME,
                RoleDBHelper.FIELD_WORKING_START_TIME,
                RoleDBHelper.FIELD_WORKING_END_TIME,
                RoleDBHelper.FIELD_WORKING_SPARE_TIME,
                RoleDBHelper.FIELD_WORKING_LOCATION,
                RoleDBHelper.FIELD_SALARY
        };
        Cursor cursor = database.query(
                RoleDBHelper.TABLE_ROLE,
                columns,
                RoleDBHelper.FIELD_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) {

                RoleModel role = new RoleModel();
                role.setId(cursor.getInt(0));
                role.setRoleName(cursor.getString(1));
                role.setWorkingStartTime(cursor.getString(2));
                role.setWorkingEndTime(cursor.getString(3));
                role.setWorkingSpareTime(cursor.getString(4));
                role.setWorkingLocation(cursor.getString(5));
                role.setSalary(cursor.getString(6));

                return role;
            }
        }

        return null;
    }
}
