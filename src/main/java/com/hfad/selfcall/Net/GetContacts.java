package com.hfad.selfcall.Net;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.MainActivity;
import com.hfad.selfcall.Helpers.NetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user-dis2 on 8/19/2016.
 */
public class GetContacts extends AsyncTask<String , Void, Void> {
    private String url;

    public GetContacts(String url) {
        this.url = url;
    }

    public boolean send() throws IOException {
        ContentValues cv = new ContentValues();

        if(url.equals(NetHelper.synchContactUrl)) {
            ArrayList<String[]> al = AuthActivity.mch.getContacts();
            int i = 0;
            for(String[] s : al) {
                cv.put(String.valueOf(i)+"[NAME]", s[1]);
                cv.put(String.valueOf(i)+"[PHONE]", s[2]);
                cv.put(String.valueOf(i)+"[DESCRIPTION]", s[3]);
                i++;
            }
        } else {
            cv.put("fr", NetHelper.getLastContactGet());
        }
        String res = NetHelper.post(url, cv, 0);
        if(res.equals("") || res.equals("[]")) {
            return false;
        }
        try {
            JSONArray jsonObject = new JSONArray(res);

            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject tmp = (JSONObject) jsonObject.get(i);

                String name = String.valueOf(tmp.get("NAME"));
                String phone = String.valueOf(tmp.get("PHONE"));
                String description =  String.valueOf(tmp.get("DESCRIPTION"));

                long id = -1;
                if(phone != null && name != null) {
                    id = AuthActivity.mch.addContact(phone,name);
                }
                if(description != null && id >= 0) {
                    AuthActivity.users.addUser(id, description);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        NetHelper.setLastContactGet(new Date().getTime());
    }
}
