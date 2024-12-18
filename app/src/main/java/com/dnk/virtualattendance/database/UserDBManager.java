package com.dnk.virtualattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dnk.virtualattendance.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserDBManager {

    private UserDBHelper userDBHelper;
    private Context context;
    private SQLiteDatabase database;

    public UserDBManager(Context c) {
        context = c;
    }
    public UserDBManager open() {
        userDBHelper = new UserDBHelper(context);
        database = userDBHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        userDBHelper.close();
    }
    public void addUser(UserModel user){
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.FIELD_NAME, user.getName());
        values.put(UserDBHelper.FIELD_EMAIL, user.getEmail());
        values.put(UserDBHelper.FIELD_ROLE, user.getRole());
        database.insert(UserDBHelper.TABLE_USER, null, values);
    }

    public List<UserModel> getAllUsers(){
        List<UserModel> userList = new ArrayList<>();

        String[] columns = new String[] {
                UserDBHelper.FIELD_ID,
                UserDBHelper.FIELD_NAME,
                UserDBHelper.FIELD_EMAIL,
                UserDBHelper.FIELD_ROLE
        };

        Cursor cursor = database.query(
                UserDBHelper.TABLE_USER,
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
                    UserModel user = new UserModel();
                    user.setId(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setRole(cursor.getInt(3));
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        }

        return userList;
    }
    public int updateUser(UserModel user){
        ContentValues values = new ContentValues();
        values.put(UserDBHelper.FIELD_NAME, user.getName());
        values.put(UserDBHelper.FIELD_EMAIL, user.getEmail());
        values.put(UserDBHelper.FIELD_ROLE, user.getRole());

        return database.update(
                UserDBHelper.TABLE_USER,
                values,
                UserDBHelper.FIELD_ID + " = ?",
                new String[] {String.valueOf(user.getId())});
    }

    public void deleteUser(UserModel user) {
        database.delete(
                UserDBHelper.TABLE_USER,
                UserDBHelper.FIELD_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    public UserModel getUserById(String id){
        String[] columns = new String[] {
                UserDBHelper.FIELD_ID,
                UserDBHelper.FIELD_NAME,
                UserDBHelper.FIELD_EMAIL,
                UserDBHelper.FIELD_ROLE
        };
        Cursor cursor = database.query(
                UserDBHelper.TABLE_USER,
                columns,
                UserDBHelper.FIELD_ID + " = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) {

                UserModel user = new UserModel();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setRole(cursor.getInt(3));

                return user;
            }
        }

        return null;
    }
}
