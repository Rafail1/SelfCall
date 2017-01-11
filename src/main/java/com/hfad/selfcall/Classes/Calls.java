package com.hfad.selfcall.Classes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.hfad.selfcall.Helpers.MyDBHelper;
import com.hfad.selfcall.Net.PostCalls;

import java.util.Date;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class Calls {
    private SQLiteDatabase db;

    Calls(SQLiteDatabase db){
        this.db = db;
    }

    void addCall(Date date, long duration, String number, int inout, String record){
        ContentValues cv = new ContentValues();

        cv.put("DATE", String.valueOf(date));
        cv.put("DURATION", duration);
        cv.put("WHO", number);
        cv.put("INCOMING", inout);
        cv.put("FILE_RECORD", record);

        db.insert(MyDBHelper.DB_NAME, null, cv);
        new PostCalls(cv).execute();
    }
}
