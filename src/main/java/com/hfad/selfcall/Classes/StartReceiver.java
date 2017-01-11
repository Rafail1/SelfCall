package com.hfad.selfcall.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hfad.selfcall.AuthActivity;

/**
 * Created by user-dis2 on 9/27/2016.
 */
public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, AuthActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}
