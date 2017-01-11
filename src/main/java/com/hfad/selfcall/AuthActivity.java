package com.hfad.selfcall;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.selfcall.Classes.ToastShower;
import com.hfad.selfcall.Classes.Users;
import com.hfad.selfcall.Helpers.MyContactHelper;
import com.hfad.selfcall.Helpers.MyDBHelper;
import com.hfad.selfcall.Helpers.NetHelper;
import com.hfad.selfcall.Net.Authorization;
import com.hfad.selfcall.Net.GetContacts;

public class AuthActivity extends Activity {
    public static String MyId;
    public static MyContactHelper mch = null;
    public static MyDBHelper mdh = null;
    public static Users users = null;
    public static ToastShower t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Log.d("MY_RELOAD", "AuthActivity");
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.PROCESS_OUTGOING_CALLS
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            init();
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MY_RELOAD", "Has no permissions " + permission);
                    return false;
                }
            }
        }
        return true;
    }
    public void init(){
        if (mch == null) {
            mch = new MyContactHelper(this);
            mdh = new MyDBHelper(this);
            users = new Users();
            TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            MyId = telemamanger.getSimSerialNumber();
            new GetContacts(NetHelper.synchContactUrl).execute();
            ComponentName compName = new ComponentName(getApplicationContext(),
                    DeviceAdminSample.class);
            Intent device_policy_manager_Int = new Intent(DevicePolicyManager
                    .ACTION_ADD_DEVICE_ADMIN);
            device_policy_manager_Int.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    compName);
            device_policy_manager_Int.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(device_policy_manager_Int, 1);
        }


        SQLiteDatabase db = mdh.getWritableDatabase();
        Cursor c = db.query(MyDBHelper.TABLE_NAME_AUTH, new String[]{"LOGIN", "NAME", "PASSWORD"}, null, null, null, null, null);
        if(c.moveToFirst()) {
            auth(c.getString(0), c.getString(2), c.getString(1), true);
        }
    }

    @Override
    protected void onResume() {
        Log.d("MY_RELOAD", "AuthActivity onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("MY_RELOAD", "AuthActivity onStart");
        super.onStart();
    }

    public void onClickAuth(View view) {
        TextView e_login = (TextView)findViewById(R.id.auth_login);
        TextView e_password = (TextView)findViewById(R.id.auth_password);
        TextView e_name = (TextView)findViewById(R.id.auth_name);
        String login = String.valueOf(e_login.getText());
        String password = String.valueOf(e_password.getText());
        String name = String.valueOf(e_name.getText());
        if(login != null && password != null) {
            auth(login, password, name, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    public void auth(String login, String password, String name, boolean fromBD) {
        new Authorization(this, login, password, name, fromBD).execute();
    }
}
