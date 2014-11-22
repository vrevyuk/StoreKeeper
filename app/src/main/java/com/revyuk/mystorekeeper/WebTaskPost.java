package com.revyuk.mystorekeeper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Vitaly Revyuk on 30.10.2014.
 */
public class WebTaskPost extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        URL url;
        DataOutputStream outputStream;
        InputStream inputStream;
        HttpURLConnection con = null;
        String str;
        try {
            url = new URL("http://"+params[0]+"/"+params[1]);
            Log.d("XXX", url.toString()+"?"+params[2]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("Content-Length", "" + Integer.toString(params[2].getBytes().length));
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(params[2]);
            outputStream.flush();
            outputStream.close();

            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                str = convertStreamToString(inputStream);
                Log.d("XXX", str);
                inputStream.close();
            } else {
                str = "{\"Error\":\"1\",\"ErrorMessage\":\"" + con.getResponseMessage() + "\"}";
            }
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            str = "{\"Error\":\"1\",\"ErrorMessage\":\"" + e.toString() + "\"}";
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return str;
    }

    String convertStreamToString(java.io.InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}