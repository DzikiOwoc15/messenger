package com.hfad.messenger2021.Objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class ConnectionToJSON {
    public static JSONObject readAll(HttpURLConnection connection) throws IOException {
        try (InputStreamReader stream = new InputStreamReader(connection.getInputStream())) {
            BufferedReader reader = new BufferedReader(stream);
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                sb.append((char) cp);
            }
            Log.d("Converter", sb.toString());
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
