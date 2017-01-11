package com.hfad.selfcall.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    Context mContext;
    public static final int DB_VERS = 11;
    public static final String DB_NAME = "calls";
    public static final String TABLE_NAME_CALLS = "calls";
    public static final String TABLE_NAME_AUTH = "auth";
    public static final String TABLE_NAME_USERS = "users";
    public static final String TABLE_NAME_LAST_HELPER = "last_helper";
    public MyDBHelper(Context context) {
        super(context, "calls", null, DB_VERS);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_CALLS = "CREATE TABLE "
                + MyDBHelper.TABLE_NAME_CALLS + "( _id INTEGER PRIMARY KEY," +
                 "DATE DATETIME, DURATION INTEGER, WHO INTEGER, INCOMING INTEGER, " +
                " FILE_RECORD TEXT" + ")";

        String CREATE_TABLE_USERS = "CREATE TABLE "
                + MyDBHelper.TABLE_NAME_USERS + "( _id INTEGER PRIMARY KEY," +
                " USER_ID INTEGER," +
                " DESCRIPTION TEXT" + ")";
        String CREATE_TABLE_AUTH = "CREATE TABLE "
                + MyDBHelper.TABLE_NAME_AUTH + "( _id INTEGER PRIMARY KEY," +
                " LOGIN TEXT," +
                " NAME TEXT," +
                " PASSWORD TEXT" + ")";
        String CREATE_TABLE_LAST_HELPER = "CREATE TABLE "
                + MyDBHelper.TABLE_NAME_LAST_HELPER + "( _id INTEGER PRIMARY KEY," +
                " PROPERTY TEXT," +
                " VALUE TEXT" + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_CALLS);
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_LAST_HELPER);
        sqLiteDatabase.execSQL(CREATE_TABLE_AUTH);
        ContentValues cv = new ContentValues();
        cv.put("PROPERTY", "LastContactGet");
        cv.put("VALUE", "0");
        sqLiteDatabase.insert(MyDBHelper.TABLE_NAME_LAST_HELPER, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyDBHelper.TABLE_NAME_CALLS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyDBHelper.TABLE_NAME_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyDBHelper.TABLE_NAME_AUTH);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyDBHelper.TABLE_NAME_LAST_HELPER);
        onCreate(sqLiteDatabase);
    }
}
