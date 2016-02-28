package com.example.julie.applestoapples;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Vernice on 2/27/16.
 */
public class Http_Handler {
    /**
     * This class will contain logic for all HTTP requests.
     */

    static final public String host = "http://dev.mrerickruiz.com/ata/";

    static public boolean test( String username, String groupID ) {
        final String request = "player?name=" + username +
                "&groupID=" + groupID;
        boolean ret = false;
        URL url = null;
        HttpURLConnection conn = null;
        InputStream in = null;

        /**
         *  TODO: Throw the exception instead of try/catch?
         *  Figure this out when we figure out error handling mechanism. */
        try {
            //Set up request and send it
            url = new URL(host + request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            /**
             * Use this code if we send POST parameters in the body instead of the URI
             conn.setDoInput(true);
             conn.setDoOutput(true);
             conn.setRequestMethod("POST");
             OutputStream os = conn.getOutputStream();
             BufferedWriter writer = new BufferedWriter(
             new OutputStreamWriter(os, "UTF-8"));
             writer.write(request);
             writer.flush();
             writer.close();
             os.close();
             */

            //Read the response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                System.out.println(result.toString());
                ret = true;
            } else {
                System.out.println("Bad response code, request failed");
                ret = false;
            }
            conn.disconnect();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e) {
            // error_log( url creation failed )
            e.printStackTrace();
        } catch (IOException e) {
            // error_log( connection open failed )
            e.printStackTrace();
        }

        return ret;
    }
}