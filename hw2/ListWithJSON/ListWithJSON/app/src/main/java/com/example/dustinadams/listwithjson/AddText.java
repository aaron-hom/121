package com.example.dustinadams.listwithjson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.content.Intent;

public class AddText extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray ja = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start up the Location Service

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        final EditText first = findViewById(R.id.user_input1);
        final EditText second = findViewById(R.id.user_input2);
        Button b = findViewById(R.id.button);

        // Read the file


        try{
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            // Notice here that we are de-serializing a String object (instead of
            // a JSONObject object) and passing the String to the JSONObject’s
            // constructor. That’s because String is serializable and
            // JSONObject is not. To convert a JSONObject back to a String, simply
            // call the JSONObject’s toString method.
            String j = null;
            try{
                j = (String) o.readObject();
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(j);
                ja = jo.getJSONArray("data");
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        catch(IOException e){
            // Here, initialize a new JSONObject
            jo = new JSONObject();
            ja = new JSONArray();
            try{
                jo.put("data", ja);
            }
            catch(JSONException j){
                j.printStackTrace();
            }
        }

        b.setOnClickListener(new Button.OnClickListener(){
            @SuppressLint("MissingPermission")
            public void onClick(View v){

                String location = null;
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = lm.getProviders(true);
                Location l;
                for (int i=providers.size()-1; i>=0; i--) {
                    l = lm.getLastKnownLocation(providers.get(i));
                    String lat = String.format("%.6f", l.getLatitude());
                    String lon = String.format("%.6f", l.getLongitude());
                    location = (lat + " , " + lon);
                    if (l != null) break;
                }
                final String definiteLocation = location;

                Date current = new Date();
                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy");

                String firstText = first.getText().toString();
                String secondText = second.getText().toString();
                String thirdText = time.format(current);
                String fourthText = date.format(current);
                String fifthText = definiteLocation;

                JSONObject temp = new JSONObject();
                try {
                    temp.put("first", firstText);
                    temp.put("second", secondText);
                    temp.put("third", thirdText);
                    temp.put("fourth", fourthText);
                    temp.put("fifth", fifthText);
                }
                catch(JSONException j){
                    j.printStackTrace();
                }

                ja.put(temp);

                // write the file
                try{
                    File f = new File(getFilesDir(), "file.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();
                }
                catch(IOException e){

                }

                //pop the activity off the stack
                Intent i = new Intent(AddText.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }
}
