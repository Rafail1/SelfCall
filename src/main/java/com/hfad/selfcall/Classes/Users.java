package com.hfad.selfcall.Classes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.Helpers.MyDBHelper;
import com.hfad.selfcall.MainActivity;

import java.util.ArrayList;

/**
 * Created by user-dis2 on 8/22/2016.
 */
public class Users {

    public void addUser(long id, String description) {
        if(getById(id) != null) {
            updateUser(id, description);
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("USER_ID", id);
        cv.put("DESCRIPTION", description);
        SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
        db.insert(MyDBHelper.TABLE_NAME_USERS, null, cv);
        db.close();
    }
    public void updateUser(long id, String description){
        ContentValues cv = new ContentValues();
        cv.put("DESCRIPTION", description);
        SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
        db.update(MyDBHelper.TABLE_NAME_USERS, cv, "USER_ID = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public String getById(long id) {
        SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
        Cursor c = db.query(MyDBHelper.TABLE_NAME_USERS, new String[]{"DESCRIPTION"}, "USER_ID = ?", new String[]{String.valueOf(id)}, null, null, null);
        String res = null;
        if (c.moveToFirst()) {
           res = c.getString(0);
        }
        return res;
    }

    public ArrayList<String[]> getAll() {
       try {
            SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
            Cursor c = db.query(MyDBHelper.TABLE_NAME_USERS, new String[]{"USER_ID","DESCRIPTION"}, null, null, null, null, null);
            ArrayList<String[]> result = new ArrayList<>();
            while (c.moveToNext()) {
                result.add(new String[]{c.getString(0), c.getString(1)});
            }
            return result;
       } catch (Exception e) {
          e.printStackTrace();
       }
       return null;
    }

}
