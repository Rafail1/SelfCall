package com.hfad.selfcall.Net;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.Helpers.MyDBHelper;
import com.hfad.selfcall.Helpers.NetHelper;
import com.hfad.selfcall.MainActivity;

import java.io.IOException;

/**
 * Created by user-dis2 on 8/31/2016.
 */
public class Authorization extends AsyncTask<String, Void, Void> {
    private final AuthActivity activity;
    private final boolean fromBD;
    private String login;
    private String password;
    private String name;
    private boolean authorized;
    public Authorization(AuthActivity authActivity, String login, String password, String name, boolean fromBD){
        this.activity = authActivity;
        this.login = login;
        this.password = password;
        this.name = name;
        this.fromBD = fromBD;
    }

    private boolean auth() throws IOException {
        ContentValues cv = new ContentValues();
        cv.put("l",login);
        cv.put("p",password);
        cv.put("n",name);
        String res = NetHelper.post(NetHelper.authUrl, cv, 0);
        if(res == null || res.equals("-1") || res.equals("")) {
            return false;
        }
        return true;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            authorized = auth();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(authorized) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            if(!fromBD) {
                SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("LOGIN", login);
                cv.put("PASSWORD", password);
                cv.put("NAME", name);
                db.insert(MyDBHelper.TABLE_NAME_AUTH, null, cv);
            }
        } else {
            Toast toast = Toast.makeText(activity, "Wrong", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
