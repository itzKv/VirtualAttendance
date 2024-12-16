package com.dnk.virtualattendance.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RoleDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String DB_NAME = "ROLE.DB";
    public static final String TABLE_ROLE = "roles";
    public static final String FIELD_ID = "id";
    public static final String FIELD_ROLE_NAME = "role_name";
    public static final String FIELD_WORKING_START_TIME = "working_start_time";
    public static final String FIELD_WORKING_END_TIME = "working_end_time";
    public static final String FIELD_WORKING_SPARE_TIME = "working_spare_time";
    public static final String FIELD_WORKING_LOCATION = "working_location";
    public static final String FIELD_SALARY = "salary";

    public RoleDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_ROLE + "("
                + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_ROLE_NAME + " TEXT, "
                + FIELD_WORKING_START_TIME + " TEXT, "
                + FIELD_WORKING_END_TIME + " TEXT, "
                + FIELD_WORKING_SPARE_TIME + " TEXT, "
                + FIELD_WORKING_LOCATION + " TEXT, "
                + FIELD_SALARY + " TEXT "
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE);
        onCreate(db);
    }
}
