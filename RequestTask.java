package com.cs130.apartmates.base;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestTask extends AsyncTask<String, String, JSONObject> {
    private HttpURLConnection conn;
    private OutputStream out;
    private InputStream in;

    @Override
    protected JSONObject doInBackground(String... args) {
        try {
            URL url = new URL(args[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(args[2]);
            conn.setDoInput(true);
            if (conn.getRequestMethod() == "POST") {
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");


            if (args[1] != null) {
                JSONObject jsonObject = new JSONObject(args[1]);
                out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                out.close();
            }

            conn.connect();

            int status = conn.getResponseCode();

            if (status >= 400) {
                in = conn.getErrorStream();
            } else {
                in = conn.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            in.close();
            System.err.println(result.toString());
            return new JSONObject(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return null;
    }
}
