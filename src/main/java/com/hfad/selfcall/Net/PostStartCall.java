package com.hfad.selfcall.Net;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.hfad.selfcall.Helpers.NetHelper;

import java.io.IOException;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class PostStartCall extends AsyncTask<String, Void, Void> {

    private final ContentValues params;
    public PostStartCall(ContentValues params){
        this.params = params;
    }

    private void send() throws IOException {
        NetHelper.post(NetHelper.startCallUrl, params, 0);
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

}
