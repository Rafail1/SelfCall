package com.hfad.selfcall;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by user-dis2 on 8/17/2016.
 */
public class DeviceAdminSample extends DeviceAdminReceiver {

    void showToast(Context context, String msg) {
        String status = msg;
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Enabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "DisableRequested";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "PasswordChanged");
    }

}
