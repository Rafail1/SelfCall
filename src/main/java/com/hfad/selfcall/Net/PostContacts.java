package com.hfad.selfcall.Net;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.hfad.selfcall.Helpers.NetHelper;

import java.io.IOException;

/**
 * Created by user-dis2 on 8/17/2016.
 */
public class PostContacts extends AsyncTask<String, Void, Void> {

    ContentValues contact;

    public PostContacts(ContentValues contact){
        this.contact = contact;
    }

    private void send(){
        try {
            NetHelper.post(NetHelper.putContactUrl, contact, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        send();
        return null;
    }
}
