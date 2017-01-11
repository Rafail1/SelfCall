package com.hfad.selfcall.Classes;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.MainActivity;
import com.hfad.selfcall.Net.PostContacts;
import com.hfad.selfcall.R;


/**
 * Created by user-dis2 on 8/22/2016.
 */
public class AddContact extends Activity {
    private String number;
    private String name;
    private String description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        Intent intent = getIntent();
        number = intent.getStringExtra("NUMBER");
        if (number == null) {
            number = AuthActivity.mch.getLastNumber();
        } else {
            name = intent.getStringExtra("NAME");
            description = intent.getStringExtra("DESCRIPTION");
        }

        if (number != null) {
            EditText phone = (EditText) findViewById(R.id.add_phone);
            phone.setText(number);
        }
        if (name != null) {
            EditText ename = (EditText) findViewById(R.id.add_name);
            ename.setText(name);
        }
        if (description != null) {
            EditText edescription = (EditText) findViewById(R.id.add_description);
            edescription.setText(description);
        }
    }

    public void onClickCallContact(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    public void onClickAddContacts(View view) {

        EditText name = (EditText)findViewById(R.id.add_name);
        EditText description = (EditText)findViewById(R.id.add_description);

        String tname = String.valueOf(name.getText());
        String tdescription = String.valueOf(description.getText());
        long id = -1;
        ContentValues cv = new ContentValues();
        if(number != null && tname != null) {
            id = AuthActivity.mch.addContact(number,tname);
            cv.put("NAME", tname);
            cv.put("PHONE", number);
        }
        if(tdescription != null && id >= 0) {
            AuthActivity.users.addUser(id, tdescription);
            cv.put("DESCRIPTION", tdescription);
        }

        new PostContacts(cv).execute();

    }

}
