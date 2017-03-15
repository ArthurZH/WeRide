package com.example.deanwen.myapplication;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by XiaodongZhou on 8/3/15.
 */
public class RetrieveHistory extends AsyncTask<String, Void, ArrayList<String>> {
    public static String total_money = "";
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    @Override
    public ArrayList<String> doInBackground(String... url) {
        String uuid = "";
        String access_token = url[1];
        ArrayList<String> rs = new ArrayList<String>();
        try {
            JSONObject json1 = readJsonFromUrl(url[0]);
            JSONArray history = (JSONArray) json1.get("history");
            JSONObject obj = (JSONObject) history.get(0);
            uuid = (String) obj.get("uuid");
            String url2 = "https://api.uber.com/v1/requests/" + uuid + "/receipt?access_token=" + access_token;
            JSONObject json2 = readJsonFromUrl(url2);
            String money = "Total:" + (String)json2.get("total_charged");
            rs.add(money);

            String url3 = "https://api.uber.com/v1/me?access_token=" + access_token;
            JSONObject json3 = readJsonFromUrl(url3);
            String last_name = (String) json3.get("last_name");
            String first_name = (String) json3.get("first_name");
            String email = (String) json3.get("email");
            rs.add(last_name + first_name);
            rs.add(email);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
