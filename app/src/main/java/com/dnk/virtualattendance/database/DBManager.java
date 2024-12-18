package com.dnk.virtualattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public DBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DBManager open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // === ROLE OPERATIONS ===

    public void addRole(RoleModel role) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.ROLE_FIELD_ROLE_NAME, role.getRoleName());
        values.put(DBHelper.ROLE_FIELD_WORKING_START_TIME, role.getWorkingStartTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_END_TIME, role.getWorkingEndTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_SPARE_TIME, role.getWorkingSpareTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_LOCATION, role.getWorkingLocation());
        values.put(DBHelper.ROLE_FIELD_SALARY, role.getSalary());
        database.insert(DBHelper.TABLE_ROLE, null, values);
    }

    public List<RoleModel> getAllRoles() {
        List<RoleModel> roleList = new ArrayList<>();
        String[] columns = new String[]{
                DBHelper.ROLE_FIELD_ID,
                DBHelper.ROLE_FIELD_ROLE_NAME,
                DBHelper.ROLE_FIELD_WORKING_START_TIME,
                DBHelper.ROLE_FIELD_WORKING_END_TIME,
                DBHelper.ROLE_FIELD_WORKING_SPARE_TIME,
                DBHelper.ROLE_FIELD_WORKING_LOCATION,
                DBHelper.ROLE_FIELD_SALARY
        };

        Cursor cursor = database.query(
                DBHelper.TABLE_ROLE,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
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

        cursor.close();
        return roleList;
    }

    public int updateRole(RoleModel role) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.ROLE_FIELD_ROLE_NAME, role.getRoleName());
        values.put(DBHelper.ROLE_FIELD_WORKING_START_TIME, role.getWorkingStartTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_END_TIME, role.getWorkingEndTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_SPARE_TIME, role.getWorkingSpareTime());
        values.put(DBHelper.ROLE_FIELD_WORKING_LOCATION, role.getWorkingLocation());
        values.put(DBHelper.ROLE_FIELD_SALARY, role.getSalary());

        return database.update(
                DBHelper.TABLE_ROLE,
                values,
                DBHelper.ROLE_FIELD_ID + " = ?",
                new String[]{String.valueOf(role.getId())}
        );
    }

    public void deleteRole(RoleModel role) {
        database.delete(
                DBHelper.TABLE_ROLE,
                DBHelper.ROLE_FIELD_ID + " = ?",
                new String[]{String.valueOf(role.getId())}
        );
    }

    public RoleModel getRoleById(int id) {
        String[] columns = new String[]{
                DBHelper.ROLE_FIELD_ID,
                DBHelper.ROLE_FIELD_ROLE_NAME,
                DBHelper.ROLE_FIELD_WORKING_START_TIME,
                DBHelper.ROLE_FIELD_WORKING_END_TIME,
                DBHelper.ROLE_FIELD_WORKING_SPARE_TIME,
                DBHelper.ROLE_FIELD_WORKING_LOCATION,
                DBHelper.ROLE_FIELD_SALARY
        };

        Cursor cursor = database.query(
                DBHelper.TABLE_ROLE,
                columns,
                DBHelper.ROLE_FIELD_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            RoleModel role = new RoleModel();
            role.setId(cursor.getInt(0));
            role.setRoleName(cursor.getString(1));
            role.setWorkingStartTime(cursor.getString(2));
            role.setWorkingEndTime(cursor.getString(3));
            role.setWorkingSpareTime(cursor.getString(4));
            role.setWorkingLocation(cursor.getString(5));
            role.setSalary(cursor.getString(6));
            cursor.close();
            return role;
        }

        cursor.close();
        return null;
    }

    // === USER OPERATIONS ===

    public void addUser(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.USER_FIELD_NAME, user.getName());
        values.put(DBHelper.USER_FIELD_EMAIL, user.getEmail());
        values.put(DBHelper.USER_FIELD_ROLE, user.getRole());
        database.insert(DBHelper.TABLE_USER, null, values);
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();
        String[] columns = new String[]{
                DBHelper.USER_FIELD_ID,
                DBHelper.USER_FIELD_NAME,
                DBHelper.USER_FIELD_EMAIL,
                DBHelper.USER_FIELD_ROLE
        };

        Cursor cursor = database.query(
                DBHelper.TABLE_USER,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                UserModel user = new UserModel();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setRole(cursor.getInt(3));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userList;
    }

    public int updateUser(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.USER_FIELD_NAME, user.getName());
        values.put(DBHelper.USER_FIELD_EMAIL, user.getEmail());
        values.put(DBHelper.USER_FIELD_ROLE, user.getRole());

        return database.update(
                DBHelper.TABLE_USER,
                values,
                DBHelper.USER_FIELD_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
    }

    public void deleteUser(UserModel user) {
        database.delete(
                DBHelper.TABLE_USER,
                DBHelper.USER_FIELD_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
    }

    public UserModel getUserById(String id) {
        String[] columns = new String[]{
                DBHelper.USER_FIELD_ID,
                DBHelper.USER_FIELD_NAME,
                DBHelper.USER_FIELD_EMAIL,
                DBHelper.USER_FIELD_ROLE
        };

        Cursor cursor = database.query(
                DBHelper.TABLE_USER,
                columns,
                DBHelper.USER_FIELD_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            UserModel user = new UserModel();
            user.setId(cursor.getString(0));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setRole(cursor.getInt(3));
            cursor.close();
            return user;
        }

        cursor.close();
        return null;
    }

    public String getRoleNameByEmail(String email) {
        String roleName = null;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();

        // Query untuk join tabel users dan roles
        String query = "SELECT " + DBHelper.TABLE_ROLE + "." + DBHelper.ROLE_FIELD_ROLE_NAME + " " +
                "FROM " + DBHelper.TABLE_USER + " " +
                "INNER JOIN " + DBHelper.TABLE_ROLE + " ON " + DBHelper.TABLE_USER + "." + DBHelper.USER_FIELD_ROLE + " = " + DBHelper.TABLE_ROLE + "." + DBHelper.ROLE_FIELD_ID + " " +
                "WHERE " + DBHelper.TABLE_USER + "." + DBHelper.USER_FIELD_EMAIL + " = ?";

        Log.d("DBManager", "Query: " + query);

        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            roleName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ROLE_FIELD_ROLE_NAME));
        }
        cursor.close();
        db.close();

        return roleName;
    }
}