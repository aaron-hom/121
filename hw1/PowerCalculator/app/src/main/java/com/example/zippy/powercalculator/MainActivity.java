package com.example.zippy.powercalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final EditText power1 = findViewById(R.id.power1);

        final Button button = findViewById(R.id.eq);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView standard = (TextView)findViewById(R.id.result);
                EditText power = (EditText) findViewById(R.id.power);
                double num = Double.parseDouble(power.getText().toString());
                double result = Math.pow(2, num);
                String fin = String.format("%.2f", result);
                standard.setText(fin);
            }
        });

        final Button button2 = findViewById(R.id.eq2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView standard = (TextView)findViewById(R.id.result2);
                EditText power = (EditText) findViewById(R.id.power2);
                double num = Double.parseDouble(power.getText().toString());
                double result = Math.pow(3, num);
                String fin = String.format("%.2f", result);
                standard.setText(fin);
            }
        });

        final Button button3 = findViewById(R.id.eq3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView standard = (TextView)findViewById(R.id.result3);
                EditText power = (EditText) findViewById(R.id.power3);
                double num = Double.parseDouble(power.getText().toString());
                double result = Math.pow(4, num);
                String fin = String.format("%.2f", result);
                standard.setText(fin);
            }
        });

        final Button button4 = findViewById(R.id.eq4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView standard = (TextView)findViewById(R.id.result4);
                EditText power = (EditText) findViewById(R.id.power4);
                double num = Double.parseDouble(power.getText().toString());
                double result = Math.pow(5, num);
                String fin = String.format("%.2f", result);
                standard.setText(fin);
            }
        });
    }
}

