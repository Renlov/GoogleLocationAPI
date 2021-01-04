package com.example.locationapi;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button startLocation, stopLocation;
    private TextView locationTextView, locationUpdateTimeTextView;

    //Класс для определения местоположения
    private FusedLocationProviderClient fusedLocationClient;
    //Для доступа к настройкам
    private SettingsClient settingsClient;
    //Для сохранения данных запроса
    private LocationRequest locationRequest;
    //Для определения настроек устройства
    private LocationSettingsRequest locationSettingsRequest;
    //Используется для событий определения местоположения
    private LocationCallback locationCallback;
    private Location currentLocation;

    private boolean isLocationActive;
    private String locationUpdateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocation = findViewById(R.id.startLocation);
        stopLocation = findViewById(R.id.stopLocation);
        locationTextView = findViewById(R.id.locationTextView);
        locationUpdateTimeTextView = findViewById(R.id.locationUpdateTimeTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        buildLocationRequest();
        buildLocationCallBack();
        buildLocationSettingRequest();


    }

    private void buildLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();

                locationTextView.setText("" + currentLocation.getLatitude() +
                        "/" + currentLocation.getLongitude());

                locationUpdateTimeTextView.setText(DateFormat.getTimeInstance().format(new Date()));
            }
        };

    }

    private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}