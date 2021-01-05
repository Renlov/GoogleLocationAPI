package com.example.locationapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button startLocation, stopLocation;
    private TextView locationTextView, locationUpdateTimeTextView;
    private static final int CHECK_SETTING_CODE = 111;

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
    //Класс, в котором храниться широта и долгота
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

        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdate();
            }
        });

        buildLocationRequest();
        buildLocationCallBack();
        buildLocationSettingRequest();

    }



    private void startLocationUpdate() {

        isLocationActive = true;
        startLocation.setEnabled(false);
        stopLocation.setEnabled(true);

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException =
                                    (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(
                                    MainActivity.this, CHECK_SETTING_CODE
                            );
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String message = "Make location settings on your device";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        isLocationActive = false;
                        startLocation.setEnabled(true);
                        stopLocation.setEnabled(false);

                }
                updateLocationUi();
            }
        });
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

                updateLocationUi();
    }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHECK_SETTING_CODE:
                switch (resultCode) {
                    case Activity
                            .RESULT_OK:
                        Log.d("MainActivity", "User has agreed to change location" + "settings");
                    startLocationUpdate();
                    break;

                    case Activity.RESULT_CANCELED:
                        Log.d("MainActivity", "User has not agreed to change location" + "settings");
                        isLocationActive = false;
                        startLocation.setEnabled(true);
                        stopLocation.setEnabled(false);
                        updateLocationUi();
                        break;

                }
                break;
        }
    }

    private void updateLocationUi() {
        locationTextView.setText("" + currentLocation.getLatitude() +
                "/" + currentLocation.getLongitude());

        locationUpdateTimeTextView.setText(DateFormat.getTimeInstance().format(new Date()));
    }

    private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocationActive && checkLocationPermission()) {
            startLocationUpdate();
        } else if (!checkLocationPermission()) {
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            showSnackBar();
        }

    }

    private void showSnackBar() {

    }
}