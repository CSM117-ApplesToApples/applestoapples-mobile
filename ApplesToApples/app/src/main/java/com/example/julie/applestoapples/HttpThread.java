package com.example.julie.applestoapples;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Julie on 2/29/16.
*/

public class HttpThread extends AsyncTask<String, String, JSONObject>{

    @Override
    protected JSONObject doInBackground(String... args){
        JSONObject ret = null;
        try {
           //Set up request and send it
            URL url = new URL(args[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);


            //Read the response
            int responseCode = conn.getResponseCode();
            System.out.println("-------GOT HTTP Connection --------");


            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();

                in = new BufferedInputStream(conn.getInputStream());
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                conn.disconnect();
                Log.i("HttpThread", "Url: "+url + " Response: "+result.toString());
                ret = new JSONObject(result.toString());

             } else {
                System.out.println("Bad response code, request failed");
            }
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e) {
            // error_log( url creation failed )
            e.printStackTrace();
        } catch (IOException e) {
            // error_log( connection open failed )
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ret;
    }
}
