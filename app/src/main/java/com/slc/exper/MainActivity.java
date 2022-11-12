package com.slc.exper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int PERMISSION_FINE_LOCATION = 99;
    Location originLocation;
    LocationCallback locationCallback;


    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address;
    Switch sw_gps;
    Button btn_showMap;
    LinearLayout infoContent;
    LocationData locationData = LocationData.getInstance();
    MediaPlayer mediaPlayer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // give each ui a variable view
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        btn_showMap = findViewById(R.id.btn_showMap);
        infoContent =findViewById(R.id.infoContainer);

        //set all properites of LocationRequest
        locationData.locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000 + FAST_UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(1000)
                .setIntervalMillis(1000 + DEFAULT_UPDATE_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                //  Date currentTime = Calendar.getInstance().getTime();
                //   Location location = locationResult.getLastLocation();
                Log.i("MainActivity", "location call  " + locationData.destinationOne.getPosition().latitude);
                mediaPlayer.setVolume(5,50);
                mediaPlayer.start();
            }
        };



        // add click listener
        sw_gps.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v){
                if(sw_gps.isChecked()){
                    // most accurate
                    locationData.locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
                    // we don't want anyways
                    tv_sensor.setText("Location Updates are on");
                    updateGps();
                    startLocationupdates();
                    String printName = LocationData.getInstance().getName();

                    Toast.makeText(MainActivity.this,"name is " + printName, Toast.LENGTH_SHORT).show();
                    LocationData.getInstance().setName("Bob");
                }
                else
                {
                    stopLocationUpdates();
                    locationData.fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    tv_sensor.setText("Location updates are off");

                }
            }
        });

        updateGps();

        // display map
        btn_showMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btn_showMap.setEnabled(false);
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }

    private void stopLocationUpdates() {

    }

    private void startLocationupdates() {
        locationData.fusedLocationProviderClient.requestLocationUpdates(locationData.locationRequest, locationCallback, null);

    }

    @SuppressLint("MissingPermission")
    private void updateGps() {
        // get permission from user to track gps
        // get the current location
        // update the ui

        locationData.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationData.locationPermissionGranted = true;
            locationData.fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.println(Log.INFO,"Inside onSuccess","Inside onSuccess");

                    originLocation=location;
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        originLocation=location;
                        updateUIValues(location);
                        System.out.println(" permission granted location is in iff ++///////////////"+location);
                    }
                }
            });
        }
        else
        {
            // permissions not granted yet.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        // update all of the text view objects with new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude())
            tv_altitude.setText(String.valueOf(location.hasAltitude()));
        else
            tv_altitude.setText("Not available");
        if(location.hasSpeed())
            tv_speed.setText(String.valueOf(location.hasSpeed()));
        else
            tv_speed.setText("Not available");

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));

        } catch(Exception e)
        {
            tv_address.setText("Unable to get address");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){

            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    updateGps();
                else
                    Toast.makeText(this,"This app requires permissions", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        btn_showMap.setVisibility(View.INVISIBLE);
        infoContent.setVisibility(View.INVISIBLE);

        mediaPlayer = MediaPlayer.create(this, R.raw.boing);

        startLocationupdates();
        Log.i("MainActivity", "onResume");
    }
}