package com.slc.exper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.slc.exper.databinding.ActivityMapsBinding;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationData locationData = LocationData.getInstance();
    private static final int DEFAULT_ZOOM = 15;
    Button btn_search;


/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // View view = inflater.inflate(R.layout.activity_maps, container, false);

//
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.map, fragment);
        transaction.commit();
       //



        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        View view = binding.getRoot();

        mapFragment.getMapAsync(this);


        return view;
    }

    */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btn_search = findViewById(R.id.btn_search);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // display map
        btn_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MapsActivity.this, FindActivity.class);
                startActivity(i);

               // finish();
            }
        });
    }







    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        updateLocationUI();
        getDeviceLocation();


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng position) {

                MarkerOptions markerOption = new MarkerOptions().
                        position(position).title("new Marker");
                locationData.destinationOne = mMap.addMarker(markerOption);
                btn_search.setVisibility(View.VISIBLE);

                // String printName = LocationData.getInstance().getName();
                //  Toast.makeText(MapsActivity.this,"name is " + printName, Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(),"name is " + printName, Toast.LENGTH_SHORT).show();


            }
        });

        // adding on click listener to marker of google maps.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // on marker click we are getting the title of our marker
                // which is clicked and displaying it in a toast message.
                String markerName = marker.getTitle();
                Toast.makeText(MapsActivity.this,"Checked! " + marker.getTitle(),Toast.LENGTH_SHORT).show();
                // could return this position to the MainActivity.
                // finish();
                return false;
            }
        });
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationData.locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
            } else {
                Log.i("MapActivity", "Does not have permissions");

            }
        } catch (SecurityException e)  {
            Log.i("MapActivity", "security Exception" + e);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationData.locationPermissionGranted) {
                Task<Location> locationResult = locationData.fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.i("MapActivity", "no location");


                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.i("MapActivity", e.getMessage(), e);

        }
    }


}