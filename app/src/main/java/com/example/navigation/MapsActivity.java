package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
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
import com.example.navigation.navigation.NavRoute;
import com.example.navigation.utils.DatabaseInitializer;
import com.example.navigation.utils.DatabaseListner;
import com.example.navigation.utils.LocationIndex;
import com.example.navigation.utils.RouteInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

      //  Toast.makeText(this, routeName, Toast.LENGTH_SHORT).show();

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
        mMap.setMaxZoomPreference(30);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if(checkLocationPermission()){
            mMap.setMyLocationEnabled(true);
            requestLocationUpdates();
            database.drawRoute(routeName);
        }

        if(!viewMode){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    addRouteFinishButton.setEnabled(true);
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Current"));
                    Route route = new Route();
                    route.setRouteName(routeName);
                    route.setDirection("drag");
                    addRouteToDb(location,route);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker arg0) {
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    Log.d("System out", "onMarkerDragEnd...");
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                }
            });
        }


    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

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
//                Route route4 = new Route();
//                route4.setRouteName(routeName);
//                route4.setDirection("finish");
//                addRouteToDb(currentLocation,route4);
                addRouteStartButton.setEnabled(false);
                addRouteLeftButton.setEnabled(false);
                addRouteRightButton.setEnabled(false);
                addRouteFinishButton.setEnabled(false);
                break;
        }
    }

    @Override
    public void fetchRouteInfo(ArrayList<RouteInfo> routeInfos) {

        ArrayList<Location> locations = new ArrayList<>();

        List<LatLng> points = new ArrayList<LatLng>();
        final MarkerOptions options = new MarkerOptions();

        for (int i=0;i<routeInfos.size();i++){
            LocationD locationD = routeInfos.get(i).getLocationD();

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(locationD.getLattitude());
            location.setLongitude(locationD.getLongitude());
            location.setAltitude(locationD.getAltitude());
            locations.add(location);

            LatLng latLng = new LatLng(locationD.getLattitude(),locationD.getLongitude());
            points.add(latLng);
        }
        if (viewMode){
            final NavRoute navRoute = new NavRoute(locations,1,this);

            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient clients = LocationServices.getFusedLocationProviderClient(this);
            clients.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        Log.d("TAG", "location update " + location);
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Current"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        int index = navRoute.getNearestPointIndex(location);

                        List<LocationIndex> locationIndexs = navRoute.getNearest3Point(location);
                                //navRoute.getNearestpointLocation(location);

                       // Toast.makeText(MapsActivity.this, String.valueOf(locationIndexs.size()), Toast.LENGTH_SHORT).show();

                        String res = "";
                        for(LocationIndex x : locationIndexs){
                            LatLng latLng = new LatLng(x.getLocation().getLatitude(), x.getLocation().getLongitude());
                            res += String.valueOf(x.getIndex()+ " " +"\n");
                            mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(x.getIndex())).snippet("Marker in index"));
                           // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        Toast.makeText(MapsActivity.this,res, Toast.LENGTH_LONG).show();

                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(navRoute.getPointLocation(index).getLatitude()
                                        ,navRoute.getPointLocation(index).getLongitude()))
                                .radius(3)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE)
                        );
                        Bitmap bitmap = getBitmap(R.drawable.ic_directions_run_black_24dp);
                        //Toast.makeText(MapsActivity.this, String.valueOf(navRoute.getAllpointLatLngs().size()), Toast.LENGTH_SHORT).show();
                        navRoute.setAnimation(mMap,navRoute.getAllpointLatLngs(),bitmap);
                    }

                }
            }, null);



        }

        int i=0;
//        for (LatLng point : points) {
//            options.position(point);
//            options.title(String.valueOf(i));
//            options.snippet("someDesc");
//            mMap.addMarker(options);
//            i++;
//        }
        drawLine(points);
    }


    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
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
