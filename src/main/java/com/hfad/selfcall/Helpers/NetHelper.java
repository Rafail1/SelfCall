package com.hfad.selfcall.Helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by user-dis2 on 8/15/2016.
 */
public class NetHelper {

    public static final String putUrl = "http://testcat.by/android/index.php?method=new_file&id="+AuthActivity.MyId;
    public static final String putCallUrl = "http://testcat.by/android/index.php?method=new_call&id="+AuthActivity.MyId;
    public static final String putContactUrl = "http://testcat.by/android/index.php?method=new_contact&id="+AuthActivity.MyId;
    public static final String getContactUrl = "http://testcat.by/android/index.php?method=get_contacts&id="+AuthActivity.MyId;
    public static final String synchContactUrl = "http://testcat.by/android/index.php?method=synch_contacts&id="+AuthActivity.MyId;
    public static String startCallUrl = "http://testcat.by/android/index.php?method=start_call&id="+AuthActivity.MyId;
    public static String authUrl = "http://testcat.by/android/index.php?method=auth&id="+ AuthActivity.MyId;
    public static long lastContactGet = 0;

    public static String get(String sUrl, ContentValues params) throws IOException {
        String q = getQuery(params);
        sUrl += "&"+q;
        URL url = new URL(sUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        return String.valueOf(response);
    }
    public static String post(String sUrl, ContentValues params, int i) throws IOException {
        if(i == 0) {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String q = getQuery(params);
            writer.write(q);
            writer.flush();
            writer.close();
            os.close();
            StringBuilder response = new StringBuilder();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            return String.valueOf(response);
        } else if(i == 1) {
            final String fr = (String) params.get("FILE_RECORD");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadFile(fr);
                }
            }).start();
        }

        return sUrl;
    }

    public static int uploadFile(String selectedFilePath){
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);

        if (!selectedFile.isFile()) {
            return 0;
        } else {
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(putUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer,0,bufferSize);
                while (bytesRead > 0){
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = connection.getResponseCode();
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponseCode;
        }

    }

    private static String getQuery(ContentValues params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set<Map.Entry<String, Object>> s=params.valueSet();
        Iterator itr = s.iterator();


        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            String value =  String.valueOf(me.getValue());

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));
        }

        return result.toString();
    }

    public static long getLastContactGet() {
        if(lastContactGet == 0) {
            SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
            Cursor c = db.query(MyDBHelper.TABLE_NAME_LAST_HELPER, new String[]{"VALUE"}, "PROPERTY = ?", new String[]{"LastContactGet"}, null, null, null);
            if(c.moveToFirst()) {
                long lastCG = Long.parseLong(c.getString(0));
                lastContactGet = lastCG;
            }
        }
        return lastContactGet;
    }
    public static void setLastContactGet(long lastContactGet) {
        SQLiteDatabase db = AuthActivity.mdh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("VALUE", lastContactGet);
        db.update(MyDBHelper.TABLE_NAME_LAST_HELPER, cv, "PROPERTY = ?", new String[]{"LastContactGet"});
        NetHelper.lastContactGet = lastContactGet;
        db.close();
    }
}
