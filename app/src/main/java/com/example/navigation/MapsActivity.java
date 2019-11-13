package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.navigation.database.AppDatabase;
import com.example.navigation.entity.LocationD;
import com.example.navigation.utils.DatabaseInitializer;
import com.example.navigation.utils.DatabaseListner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, SensorEventListener, DatabaseListner {

    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private Location previousLocation= null;

    Float azimut;  // View to draw a compass

    private float prevorientation=0;
    private boolean hasOrientationChange=false;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;

    DatabaseInitializer database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        database = new DatabaseInitializer(AppDatabase.getAppDatabase(this));

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
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if(checkLocationPermission()){
            requestLocationUpdates();
            mMap.setMyLocationEnabled(true);
        }

    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Enable location")
                        .setMessage("please enable location service")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                        requestLocationUpdates();
                    }

                } else {

                    checkLocationPermission();

                }
                return;
            }

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d("TAG", "location update " + location);
//                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Current"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                        if (previousLocation!=null){
                            float distance = previousLocation.distanceTo(location);
                            if ((distance >= 1)|| hasOrientationChange==true){
                                addLocationToDb(location);
                            }
                        }else{
                            addLocationToDb(location);
                        }

                    }
                    previousLocation = location;

                }
            }, null);
        }
    }

    private void addLocationToDb(Location location){
        LocationD locationD = new LocationD();

        locationD.setAltitude(location.getAltitude());
        locationD.setLattitude(location.getLatitude());
        locationD.setLongitude(location.getLongitude());
        locationD.setBearing(location.getBearing());
        locationD.setSpeed(location.getSpeed());

        database.populateAsync(locationD);
        fetchAllData(database.getAll());
    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll

                if((azimut-prevorientation) > 0.5){
                    Log.d("orientation",String.valueOf(azimut));
                    Toast.makeText(this, "change", Toast.LENGTH_SHORT).show();
                    hasOrientationChange = true;
                }
                prevorientation = azimut;
            }
        }
    }

    @Override
    public void fetchAllData(List<LocationD> locations) {
        mMap.clear();
        Toast.makeText(this,String.valueOf(locations.size()),Toast.LENGTH_LONG);
        for (int i = 0;i<locations.size();i++){
            LocationD location = locations.get(i);
            LatLng latLng =  new LatLng(location.getLattitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in "+i));
        }
    }
}
