package com.cs130.apartmates.base;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApartmatesHttpClient {
    private static final String ENDPOINT = "http://backend-apartmates.rhcloud.com";

    //sends request to given url endpoint and returns response in string
    public static JSONObject sendRequest(String address, HashMap<String, String> params, String data, String method) {
        try {
            String fulladdress;
            if (params != null && !params.isEmpty()) {
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                fulladdress = ENDPOINT + address + "?" + result;
            } else {
                fulladdress = ENDPOINT + address;
            }
            System.err.println(fulladdress);

            return new RequestTask().execute(fulladdress, data, method).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

