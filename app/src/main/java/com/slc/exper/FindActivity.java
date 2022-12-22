package com.slc.exper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;



public class FindActivity extends AppCompatActivity  {


    LocationData locationData = LocationData.getInstance();
    LocationCallback locationCallback;
    Button btn_terminate, btn_form;

    // device sensor manager
    private SensorManager SensorManage;
    // define the compass picture that will be use
    private ImageView compassimage;
    // record the angle turned of the compass picture
    private float DegreeStart = 0f;
    //TextView DegreeTV;
    //TextView myLocation;
    TextView distance ,degreeTV, leftVolumeTV, rightVolumeTV, currentVolumeTV;

    static final float MAX_VOLUME = 90f;
    private float currentVolume = MAX_VOLUME * 0.75f - 1;
    private float leftVolume = (float)(Math.log(MAX_VOLUME-(currentVolume))/Math.log(MAX_VOLUME));
    private float rightVolume = (float)(Math.log(MAX_VOLUME-(currentVolume))/Math.log(MAX_VOLUME));


    private float previousDistance  = 0.0f;

    private float previousDegree = -5555f; // very small number

    MediaPlayer mediaPlayer ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
     //   DegreeTV = (TextView) findViewById(R.id.DegreeTV);
      //  myLocation = (TextView) findViewById(R.id.MyLocationTV);
        distance = (TextView) findViewById(R.id.distanceTV);
        degreeTV =(TextView) findViewById(R.id.degreeTV);
        leftVolumeTV = (TextView) findViewById(R.id.leftVolumeTv);
        rightVolumeTV = (TextView) findViewById(R.id.rightVolumeTV);
        currentVolumeTV= (TextView) findViewById(R.id.currentVolumeTv);


        boolean displayed = false;
        btn_terminate = findViewById(R.id.btn_terminate);
        btn_form = findViewById(R.id.btn_form);

        //  destination.setText("Destination: " + locationData.destinationOne.getTitle());

        compassimage = (ImageView) findViewById(R.id.compass_image);
        // TextView that will display the degree
        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                //  Date currentTime = Calendar.getInstance().getTime();
                //   Location location = locationResult.getLastLocation();
               //Log.i("FindActity", "Destination location  " + locationData.destinationOne.getPosition().latitude);

              //  Log.i("FindActity", "Inside location Result  ");
              //  Log.i("FindActity", "My location lat  " + locationResult.getLastLocation().getLatitude());
              //  Log.i("FindActity", "My location long  " + locationResult.getLastLocation().getLongitude());

             //   Log.i("FindActity", "My location bearing  " + locationResult.getLastLocation().getBearing());
                //myLocation.setText("My location: " + locationResult.getLastLocation().getLatitude());

                Location destination = new Location("destination");
                destination.setLatitude(locationData.destinationOne.getPosition().latitude);
                destination.setLongitude(locationData.destinationOne.getPosition().longitude);
            //    Log.i("FindActity", "destination lat  " + locationData.destinationOne.getPosition().latitude);
            //    Log.i("FindActity", "My destination long  " + locationData.destinationOne.getPosition().longitude);

                //Log.i("FindActivity", "Distance is  " + +  locationResult.getLastLocation().distanceTo(destination));
                distance.setText("Distance: " + locationResult.getLastLocation().distanceTo(destination) + " meters");
                float currentDistance = locationResult.getLastLocation().distanceTo(destination);

                // double currentDirection = getBearingBetweenTwoPoints(locationResult.getLastLocation(), locationResult.getLocations().get(locationResult.getLocations().size() - 2));
                double correctDirection = getBearingBetweenTwoPoints(locationResult.getLastLocation(), destination);
             //   Log.i("FindActivity", "correct direction is  " + +  correctDirection);


                playSound(locationResult.getLastLocation().getBearing(), correctDirection, currentDistance);

