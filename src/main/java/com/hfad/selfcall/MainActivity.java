package com.hfad.selfcall;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hfad.selfcall.Classes.AddContact;


import java.util.ArrayList;


/**
 * Created by user-dis2 on 7/25/2016.
 */


public class MainActivity extends Activity {
    public static int status = 0;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("MY_RELOAD", "MainActivity");



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        status = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        status = 1;
    }

    private void showContacts() {
        final ArrayList<String[]> contacts = AuthActivity.mch.getContacts(); //return ArrayList<String[]>{id, name, phoneNumber, description}
        String[] names = new String[contacts.size()];
        for(int i = 0; i < contacts.size(); i++) {
            names[i] = contacts.get(i)[1];
        }

        final ListView lvMain = (ListView) findViewById(R.id.lvMain);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String[] contact = contacts.get(position);
                Intent intent = new Intent(MainActivity.this, AddContact.class);
                intent.putExtra("NUMBER", contact[2]);
                intent.putExtra("NAME", contact[1]);
                intent.putExtra("DESCRIPTION", contact[3]);
                startActivity(intent);
            }
        });
    }


    public void onClickRemoveContacts(View view) {
        AuthActivity.mch.removeAllContacts();
    }

    public void onClickShowContacts(View view) {
        showContacts();
    }



}