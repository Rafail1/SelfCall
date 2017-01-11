package com.hfad.selfcall.Net;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.hfad.selfcall.Helpers.NetHelper;

import java.io.IOException;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class PostCalls extends AsyncTask<String, Void, Void> {

    private final ContentValues params;
    public PostCalls(ContentValues params){
        this.params = params;
    }

    private void send(int i) throws IOException {
        NetHelper.post(NetHelper.putCallUrl, params, i);
    }

    @Override
    protected Void doInBackground(String... strings) {
        for(int i = 0; i < 2; i++) {
            try {
                send(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