                //  mediaPlayer.setVolume(5,50);
               // mediaPlayer.start();
            }
        };



        btn_terminate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(FindActivity.this,"Terminating Program " ,Toast.LENGTH_SHORT).show();

                finish();
                System.exit(0);

                // finish();
            }
        });

        btn_form.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(FindActivity.this, FormActivity.class);
                startActivity(i);

                // finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
    }

    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        mediaPlayer = MediaPlayer.create(this, R.raw.fountain);

        mediaPlayer.setVolume(leftVolume,rightVolume);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        startLocationupdates();
    }

    /*
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
       // DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    */


    private void stopLocationUpdates() {

    }

    private void startLocationupdates() {
        locationData.fusedLocationProviderClient.requestLocationUpdates(locationData.locationRequest, locationCallback, null);

    }

    private double degreesToRadians(double degrees)  {
        return degrees * Math.PI / 180;
    }

    private double radiansToDegrees(double radians) {
        return radians * 180 / (Math.PI);
    }

    private double getBearingBetweenTwoPoints(Location point1, Location point2)  {
        //   print("point1 ", point1.coordinate.latitude, " ", point1.coordinate.longitude)
        //  print("point2 ", point2.coordinate.latitude, " ", point2.coordinate.longitude)
        double lat1 = degreesToRadians(point1.getLatitude());
        double lon1 = degreesToRadians(point1.getLongitude());
        double lat2 = degreesToRadians(point2.getLatitude());
        double lon2 = degreesToRadians(point2.getLongitude());
        double dLon = lon2 - lon1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double radiansBearing = Math.atan2(y, x);
        if(radiansBearing < 0){
            return (radiansToDegrees(radiansBearing)) + 360;
            // return radiansToDegrees(radians: radiansBearing)
        }
        else{
            return radiansToDegrees(radiansBearing);
        }

        //south 0, west 90, north 180, east -90

    }  // end getBearing


    private void playSound(double userDirection, double correctDirection, float currentDistance)
    {

        //(float)(Math.log(MAX_VOLUME-(MAX_VOLUME * currentVolumePercent))/Math.log(MAX_VOLUME));
        if(previousDistance != 0.0)
        {
            if(currentDistance < previousDistance)
            {
                if(currentVolume + 0.4F <= MAX_VOLUME)  // don't go over 1
                    currentVolume+=0.04f;

            }
            else {
                if (currentVolume - 0.04F >= 1.0f)
                    currentVolume -= 0.04f;
            }
        }

        Log.i("FindActity", "correctDirection is  " + correctDirection);
        Log.i("FindActity", "userDirection is  " + userDirection);

        double firstDegree = ((correctDirection - userDirection)%360);
        Log.i("FindActity", "firstDegree is  " + firstDegree);


        double degree = (((firstDegree + 540)%360) - 180);
        Log.i("FindActity", "Degree is  " + degree);
        // (float) ((degree < 0) ? degree + 360 : degree),

        degreeTV.setText("Degrees: " + degree);



        if(Math.abs(previousDegree - degree) > 2) {
            // rotation animation - reverse turn degree degrees
            if(previousDegree < -500)
                previousDegree =    (float) ((degree < 0) ? degree + 360 -1 : degree +1);

            RotateAnimation ra = new RotateAnimation(
                    previousDegree,
                    (float) ((degree < 0) ? degree + 360 : degree),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            // set the compass animation after the end of the reservation status
            ra.setFillAfter(true);
            // set how long the animation for the compass image will take place
            ra.setDuration(2);
            // Start animation of compass image

            compassimage.startAnimation(ra);
            previousDegree =     (float) ((degree < 0) ? degree + 360 : degree);

        }

        if(degree > 90)
            degree = Math.abs(degree - 180); // does this make sense????
        else if(degree < -90)
            degree = -(degree + 180);
        Log.i("FindActity", " degree for volume is " + degree );
        Log.i("FindActity", " currentVolume is " + currentVolume );

        if( degree < 0) // left ear entirely
        {
            Log.i("FindActity", " right ear volume reduction is " + (currentVolume * (1 -(Math.abs(degree) / (MAX_VOLUME))))   );

        //    leftVolume = (float)(currentVolume / MAX_VOLUME);
        //    rightVolume = (float)(currentVolume * (1 -(Math.abs(degree) / MAX_VOLUME)))/MAX_VOLUME;


           leftVolume = (float) (1 - (Math.log(MAX_VOLUME - currentVolume) / Math.log(MAX_VOLUME)));
           rightVolume = (float) (1 - (Math.log(MAX_VOLUME - (currentVolume * (1 -(Math.abs(degree) / (MAX_VOLUME))))) / Math.log(MAX_VOLUME)));


         //   leftVolume=(float)(Math.log(MAX_VOLUME-85f)/Math.log(MAX_VOLUME));
         //   rightVolume=(float)(Math.log(MAX_VOLUME-15f)/Math.log(MAX_VOLUME));
        }
        else  // right ear entirely
        {
            Log.i("FindActity", " left ear volume reduction is " + (currentVolume * (1 -(Math.abs(degree) / (MAX_VOLUME))))   );
          //  leftVolume = (float)(currentVolume * (1 -(Math.abs(degree) / MAX_VOLUME)))/MAX_VOLUME;
          //  rightVolume = (float)(currentVolume / MAX_VOLUME);

            leftVolume = (float) (1 - (Math.log(MAX_VOLUME - (currentVolume * (1 -(Math.abs(degree) / (MAX_VOLUME))))) / Math.log(MAX_VOLUME)));
            rightVolume = (float) (1 - (Math.log(MAX_VOLUME - currentVolume) / Math.log(MAX_VOLUME)));

        }
        Log.i("FindActity", " left volume is " + leftVolume );
        Log.i("FindActity", " right volume is " + rightVolume );
        leftVolumeTV.setText("LeftV: " + leftVolume);
        rightVolumeTV.setText("RightV: " + rightVolume);
        currentVolumeTV.setText("currentV: " + currentVolume);

        mediaPlayer.setVolume(leftVolume,rightVolume);


        /*
        if(degree > 0)  // if destination on the right?
        {

            if(degree < 90)
            {
                leftVolume=(float)(Math.log(currentVolume-degree)/Math.log(currentVolume));
                rightVolume = (float)(Math.log(currentVolume+degree)/Math.log(currentVolume));
                //leftVolume = overallVolume  * .25f;
            }
            else
            {
                leftVolume=(float)(Math.log(currentVolume-(degree-90))/Math.log(currentVolume));
                rightVolume = (float)(Math.log(currentVolume+(degree-90))/Math.log(currentVolume));
            }
           // rightVolume = overallVolume;


            ///DegreeTV.setText( " degrees - right");
            Log.i("FindActity", " right" );
        }
        else // if destination on the left
        {

            if(degree > -90)
            {

                leftVolume=(float)(Math.log(currentVolume-degree)/Math.log(currentVolume));
                rightVolume = (float)(Math.log(currentVolume+degree)/Math.log(currentVolume));
              //  rightVolume = overallVolume  * .25f;
            }
            else
            {
                leftVolume=(float)(Math.log(currentVolume-(degree+90))/Math.log(currentVolume));
                rightVolume = (float)(Math.log(currentVolume+(degree+90))/Math.log(currentVolume));
//                rightVolume = 0.0f;
            }
           // leftVolume = overallVolume;
            Log.i("FindActity", " left" );

        }

        */

        previousDistance = currentDistance;

    }


}
