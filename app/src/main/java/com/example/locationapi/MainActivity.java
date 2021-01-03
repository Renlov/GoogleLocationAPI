package com.example.locationapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button startLocation, stopLocation;
    private TextView locationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocation = findViewById(R.id.startLocation);
        stopLocation = findViewById(R.id.stopLocation);
        locationTextView = findViewById(R.id.locationTextView);



    }
}