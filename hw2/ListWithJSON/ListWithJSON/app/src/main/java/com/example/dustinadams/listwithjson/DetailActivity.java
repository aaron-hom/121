package com.example.dustinadams.listwithjson;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DetailActivity extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray ja = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        String title = i.getStringExtra("first");
        String description = i.getStringExtra("second");
        String time = i.getStringExtra("third");
        String date = i.getStringExtra("fourth");
        String gps = i.getStringExtra("fifth");

        TextView t = (TextView) findViewById(R.id.title_content);
        TextView d = (TextView) findViewById(R.id.event_content);
        TextView ti = (TextView) findViewById(R.id.time_content);
        TextView da = (TextView) findViewById(R.id.date_content);
        TextView g = (TextView) findViewById(R.id.gps_content);

        t.setText(title);
        d.setText(description);
        ti.setText(time);
        da.setText(date);
        g.setText(gps);

        final int garbage = i.getIntExtra("garbage", 0);
        Button b = findViewById(R.id.delete_event);

        //Read in file
        try {
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            // Notice here that we are de-serializing a String object (instead of
            // a JSONObject object) and passing the String to the JSONObject’s
            // constructor. That’s because String is serializable and
            // JSONObject is not. To convert a JSONObject back to a String, simply
            // call the JSONObject’s toString method.
            String j = null;
            try {
                j = (String) o.readObject();
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(j);
                ja = jo.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // Here, initialize a new JSONObject
            jo = new JSONObject();
            ja = new JSONArray();
            try {
                jo.put("data", ja);
            } catch (JSONException j) {
                j.printStackTrace();
            }
        }

        b.setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                int len = ja.length();
                if (ja != null) {
                    ja.remove(garbage);
                }

                try {
                    File f = new File(getFilesDir(), "file.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();
                } catch (IOException e) {
                }

                //pop the activity off the stack
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }
}