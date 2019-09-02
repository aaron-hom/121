package com.example.dustinadams.listwithjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddMemo extends AppCompatActivity {

    static final public String MYPREFS = "myprefs";
    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    static final public String MY_WEBPAGE = "https://users.soe.ucsc.edu/~dustinadams/CMPS121/assignment3/www/index.html";
    static final public String LOG_TAG = "memos";

    public static final int RequestPermissionCode = 1;
    public static final int RequestPermissionCode1 = 1;

    public JSONArray ja = null;
    public JSONObject jo = null;
    String AudioSavePathInDevice = null;

    MediaRecorder recorder = new MediaRecorder();
    MediaPlayer player = new MediaPlayer();

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the Javascript interface
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        myWebView.loadUrl(MY_WEBPAGE);

    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.

        public boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                    WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                    RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED &&
                    result1 == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermission() {
            ActivityCompat.requestPermissions(AddMemo.this, new
                    String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
        }

        public boolean checkPermissionLocation() {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                    ACCESS_FINE_LOCATION);
            return result == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermissionLocation(){
            ActivityCompat.requestPermissions(AddMemo.this, new
                    String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode1);
        }
        // initialize recorder object
        public void MediaRecorderReady() {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setOutputFile(AudioSavePathInDevice);
        }
        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void record() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    if (checkPermission()) {
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

                        int pos = ja.length();
                        // the path we'll be saving the file to. Notice it is from the external storage
                        // directory
                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        (pos + 1) + ".3gp";

                        MediaRecorderReady();

                        try {
                            // recording starts
                            recorder.prepare();
                            recorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Toast.makeText(AddMemo.this, "Recording",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        requestPermission();
                        record();
                    }
                }
            });
        }

        @JavascriptInterface
        @SuppressLint("MissingPermission")
        public void stop() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    recorder.stop();
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

                    String location = null;
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    List<String> providers = lm.getProviders(true);
                    Location l;
                    if(checkPermissionLocation()) {
                        for (int i = providers.size() - 1; i >= 0; i--) {
                            l = lm.getLastKnownLocation(providers.get(i));
                            String lat = String.format("%.6f", l.getLatitude());
                            String lon = String.format("%.6f", l.getLongitude());
                            location = (lat + " , " + lon);
                            if (l != null) break;
                        }
                    }
                    else{
                        requestPermissionLocation();
                    }
                    final String definiteLocation = location;

                    Date current = new Date();
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy");
                    int pos = ja.length();

                    JSONObject temp = new JSONObject();
                    try{
                        temp.put("first", String.valueOf(pos + 1));
                    }
                    catch (JSONException j){
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
                    Toast.makeText(AddMemo.this, "Stopping",
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        @JavascriptInterface
        public void play() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    // object to play the audio
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(AudioSavePathInDevice);
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.start();// play the audio

                    Toast.makeText(AddMemo.this, "Playing",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void stoprec() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    if(player != null){
                        player.stop(); // stop audio
                        player.release(); // free up memory
                        MediaRecorderReady();
                    }
                    Toast.makeText(AddMemo.this, "Stopping recording",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void exit() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Metho code here*/
                    Toast.makeText(AddMemo.this, "Exiting",
                            Toast.LENGTH_LONG).show();
                    //Pop activity off stack
                    Intent i = new Intent(AddMemo.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }

    }
}

