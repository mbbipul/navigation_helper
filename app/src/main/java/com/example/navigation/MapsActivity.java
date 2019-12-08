package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.example.navigation.database.AppDatabase;
import com.example.navigation.entity.LocationD;
import com.example.navigation.entity.Route;
import com.example.navigation.utils.DatabaseInitializer;
import com.example.navigation.utils.DatabaseListner;
import com.example.navigation.utils.RouteInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,DatabaseListner, View.OnClickListener {

    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    DatabaseInitializer database;

    Location currentLocation;

    Button addRouteStartButton,
            addRouteLeftButton,
            addRouteRightButton,
            addRouteFinishButton;

    String routeName;
    Boolean viewMode;

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addRouteStartButton  = findViewById(R.id.start);
        addRouteLeftButton   = findViewById(R.id.left);
        addRouteRightButton  = findViewById(R.id.right);
        addRouteFinishButton = findViewById(R.id.finish);

        linearLayout = findViewById(R.id.bottom_button);

        addRouteStartButton.setOnClickListener(this);
        addRouteLeftButton.setOnClickListener(this);
        addRouteRightButton.setOnClickListener(this);
        addRouteFinishButton.setOnClickListener(this);

        database = new DatabaseInitializer(AppDatabase.getAppDatabase(this),this);
        DebugDB.getAddressLog();


        routeName = intent.getStringExtra("routename");
        viewMode = intent.getBooleanExtra("viewmode",false);
        if(viewMode){
            addRouteStartButton.setVisibility(View.GONE);
            addRouteLeftButton.setVisibility(View.GONE);
            addRouteRightButton.setVisibility(View.GONE);
            addRouteFinishButton.setVisibility(View.GONE);
        }

        Toast.makeText(this, routeName, Toast.LENGTH_SHORT).show();

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
        mMap.setMaxZoomPreference(30);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if(checkLocationPermission()){
            mMap.setMyLocationEnabled(true);
            requestLocationUpdates();
            database.drawRoute(routeName);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
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
            addRouteStartButton.setEnabled(true);
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d("TAG", "location update " + location);
//                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Current"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        currentLocation = location;
                    }

                }
            }, null);
        }
    }

    private void addRouteToDb(Location location,Route route){
        database.populateAsync(location,route,route.getRouteName());
    }

    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId){
            case R.id.start:
                Route route = new Route();
                route.setRouteName(routeName);
                route.setDirection("move");
                addRouteToDb(currentLocation,route);
                addRouteLeftButton.setEnabled(true);
                addRouteRightButton.setEnabled(true);
                addRouteFinishButton.setEnabled(true);
                break;
            case R.id.left:
                Route route2 = new Route();
                route2.setRouteName(routeName);
                route2.setDirection("left");
                addRouteToDb(currentLocation,route2);
                break;
            case R.id.right:
                Route route3 = new Route();
                route3.setRouteName(routeName);
                route3.setDirection("right");
                addRouteToDb(currentLocation,route3);
                break;
            case R.id.finish:
                Route route4 = new Route();
                route4.setRouteName(routeName);
                route4.setDirection("finish");
                addRouteToDb(currentLocation,route4);
                addRouteStartButton.setEnabled(false);
                addRouteLeftButton.setEnabled(false);
                addRouteRightButton.setEnabled(false);
                addRouteFinishButton.setEnabled(false);
                break;
        }
    }

    @Override
    public void fetchRouteInfo(ArrayList<RouteInfo> routeInfos) {

        List<LatLng> points = new ArrayList<LatLng>();
        MarkerOptions options = new MarkerOptions();

        for (int i=0;i<routeInfos.size();i++){
            LocationD locationD = routeInfos.get(i).getLocationD();
            LatLng latLng = new LatLng(locationD.getLattitude(),locationD.getLongitude());
            points.add(latLng);
        }
        Toast.makeText(this, "draw line", Toast.LENGTH_SHORT).show();
        int i=0;
        for (LatLng point : points) {
            options.position(point);
            options.title(String.valueOf(i));
            options.snippet("someDesc");
            mMap.addMarker(options);
            i++;
        }
        drawLine(points);
    }

    public void drawLine(List<LatLng> points) {
        if (points == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }

        Polyline line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        line.setPoints(points);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.route_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }
}
