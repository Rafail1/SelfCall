package com.hfad.selfcall.Helpers;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.MainActivity;

import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds;
import static android.provider.ContactsContract.PhoneLookup;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class MyContactHelper {

    private Activity activity;
    private String lastNumber;

    public MyContactHelper(Activity activity){
        this.activity = activity;
    }

    /**
     * return ArrayList<String[]>{id, name, phoneNumber, description}
     *
     */
    public ArrayList<String[]> getContacts() {

        ArrayList<String[]> descr = AuthActivity.users.getAll();

        Cursor phones = activity.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                new String[] {CommonDataKinds.Phone.CONTACT_ID,
                        CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NUMBER},null,null, null);
        ArrayList<String[]> al = new ArrayList<>();


        int cid =  phones.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID);
        int cp = phones.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
        int cn = phones.getColumnIndex(CommonDataKinds.Phone.NUMBER);
        while (phones.moveToNext())
        {
            String id = String.valueOf(phones.getInt(cid));
            String name=phones.getString(cp);
            String phoneNumber = phones.getString(cn);
            String description = findDescription(id,descr);

            al.add(new String[]{id, name, phoneNumber, description});
        }
        return al;
    }


    public String findDescription(String sid, ArrayList<String[]> haystack){
        if(haystack == null) {
            return "";
        }
        for(int i = 0; i < haystack.size(); i++) {
            if(haystack.get(i)[0].equals(sid)) {
                return haystack.get(i)[1];
            }
        }
        return "";
    }


    public int getContactIdByNumber(String number) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        int id = -1;

        ContentResolver contentResolver = activity.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                id = contactLookup.getInt(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return id;
    }
     public void removeAllContacts() {

         ContentResolver cr = activity.getContentResolver();
         Cursor cur = activity.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                 null,null, null, null);
         while (cur.moveToNext()) {
             try{
                 String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                 Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                 System.out.println("The uri is " + uri.toString());
                 cr.delete(uri, null, null);
             }
             catch(Exception e)
             {
                 System.out.println(e.getStackTrace());
             }
         }

     }
    public long addContact(String phone, String name) {

        int id = getContactIdByNumber(phone);
        if(id != -1) {
            return id;
        }
        ArrayList<ContentProviderOperation> op = new ArrayList<ContentProviderOperation>();

        op.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Phone.NUMBER, phone)
                .withValue(CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            ContentProviderResult[] res = activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, op);
            long contactID = ContentUris.parseId(res[0].uri);
            ContentValues cv = new ContentValues();
            cv.put("NAME", name);
            cv.put("PHONE", phone);
            return contactID;
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
        return -1;
    }

    public void setLastNumber(String number) {
        this.lastNumber = number;
    }
    public String getLastNumber() {
        return lastNumber;
    }


}
