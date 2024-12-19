package com.dnk.virtualattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "VirtualAttendance.db";
    private static final int DATABASE_VERSION = 2;

    // Table Roles
    public static final String TABLE_ROLE = "roles";
    public static final String ROLE_FIELD_ID = "id";
    public static final String ROLE_FIELD_ROLE_NAME = "role_name";
    public static final String ROLE_FIELD_WORKING_START_TIME = "working_start_time";
    public static final String ROLE_FIELD_WORKING_END_TIME = "working_end_time";
    public static final String ROLE_FIELD_WORKING_SPARE_TIME = "working_spare_time";
    public static final String ROLE_FIELD_WORKING_LOCATION = "working_location";
    public static final String ROLE_FIELD_SALARY = "salary";

    // Table Users
    public static final String TABLE_USER = "users";
    public static final String USER_FIELD_ID = "id";
    public static final String USER_FIELD_NAME = "name";
    public static final String USER_FIELD_EMAIL = "email";
    public static final String USER_FIELD_ROLE = "role";

    // Table Attendance
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String ATTENDANCE_FIELD_ID = "id";
    public static final String ATTENDANCE_FIELD_USER_ID = "user_id";
    public static final String ATTENDANCE_FIELD_DATE = "date";
    public static final String ATTENDANCE_FIELD_STATUS = "status"; // Present, Absent, etc.
    public static final String ATTENDANCE_FIELD_CHECKIN_TIME = "checkin_time";
    public static final String ATTENDANCE_FIELD_CHECKOUT_TIME = "checkout_time";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Query to create roles table
        String createRolesTable = "CREATE TABLE " + TABLE_ROLE + "("
                + ROLE_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ROLE_FIELD_ROLE_NAME + " TEXT, "
                + ROLE_FIELD_WORKING_START_TIME + " TEXT, "
                + ROLE_FIELD_WORKING_END_TIME + " TEXT, "
                + ROLE_FIELD_WORKING_SPARE_TIME + " TEXT, "
                + ROLE_FIELD_WORKING_LOCATION + " TEXT, "
                + ROLE_FIELD_SALARY + " TEXT "
                + ")";
        db.execSQL(createRolesTable);

        // Query to create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USER + "("
                + USER_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_FIELD_NAME + " TEXT, "
                + USER_FIELD_EMAIL + " TEXT, "
                + USER_FIELD_ROLE + " TEXT "
                + ")";
        db.execSQL(createUsersTable);

        // Create Attendance Table
        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE + "("
                + ATTENDANCE_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ATTENDANCE_FIELD_USER_ID + " INTEGER, "
                + ATTENDANCE_FIELD_DATE + " TEXT, "
                + ATTENDANCE_FIELD_STATUS + " TEXT, "
                + ATTENDANCE_FIELD_CHECKIN_TIME + " TEXT, "
                + ATTENDANCE_FIELD_CHECKOUT_TIME + " TEXT, "
                + "FOREIGN KEY(" + ATTENDANCE_FIELD_USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_FIELD_ID + ")"
                + ")";
        db.execSQL(createAttendanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        // Recreate tables
        onCreate(db);
    }

    // Insert dummy data for testing
    public void insertDummyDataAttendance() {
        SQLiteDatabase db = this.getWritableDatabase(); // Access writable database

        String[] dates = {
                "2024-11-01", "2024-11-02", "2024-11-05", "2024-11-10", "2024-11-15"
        };
        String[] statuses = {
                "present", "absent", "present", "absent", "present"
        };

        for (int i = 0; i < dates.length; i++) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.ATTENDANCE_FIELD_USER_ID, "1");  // Assuming user_id is 1
            values.put(DBHelper.ATTENDANCE_FIELD_DATE, dates[i]);
            values.put(DBHelper.ATTENDANCE_FIELD_STATUS, statuses[i]);
            values.put(DBHelper.ATTENDANCE_FIELD_CHECKIN_TIME, "08:00 AM");
            values.put(DBHelper.ATTENDANCE_FIELD_CHECKOUT_TIME, "05:00 PM");

            db.insert(DBHelper.TABLE_ATTENDANCE, null, values);
        }

        db.close();
    }
}
