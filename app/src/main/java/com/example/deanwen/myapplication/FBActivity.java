package com.example.deanwen.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.victorsima.uber.UberClient;

import retrofit.RestAdapter;

public class FBActivity extends ActionBarActivity {
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ArrayList<FriendItem> list = new ArrayList<>();
    private ListView listView;
    private MyAdapter adapter;
    public static DatabaseHandler db;

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("enter onResume");
        list.clear();
        if (AccessToken.getCurrentAccessToken() != null) {
            System.out.println("in onResume, current token is not null. it is: " + AccessToken.getCurrentAccessToken().getToken());
        } else {
            System.out.println("in onResume method, token not found");
        }

        if (AccessToken.getCurrentAccessToken() == null) {
            startActivity(new Intent(FBActivity.this, FBLoginPop.class));
        }

        db = new DatabaseHandler(this);
        db.deleteTable();
        db.createTable();
        System.out.println("ready to fetch data");

        new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/friends", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() != null) {
                            System.out.println("error while fetching friends in FBActivity.java");
                        } else {

                            try {
                                parseResp(response);
                            } catch (JSONException e) {

                            }

                        }
                    }
                }
        ).executeAsync();
    }

    private void parseResp(GraphResponse response) throws JSONException {
        JSONObject json = response.getJSONObject();
        JSONArray arr = json.getJSONArray("data");
        System.out.println("inside pareResp!!!!");
        Bundle params = new Bundle();
        params.putString("fields", "email,name");
        params.putString("access_token", "1506471932977294|488069ddea7fd24610284677fcd3bea3");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jObj = arr.getJSONObject(i);
            String friendId = jObj.getString("id");
            new GraphRequest(
                    null, friendId, params, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject perJson = response.getJSONObject();
                            try {
                                String name = perJson.getString("name");
                                String email = perJson.getString("email");
                                System.out.println("friend email: " + email);
                                list.add(new FriendItem(name, email));
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                System.out.println("parse json error in callback in side" +
                                        "of parseResp in FBActivity.java");
                            }

                        }
                    }


            ).executeAsync();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("enter onCreate");

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_fb);
        listView = (ListView) findViewById(R.id.listView);
        this.adapter = new MyAdapter(list, this);
        listView.setAdapter(this.adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendItem tmp = list.get(position);
                tmp.setSelected(!tmp.isSelected());
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void callUber(View view) {
        //call uber

        List<Record> selectedItems = getSelectedItems();
        for(Record record : selectedItems) {
            record.set_start_time("dummy");
            record.set_end_time("dummy");
            db.addRecord(record);
        }

        Log.d("Read: ", "Reading all records...");
        List<Record> records = db.getAllRecords();
        for(Record r : records) {
            String log = "Email: " + r.get_email()
                    + " ,Name: " + r.get_name()
                    + " ,Start_time: " + r.get_start_time()
                    + " ,End_time: " + r.get_end_time();
            Log.d("Record: ", log);
        }

        PackageManager pm = getPackageManager();

        Intent launchIntent = pm.getLaunchIntentForPackage("com.ubercab");
        // Do something awesome - the app is installed! Launch App.
        startActivity(launchIntent);
    }

    private List<Record> getSelectedItems() {
        List<Record> result = new ArrayList<>();
        for (FriendItem item : list) {
            if (item.isSelected()) {
                result.add(new Record(item.getEmail(), item.getName()));
            }
        }

        return result;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
