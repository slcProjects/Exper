package com.slc.exper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.slc.exper.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int PERMISSION_FINE_LOCATION = 99;
    int ctr = 0;
    Location originLocation;

    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address;
    Switch sw_gps, sw_locationsupdates;
    boolean updateOn = false;
    FusedLocationProviderClient fusedLocationProviderClient;  //Location service API.
    LocationRequest locationRequest; // config file for all settings related to fuseLocation
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
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);

        //set all properites of LocationRequest

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000 + FAST_UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(1000)
                .setIntervalMillis(1000 + DEFAULT_UPDATE_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

                .build();


        // add click listener

        sw_gps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(sw_gps.isChecked()){
                    // most accurate
                    locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
                    // we don't want anyways
                    tv_sensor.setText("Using GPS sensor");
                    updateGps();
                }
                else
                {
                    tv_sensor.setText("Using towers or wifi sensor");

                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(sw_locationsupdates.isChecked()){
                    //  tv_sensor.setText("going into map");

                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(i);
                }
                else
                {
                    tv_sensor.setText("Can't get to map");

                }
            }
        });

        updateGps();

    }

    private void updateGps() {
        // get permission from user to track gps
        // get the current location
        // update the ui

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
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
    public void onLocationChanged(Location location)
    {
        ctr ++;
        tv_sensor.setText("Lat"+location.getLatitude()+"Lon"+location.getLongitude() + ctr);

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
}