package com.slc.exper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.Marker;

public class LocationData {
    private static LocationData ourInstance = new LocationData();
    String name;
    public boolean locationPermissionGranted = false;

    public FusedLocationProviderClient fusedLocationProviderClient;  //Location service API.
   public  LocationRequest locationRequest; // config file for all settings related to fuseLocation
    public   Marker destinationOne;

    public static LocationData getInstance() {
        return ourInstance;
    }

    private LocationData() {
        name = "Colin";
    }

    public String getName() {


        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
