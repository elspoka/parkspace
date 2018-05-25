package tech.crossroads.parkspace;

import android.Manifest;
import android.app.Activity;
//import android.app.backup.BackupManager;
//import android.app.backup.RestoreObserver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
//import com.roughike.bottombar.BottomBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//import java.util.stream.Stream;

//import gr.test.activity.AboutUsActivity;
//import gr.test.activity.MainActivityDraw;
//import gr.test.activity.PrivacyPolicyActivity;
//import gr.test.fragment.HomeFragment;
//import gr.test.fragment.MoviesFragment;
//import gr.test.fragment.NotificationsFragment;
//import gr.test.fragment.PhotosFragment;
//import gr.test.fragment.SettingsFragment;
//import gr.test.other.CircleTransform;


public class SetReminderOnScanActivity extends AppCompatActivity implements SensorEventListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraMoveStartedListener, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnCameraIdleListener,   GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener {

    public GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    PendingResult<LocationSettingsResult> result;
    Status status;
    LocationSettingsStates state;
    int currentTime = 0;
    //BottomBar mBottomBar;
    //ImageView blurView;

    Location reminderLocation= new Location("reminder");

    public PolylineOptions polylineOptions= new PolylineOptions();

    private int ticketsNumber=0;

    //private LocationManager locationManager;
    private String provider;
    //private MyLocationListener mylistener;
    private Criteria criteria;

    //String timeFromServer = "0";
    String timeFromDevice = "0";
    boolean wasMoving = false;
    boolean mapMoved = false;
    boolean overrideDestination = false;

    Location lastLocation = new Location("providerNotSet");
    final static int REQUEST_LOCATION_INT = 1000;
    ////////////
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_TUTORIAL = "tutorial";
    private static final String TAG_PARKVERSETAG = "nfc tag";
    private static final String TAG_PRIVACYPOLICY = "privacy policy";
    private static final String TAG_TICKETS = "tickets remaining";
    private static final String TAG_REMINDER = "parking reminder";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    ////////////
    public HashMap<LatLng, Integer> sortList = new HashMap<LatLng, Integer>();

    //////////////
    private void loadNavHeader() {
        // name, website
        txtName.setText("name");
        txtWebsite.setText("website");

        // loading header background image
//        Glide.with(this).load(urlNavHeaderBg)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgNavHeaderBg);
//
//        // Loading profile image
//        Glide.with(this).load(urlProfileImg)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */




//    private Fragment getHomeFragment() {
//        switch (navItemIndex) {
//            case 0:
//                // home
//                HomeFragment homeFragment = new HomeFragment();
//                return homeFragment;
//            case 1:
//                // photos
//                PhotosFragment photosFragment = new PhotosFragment();
//                return photosFragment;
//            case 2:
//                // movies fragment
//                MoviesFragment moviesFragment = new MoviesFragment();
//                return moviesFragment;
//            case 3:
//                // notifications fragment
//                NotificationsFragment notificationsFragment = new NotificationsFragment();
//                return notificationsFragment;
//
//            case 4:
//                // settings fragment
//                SettingsFragment settingsFragment = new SettingsFragment();
//                return settingsFragment;
//            default:
//                return new HomeFragment();
//        }
//    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }


    @Override
    public void onCameraMoveStarted(int reason) {

//        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
        // Toast.makeText(this, "started",Toast.LENGTH_SHORT).show();
//        } else if (reason == GoogleMap.OnCameraMoveListener
//                .REASON_API_ANIMATION) {
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
//        } else if (reason == OnCameraMoveStartedListener
//                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
//        }
//        if (mMap != null) {
//            VisibleRegion vr = mMap.getProjection().getVisibleRegion();
//            LatLng farLeft = vr.farLeft;
//            LatLng farRight = vr.farRight;
//            double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude);
//            //Toast.makeText(MainActivity.this, "moved",Toast.LENGTH_SHORT).show();
//            double dist_camera = distanceFrom(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, cameracenter.latitude, cameracenter.longitude);
//            double ratio = dist_h / dist_camera;
//
//
//            // if((Math.abs(mMap.getCameraPosition().zoom-cameralevel)>0.4)||(ratio>(1/100)))
//            //{
//            wasMoving = true;
//            mapMoved = true;
//            //}
//        }
    }


//    @Override
//    public void onProviderEnabled(String string) {
//    }
//
//    @Override
//    public void onProviderDisabled(String string) {
//    }
//
//    @Override
//    public void onStatusChanged(String string, int integ, Bundle bundle) {
//    }


    public void AAR() {
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createApplicationRecord("gr.crossroads.parktracker")});
    }

    float cameralevel = 20;
    LatLng cameracenter;

    public void showPosition() {


    }

    float bearing = 0;
    Handler rot = new Handler();
    Runnable runnableMarker = new Runnable() {
        @Override
        public void run() {

            bearing = bearing + 45f;
            MarkerOptions mo = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position)).position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).rotation(bearing).anchor(0.5f, 0.5f);
            final Marker mark = mMap.addMarker(mo);

            Handler newhandler = new Handler();
            Runnable runwait = new Runnable() {
                @Override
                public void run() {
                    mark.remove();
                }
            };
            newhandler.postDelayed(runwait, 1900);

            rot.postDelayed(this, 90);

        }
    };

    //    public void readyLocation(){
//        rot.postDelayed(runnableMarker,2000);
//    }
    Circle circle0;
    MarkerOptions mo;
    Marker marker0;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);

        Boolean trafficBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkspace.trafficPreference", false);
        //= sharedPref.getString("gr.mapZoom","");
        //int trafficValue= Integer.valueOf(trafficString);
        //blurView.setVisibility(View.INVISIBLE);

//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        }
        //SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String zoomString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("gr.mapZoom", "19");
        //= sharedPref.getString("gr.mapZoom","");
        float zoomFloat = Float.valueOf(zoomString);


        //CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomFloat);
        //cameralevel = zoomFloat;
        //cameracenter = mMap.getCameraPosition().target;
        //mMap.animateCamera(zoom);
        if(lastLocation!=null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                    .zoom(zoomLevel)
                    //.bearing(azimuth)
                    .build()));
        }

        //boolean success = mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_nightmode)));



        Boolean styleBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkspace.style", false);
        //if(styleBoolean)
        {mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_reminder)));}
        //else{mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_blueish)));}



        //marker0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));

//        if(readReminderLatitude()!=0&&readReminderLongitude()!=0) {
//            mo = new MarkerOptions().position(new LatLng(readReminderLatitude(), readReminderLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));
//            marker0 = mMap.addMarker(mo);
//        }





        //MapsInitializer.initialize(getApplicationContext());

        // mMap.setOnMarkerDragListener(this);
//        if(RouteActivity.getRouteStartLong()!=0) {
//            LatLng start_pos = new LatLng(RouteActivity.getRouteStartLat(), RouteActivity.getRouteStartLong());
//            Start_Point = mMap.addMarker(new MarkerOptions()
//
//
//                    .position(start_pos)
//                    .title("Start")
//                    .snippet("When reached, timer starts")
//                    // .draggable(true)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)));
//        }

//        if(RouteActivity.getRouteFinishLong()!=0) {
//            LatLng finish_pos = new LatLng(RouteActivity.getRouteFinishLat(), RouteActivity.getRouteFinishLong());
//            Finish_Point = mMap.addMarker(new MarkerOptions()
//                    .position(finish_pos)
//                    .title("Finish")
//                    .snippet("When reached, timer stops")
//                    //.draggable(true)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)));
//        }


//        CameraPosition oldPos = mMap.getCameraPosition();
//        if(RouteActivity.getRouteStartLong()!=0) {
//            if(RouteActivity.getRouteFinishLong()!=0) {
//
//                CameraPosition pos = CameraPosition.builder(oldPos).bearing((bearing(RouteActivity.getRouteStartLat(), RouteActivity.getRouteStartLong(), RouteActivity.getRouteFinishLat(), RouteActivity.getRouteFinishLong())))
//                        .build();
//
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(RouteActivity.getRouteStartLat(), RouteActivity.getRouteStartLong()), 14));
//
//            }
//        }
        ////////////////////////////////////////////
        // new PingTask().execute("www.google.com","80");
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        try {
//            String url = getMapsApiDirectionsUrl();
//            ReadTask downloadTask = new ReadTask();
//            downloadTask.execute(url);
//        }
//        catch (Exception e){}
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection suspened", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection suspened", "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location0) {
        // mCurrentLocation = location;
        // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        lastLocation = location0;
        LatLng userPosition = new LatLng(location0.getLatitude(), location0.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(userPosition);
        float zoom = mMap.getCameraPosition().zoom;
        CameraUpdate zoomCU = CameraUpdateFactory.zoomTo(zoom);
        if (mMap != null) {

            Boolean centerMapBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkspace.centerMap", false);

            {

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(userPosition)
                        .zoom(zoomLevel)
                        .bearing(azimuth)
                        .build()));
                //mMap.moveCamera(center);
                //mMap.animateCamera(zoomCU);
            }

            //if(lastLocation!=null) {
            if(circle0!=null) {
                double radius = 1565430.3392 * Math.cos(location0.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
                circle0.setCenter(new LatLng(location0.getLatitude(), location0.getLongitude()));
                circle0.setRadius(radius);
            }
            else
            {
                double radius= 1565430.3392 * Math.cos(location0.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
                circle0 = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(location0.getLatitude(),location0.getLongitude()))
                        .radius(radius*1.25)
                        .fillColor(Color.DKGRAY)
                        .strokeColor(Color.WHITE)
                        .strokeWidth(10));
            }

            //}
            //updateUI();
            //Toast.makeText(MainActivity.this, "on location changed", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean gotit = false;

    private void updateUI() {

        String zoomString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("gr.mapZoom", "19");
        //= sharedPref.getString("gr.mapZoom","");
        float zoomFloat = Float.valueOf(zoomString);
        //double radius= 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
        //mMap.clear();
//        if(lastLocation!=null) {
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                    .target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
//                    .zoom(zoomFloat)
//                    //.bearing(azimuth)
//                    .build()));
//        }

        if(circle0!=null) {
            double radius = 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
            circle0.setCenter(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            circle0.setRadius(radius*1.25);
        }
        else
        {
            double radius= 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
            circle0 = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                    .radius(radius*1.25)
                    .fillColor(Color.DKGRAY)
                    .strokeColor(Color.GRAY)
                    .strokeWidth(10));
        }

        writeReminderLocation(lastLocation.getLatitude(),lastLocation.getLongitude());

        if(readReminderLatitude()!=0&&readReminderLongitude()!=0) {
            if(marker0==null) {
                mo = new MarkerOptions().position(new LatLng(readReminderLatitude(), readReminderLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));
                marker0 = mMap.addMarker(mo);
            }
            else{
                marker0.setPosition(new LatLng(readReminderLatitude(), readReminderLongitude()));
            }

            if(lastLocation!=null) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                        .zoom(zoomLevel)
                        //.bearing(azimuth)
                        .build()));
            }

        }

    }

    float zoomLevel= 19;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION_INT:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        new WaitForLocationAsync().execute();

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(SetReminderOnScanActivity.this, "Location not enabled", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connected", "Connected");

        try {
            // location2 = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // textc.setText(String.valueOf(location2.getLatitude()));
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(1 * 2000);
            locationRequest.setFastestInterval(1 * 2000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);


            //requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }

            result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    status = result.getStatus();  //final Status
                    state = result.getLocationSettingsStates();   //final LocationSettingsStates
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            new WaitForLocationAsync().execute();



                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        SetReminderOnScanActivity.this, REQUEST_LOCATION_INT);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });


        } catch (Exception e) {
        }


//        if(location2!=null) {
//            searchlat = location2.getLatitude();
//            searchlong = location2.getLongitude();
//            new LoadLocation().execute();
//        }
//        //new Thread(new GetContent()).start();
//        if(location2!=null) {
//
//            latitude = location2.getLatitude();//40.74;//
//            longitude = location2.getLongitude();//-73.98;//-73.98;
//
//
//        }
//        else
//        {
//            latitude = 0;//40.74;//
//            longitude =0;
//
//        }
//
//        searchlat = location2.getLatitude();//40.74;//
//        searchlong = location2.getLongitude();//-73.98;//-73.98;
//        //places[0]="bar";
//        //new LoadPlaces().execute();
//
//
//
//    //catch (Exception e){}
//    Geocoder geocoder2 = new Geocoder(this, Locale.getDefault());
//
//
//
//////
//    try {
//        List<Address> addresses = geocoder2.getFromLocation(latitude, longitude, 1);
//        //cityName = addresses.get(0).getAddressLine(1);
//        countryCode = addresses.get(0).getCountryCode().toLowerCase();
//        //countryCode=countryCode.toLowerCase();
//
////            if(addresses.get(0).getAdminArea()==null)
////            {adminName = addresses.get(0).getPremises().toLowerCase();}
////            else
////            {adminName = addresses.get(0).getAdminArea().toLowerCase();}
//        //adminName = addresses.get(0).getSubAdminArea();
//        //adminName=adminName.toLowerCase();
//
//        adminName = "thessaloniki";
//
//        cityName = addresses.get(0).getLocality().toLowerCase();
//
//        //cityName=cityName.toLowerCase();
//        //cityName = addresses.get(0).getAddressLine(0).split(" ");
//        //////////////////////////////////////////////////////////////
//        String[] parts = addresses.get(0).getAddressLine(0).split(" ");
//        streetName=parts[0];
//        if (!isNumeric(parts[1])){streetName=streetName+" "+parts[1];}
//        //////////////////////////
//        textc = (TextView) findViewById(R.id.v0);
//        if(streetName==null)
//        {textc.setText("No address");}
//        else {
//            // textc.setTypeface(typeFace);
//            textc.setText(streetName);
//        }
//        //////////////////////////////////////
//        streetName=streetName.toLowerCase();
//
//        // streetName=streetName.toLowerCase();
//        //currentLocation = getCurrentLocationViaJSON(latitude, longitude);
//
//        //currentLocation =
//
//        //String stateName = addresses.get(0).getAddressLine(1);
//        //String countryName = addresses.get(0).getAddressLine(2);
//
//    }
//    catch (IOException e) {
//        e.printStackTrace();
//    }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MainActivity.this);

////////////////////////////////////////////////////////////////////////////////////////////////////

        // if (mRequestingLocationUpdates) {
        //startLocationUpdates();
        //}

    }

//    protected void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
//        }
//    }

    public void saveNewPoint() {
        HashMap point = new HashMap();
        point.put("lat", String.valueOf(lastLocation.getLatitude()));
        point.put("long", String.valueOf(lastLocation.getLongitude()));


        // save object synchronously
        // Map savedPoint = Backendless.Persistence.of( "ParkPoints" ).save( point );

        // save object asynchronously
        Backendless.Persistence.of("ParkPoints").save(point, new AsyncCallback<Map>() {
            public void handleResponse(Map response) {
                // new Contact instance has been saved
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }

    @Override
    public void onResume() {

        h2.postDelayed(runnableMapUpdate, delayMapUpdate);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
        //loginUserAndGetProperties();
        //will be executed onResume
        //h.postDelayed(runnable,delay);
    }

    @Override
    protected void onStart() {

        googleApiClient.connect();
        super.onStart();


    }

    @Override
    protected void onStop() {

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        //h.removeCallbacks(runnable);

        //backupManager.dataChanged();

        super.onStop();
    }

    @Override
    protected void onPause() {

        h2.removeCallbacks(runnableMapUpdate);

        mSensorManager.unregisterListener(this);
        //mSensorManager.unregisterListener(this);

        if (googleApiClient != null) {
            if(googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        }
        super.onPause();

        if (mMap != null) {
            float mapZoom = mMap.getCameraPosition().zoom;
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("gr.mapZoom", String.valueOf(mapZoom)).commit();
        }
    }

    private int readTickets() {
        String str = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("parkspace.ticketsLeft", "0");
        int a = Integer.valueOf(str);
        return a;
    }

    private void writeTickets(int ticketsNum) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parkspace.ticketsLeft", String.valueOf(ticketsNum)).commit();
    }

    public void showBack() {
        //Toast.makeText(MainActivity.this, valuer, Toast.LENGTH_SHORT).show();
    }

    String valuer;

    Calendar calendar;

    //SupportMapFragment mMap = new SupportMapFragment();
    public void clearMap() {
        mMap.clear();
    }

    int PERMISSION_ACCESS_FINE_LOCATION = 3;
    private int ticketsAvailable;
    Handler h = new Handler();
    int delay = 10000;

    Handler h2 = new Handler();
    int delayMapUpdate = 1000;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // All good!
            } else {
                Toast.makeText(this, "Parkspace requires your location to work properly", Toast.LENGTH_SHORT).show();
            }


        }
    }

    boolean status1 = true;//free
    boolean status2 = true;//business
    boolean status3 = true;//popular

    ImageView imBar1;
    ImageView imBar2;
    ImageView imBar3;


    //BackupManager backupManager = new BackupManager(this);
    //MyBackupAgent myBackupAgent = new MyBackupAgent();


    public String readtext(String input) {
        String ret = "";

        try {
            InputStream inputStream = openFileInput(input);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = String.valueOf(stringBuilder);

                //Toast.makeText(MainActivity.this,ret,Toast.LENGTH_SHORT).show();

            }
        } catch (FileNotFoundException e) {
            //Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return ret;


    }
    public PolylineOptions po;





    float azimuth = 0;
    float previousAzimuth = 0;

    float[] inR = new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] orientVals = new float[3];


    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    float[] mGravity;
    float[] mGeomagnetic;

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

                azimuth = (float) Math.toDegrees(orientation[0]); // orientation contains: azimut, pitch and roll

            }
        }
    }

    Runnable runnableMapUpdate= new Runnable() {
        @Override
        public void run() {
            //boolean rotateBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkverse.mapRotate", false);



            if( (mMap!=null) ) {
                //CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                if(Math.abs(azimuth - previousAzimuth)>4) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(mMap.getCameraPosition().target)
                            .zoom(zoomLevel)
                            .bearing(azimuth)
                            .build()));
                }

                previousAzimuth = azimuth;
                //mMap.animateCamera(cu);
                if(circle0!=null) {
                    double radius = 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
                    circle0.setCenter(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    circle0.setRadius(radius * 1.25);
                }

            }



            h2.postDelayed(this, delayMapUpdate);
        }

    };


    // SensorManager mySensorManager;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    LinearLayout bottomSaveReminder;
    ImageView ivReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder_on_scan);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }

        //mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        bottomSaveReminder = (LinearLayout) findViewById(R.id.reminderLinearLayoutBottomButtonSave);


//        ivReminder = (ImageView) findViewById(R.id.imageViewReminder);
//        ivReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isOnline()) {
//                    //new SnapAsync().execute();
//                }
//
//                Toast.makeText(SetReminderOnScanActivity.this, "Parking reminder saved", Toast.LENGTH_SHORT).show();
//
//            }
//        });

//        ivReminder.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        ImageView view = (ImageView) v;
//                        //overlay is black with transparency of 0x77 (119)
//                        view.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);
//                        view.invalidate();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:
////                            if(lastLocation!=null) {
////                                if(mMap!=null){
////                                    offlineMapUpdateOnClick();
////                                }
////                            }
//                    case MotionEvent.ACTION_CANCEL: {
//                        ImageView view = (ImageView) v;
//                        //clear the overlay
//                        view.getDrawable().clearColorFilter();
//                        view.invalidate();
//                        break;
//                    }
//                }
//
//                return false;
//            }
//        });

        final SupportMapFragment mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reminderOnScanMap);
        // mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(SetReminderOnScanActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            //googleApiClient.connect();
        }

    }








    public void sort(HashMap<LatLng,Double> hashMap){
        Object[] a = sortList.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<LatLng, Double>) o2).getValue().compareTo(((Map.Entry<LatLng, Double>) o1).getValue());
            }
        });
        for (Object e : a) {
            System.out.println(((Map.Entry<LatLng, Double>) e).getKey() + " : "
                    + ((Map.Entry<LatLng, Double>) e).getValue());
        }
    }

    public LinkedHashMap sortHashMapByValuesD(HashMap<LatLng,Double> passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)){
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String)key, (Double)val);
                    break;
                }

            }

        }
        return sortedMap;
    }

    public void updateMap(MarkerOptions m) {

        long timenow = System.currentTimeMillis();
        //Toast.makeText(MainActivity.this,"a",Toast.LENGTH_LONG).show();
        MarkerOptions mlocal = m;
        //Toast.makeText(MainActivity.this,"b",Toast.LENGTH_LONG).show();
//        if(m.getSnippet().toString().trim().length() > 0) {
//            //Toast.makeText(MainActivity.this,"c",Toast.LENGTH_LONG).show();
//
//            long snip = Long.valueOf(m.getSnippet().toString());
//            //Toast.makeText(MainActivity.this,"d",Toast.LENGTH_LONG).show();
//
//
//        if (timenow - snip > 60000) {
//            mlocal.icon(BitmapDescriptorFactory.fromResource(R.drawable.two_shadow));
//            Toast.makeText(MainActivity.this,"e",Toast.LENGTH_LONG).show();
//        }



        //}

        if(mMap!=null){
            mMap.addMarker(mlocal);
            //Toast.makeText(MainActivity.this,"f",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCameraIdle()
    {
        if(mMap!=null){
            //offlineMapUpdateOnClick();

            double radius= 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
            circle0.setCenter(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
            circle0.setRadius(radius*1.25);

        }
        wasMoving = false;
        zoomLevel = mMap.getCameraPosition().zoom;
    }

    @Override
    public void onCameraMove() {

//        if(mMap!=null) {
//            if((Math.abs(mMap.getCameraPosition().zoom - cameralevel)>0.4)) {
//
//                VisibleRegion vr = mMap.getProjection().getVisibleRegion();
//                LatLng farLeft = vr.farLeft;
//                LatLng farRight = vr.farRight;
//                double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude);
//
//                double dist_camera = distanceFrom(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, cameracenter.latitude, cameracenter.longitude);
//                double ratio = dist_h / dist_camera;
//
//                //offlineMapUpdate();
//
//                cameralevel = mMap.getCameraPosition().zoom;
//
//                //Toast.makeText(MainActivity.this,"moved",Toast.LENGTH_SHORT).show();
//
//
//            }
//        }

    }

    private ArrayList<MarkerOptions> mMarkerOptArray = new ArrayList<MarkerOptions>();

    private ArrayList<MarkerOptions> offlineMarkerOptions = new ArrayList<>();
    private ArrayList<LatLng> offlinePointsLatLng = new ArrayList<>();
    private Location offlineLocation = new Location("offlineLocation");

    private ArrayList<MarkerOptions> offlineFreeParkingSpots = new ArrayList<>();
    private ArrayList<MarkerOptions> offlineFreePopularSpots = new ArrayList<>();
    private ArrayList<MarkerOptions> offlineParkingBusinessSpots = new ArrayList<>();

    private void updateOfflineLocation (){
        if (ActivityCompat.checkSelfPermission(SetReminderOnScanActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)!=null)
            {
                offlineLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
        }
    }


    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        // Return distance between 2 points, stored as 2 pair location;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = 6371000 * c;
        return new Double(dist * 1).floatValue();


    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";

        //String url = getMapsApiDirectionsUrl();
        if(isOnline())
        {
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        }

        marker.showInfoWindow();

        //εμφάνιση κουκίδας στην αρχή της γραμμής μέχρι την θέση παρκαρίσματος
        //LatLng latLngLastLocation= new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(latLngLastLocation).title("Start").icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)));
        overrideDestination = true;

        return true;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result!=null)
            {
                new ParserTask().execute(result);}
        }
    }



    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            if(isJSONValid(jsonData[0])) {

                try {
                    jObject = new JSONObject(jsonData[0]);
                    PathJSONParser parser = new PathJSONParser();
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return routes;

        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            offlinePointsLatLng.clear();
            // traversing through routes
            if(routes.size()>0) {

                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));


                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                        offlinePointsLatLng.add(position);
                    }


                    polyLineOptions.width(20);
                    polyLineOptions.color(Color.DKGRAY);
                    polyLineOptions.addAll(points);

                }

                {
//                    if (mMap != null) {
//                        clearMap();
//                    }

//                    if(mMap!=null)
//                    {
//                        offlineMapUpdate();
//                    }

//                    if (mMap != null) {
//
//                        mMap.clear();
//                        List<HashMap<String, String>> path = routes.get(0);
//                        HashMap<String, String> point = path.get(0);
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng positionStart = new LatLng(lat, lng);
//                        //LatLng positionStart0 = new LatLng(lat, lng+0.0000001);
////                        LatLng positionStart1 = new LatLng(lat+0.000001, lng+0.000001);
////                        LatLng positionStart2 = new LatLng(lat+0.000001, lng-0.000001);
////                        LatLng positionStart3 = new LatLng(lat-0.000001, lng-0.000001);
////                        LatLng positionStart4 = new LatLng(lat-0.000001, lng+0.000001);
////                        LatLng positionStart5 = new LatLng(lat+0.000001, lng+0.000001);
//
//                        double radius= 1565430.3392 * Math.cos(lat * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
//                        //Toast.makeText(MainActivity.this,String.valueOf(f),Toast.LENGTH_SHORT).show();
//                        //LatLng positionFinish = new LatLng(lat, lng+0.000001);
//                        Circle circle = mMap.addCircle(new CircleOptions()
//                                .center(positionStart)
//                                .radius(radius)
//                                .fillColor(Color.DKGRAY)
//                                .strokeColor(Color.GRAY)
//                                .strokeWidth(10));
////                        mMap.addPolygon(new PolygonOptions()
////                                .add(positionStart1,positionStart2,positionStart3,positionStart4,positionStart5)
////                                .strokeColor(Color.DKGRAY)
////                                .fillColor(Color.GRAY))
////                                .setStrokeWidth(100);
//                       // mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.positiondot)).position(positionStart).flat(true)).setAnchor(0.5f,0.5f);
//                        mMap.addPolyline(polyLineOptions);
//// ArrayList<LatLng> pointstart = null;
////                        PolylineOptions polyLineOption = null;
////                        pointstart = new ArrayList<LatLng>();
////                        polyLineOption = new PolylineOptions();
////                        pointstart.add(positionStart);
////                        pointstart.add(positionFinish);
////                        polyLineOption.width(100);
////                        polyLineOption.color(Color.DKGRAY);
////                        polyLineOption.addAll(pointstart);
//
//                        //mMap.addPolyline(polyLineOption);
//
//
//
//                        if(mMarkerOptArray!=null) {
//                            //Toast.makeText(MainActivity.this,"size:"+String.valueOf(mMarkerOptArray.size()),Toast.LENGTH_LONG).show();
//
//                            for (MarkerOptions markeropt : mMarkerOptArray) {
//
//                                updateMap(markeropt);
//                                // markeropt.setVisible(false);
//                                //Marker marker =  mMap.addMarker(markeropt);
//                                //marker.remove(); //<-- works too!
//                            }
//                        }
//                    }



                    //{

                    //mapMoved = false;
                    //}
                }

            }

        }
    }


//    public static boolean pingURL(String hostname, int port) throws UnknownHostException, IOException {
//        boolean reachable = false;
//
//        try (Socket socket = new Socket(hostname, port)) {
//            reachable = true;
//        }
//
//        return reachable;
//    }

//    private class MyLocationListener implements LocationListener {
//
//
//        TextView chronotext;// = (TextView) findViewById(R.id.chronometertext);
//
//
//        @Override
//        public void onLocationChanged(Location location) {
//            if(mMap!=null) {
//            //Toast.makeText(MainActivity.this, String.valueOf(mMap.getCameraPosition().zoom), Toast.LENGTH_SHORT).show();
//            lastLocation=location;
//            //updateUI();
//            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//            //CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
//
//
//
//                mMap.moveCamera(center);
//                //mMap.animateCamera(zoom);
//            }
//
//            // Initialize the location fields
//            //latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
//            //longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
//
//
//            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(RouteActivity.getRouteStartLat(), RouteActivity.getRouteStartLong()), 16));
//
//            //  if (distance2<40) {
//
//            //    int gettmr=timerstop;
//
//            //timerstop=(minutes*60*1000)+ (seconds*1000) +millis;
//
//            //final ParseQuery query2 = new ParseQuery("TestRoute");
//
//            //obj.put("gr_nametime",RouteActivity.getRouteName()+","+timerstop);
//            // obj.saveInBackground();
//
//            //RouteActivity.getSelectedObjectId()
//
//                    /*
//                    Parse update
//
//                    if(timerstop<RouteActivity.getRouteTime()) {
//
//                        final ParseQuery<ParseObject> querys = ParseQuery.getQuery("TestRoute");
//
//                        //if (timerstop<RouteActivity.getRouteTime()) {
//                        //"xGQqRimgAo"
//                        querys.orderByAscending("gr_nametime");
//                        querys.getInBackground(objid, new GetCallback<ParseObject>() {
//                            public void done(ParseObject gameScore, ParseException e) {
//                                if (e == null) {
//                                    // Now let's update it with some new data. In this case, only cheatMode and score
//                                    // will get sent to the Parse Cloud. playerName hasn't changed.
//                                    gameScore.put("gr_records", timerstop);
//                                    //gameScore.put("cheatMode", true);
//                                    gameScore.saveInBackground();
//                                }
//                            }
//                        });
//                        /////////////Its A NEW RECORD!!!//////
//                        //timeindialog.setText(String.valueOf(gettmr));
//                        showDialog();
//
//                    }
//                    else{
//                        //timeindialog.setText(String.valueOf(gettmr));
//                        showDialog();
//                    }
//                    */
//
//
//            //update record
//            //NumberFormat counts000 = new DecimalFormat("000");
//            //NumberFormat counts00 = new DecimalFormat("00");
//
//            //int count1=timerstop/60000;
//            //int count2=(timerstop-count1*60000)/1000;
//            //int count3=(timerstop-(count1 * 60000 + count2 * 1000));
//            //textRecord.setText(RouteActivity.getRouteName()+"     Route Record: "+count1+":"+counts00.format(count2)+":"+counts000.format(count3));
//            ////////////////////////////////////////////////////////////////////////////////
//
//            // }
//
//            // Toast.makeText(MainMap.this,  "Location changed",Toast.LENGTH_SHORT).show();
//
//        }
//
//        //button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
////        @Override
////        public void onMapReadyCallback(GoogleMap map) {
////
////            //toast("Map ready");
////
////        }
//
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            //Toast.makeText(MainMap.this, provider + "'s status changed to "+status +"!",
//            // Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            //Toast.makeText(MainMap.this, "Provider " + provider + " enabled!",
//            //   Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            //  Toast.makeText(MainMap.this, "Provider " + provider + " disabled!",
//            //   Toast.LENGTH_SHORT).show();
//        }
//    }

    public boolean isOnline() {
        //gia ckeck an yparxei internet//
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }

    public double readReminderLatitude (){
        String latString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("parkspace.reminder.latitude", "0");
        return Double.valueOf(latString);
    }

    public double readReminderLongitude (){
        String longString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("parkspace.reminder.longitude", "0");
        return Double.valueOf(longString);
    }

    public void writeReminderLocation (double lat, double lng){
        //float mapZoom = mMap.getCameraPosition().zoom;
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parkspace.reminder.latitude", String.valueOf(lat)).commit();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parkspace.reminder.longitude", String.valueOf(lng)).commit();
    }

    class SnapAsync extends AsyncTask<String, String, JSONObject> {

        //public String streetNameAsync="";
        //public String shop="";
        //public LoadLocationAsync asyncLoc;

        // public String getStreetName(){return streetNameAsync;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // asyncLoc = this;
//            new CountDownTimer(10000, 10000) {
//                public void onTick(long millisUntilFinished) {
//                    // You can monitor the progress here as well by changing the onTick() time
//                }
//                public void onFinish() {
//                    // stop async task if not in progress
//                    if (asyncLoc.getStatus() == AsyncTask.Status.RUNNING) {
//                        asyncLoc.cancel(true);
//                        // Add any specific task you wish to do as your extended class variable works here as well.
//                    }
//                }
//            }.start();

        }

        /**
         * getting Places JSON
         */
        protected JSONObject doInBackground(String... args) {
            //shop=args[2];

            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            //JSONObject json = jParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +String.valueOf(searchlat)+"," +String.valueOf(searchlong) +"&language=en&radius=500&type=bar&key=AIzaSyA3XkgTBsNJEt3mHaU4-M1-Bvf6ILwgZhU");
            //JSONObject json = jParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng="+args[0]+"," +args[1] +"&language=en&key=AIzaSyDXbM60rO_aAfK1edCJAmNdGQgDwypIe3I");
            JSONObject json = jParser.getJSONFromUrl("https://roads.googleapis.com/v1/snapToRoads?path=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&key=AIzaSyDxJRcGtZAQXJgDUD5TiGZts4Y1SwWyY5M");
            //lat //long
            return json;
            ////////////////////////////////////////////////////////////

        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         **/
        protected void onPostExecute(JSONObject json) {

            try {

                if (json != null) {
                    JSONArray objec = json.getJSONArray("snappedPoints");
                    //Toast.makeText(MainActivity.this,String.valueOf(objec),Toast.LENGTH_SHORT).show();
                    JSONObject ob = objec.getJSONObject(0);

                    JSONObject objectc = ob.getJSONObject("location");
//                for(int n=0;n<objectc.length();n++)
//                {if objectc.getJSONObject(n)}

                    //String str=objectcity.getJSONObject("latitude").toString();
                    double snapLat = objectc.getDouble("latitude");
                    double snapLong = objectc.getDouble("longitude");
///////////////////////////////////////////////////////////////////////////////////////////
                    //String timeEpoch = timeFromServer;
                    timeFromDevice = String.valueOf(System.currentTimeMillis());
                    String timeEpoch = "0";
                    timeEpoch = timeFromDevice;//allagi

                    reminderLocation.setLatitude(snapLat);
                    reminderLocation.setLongitude(snapLong);

                    if(readReminderLatitude()==0&&readReminderLongitude()==0)
                    {
                        mo = new MarkerOptions().position(new LatLng(snapLat, snapLong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));
                        marker0 = mMap.addMarker(mo);
                    }
                    else
                    {
                        marker0.setPosition(new LatLng(snapLat,snapLong));
                    }

                    writeReminderLocation(snapLat,snapLong);



                    //edo kano save sto backendless
//                    Backendless.Geo.savePoint(snapLat, snapLong, category, meta, new AsyncCallback<GeoPoint>() {
//                        @Override
//                        public void handleResponse(GeoPoint geoPoint) {
//                            // System.out.println( geoPoint.getObjectId() );
//
//                        }
//
//                        @Override
//                        public void handleFault(BackendlessFault backendlessFault) {
//
//                        }
//                    });

///////////////////////////////////////////////////////////////////////////////////
                    //JSONObject snapLong=objectcity.getJSONObject("longitude");
                    //Toast.makeText(MainActivity.this,String.valueOf(snapLat),Toast.LENGTH_LONG).show();

/////////////////////////////////////////////////////////////////////////////////////////////////////
//                for(int n=0;n<objectcity.length();n++){
//                    JSONObject obcity=objectcity.getJSONObject(n);
//                    JSONObject obstreet=objectcity.getJSONObject(n);
//
//
////                    if(obcity.toString().contains("administrative_area_level_3")){
////                        String city= obcity.getString("long_name").toString();
////                    }
//
//
//
//
//
////                    if(obstreet.toString().contains("route")){
////                        {
////
////                            String streetOb= obstreet.getString("short_name").toString();
////                            streetNameAsync=streetOb;
////                            if(streetNameAsync.contains("\""))
////                            {streetNameAsync.replace("\"","");}
////                        }
////                    }
//
//                }
//////////////////////////////////////////////////////////////////////////////////
                    // JSONObject obcity=objectcity.getJSONObject(5);
                    //JSONObject obstreet=objectcity.getJSONObject(1);
//                if(ob!=null) {
//                    String[] addres = ob.getString("formatted_address").split(",");
//                    // String city= obcity.getString("long_name").toString();
////                if (streetName != null) {
////                    textc.setText(streetName);
////                }
////                if (cityName != null) {
////                    txcirccity.setText(cityName);
////                }
//                }


                }
            } catch (JSONException ee) {
                //ee.printStackTrace();
            }
//        try {
//            //new PingTask().execute("www.google.com", "80");//and send to parse
//            /////////////////////////////////////////
//        }
//
//        catch (Exception e) {
//            e.printStackTrace();
//        }


        }

    }


    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }

    private void writeToSDFile(String text){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //tv.append("\nExternal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/external_storage_dir/vs");
        dir.mkdirs();
        File file = new File(dir, "vs0");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(text);
            //pw.println("Hello");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Log.i(TAG, "******* File not found. Did you" +
            //     " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tv.append("\n\nFile written to "+file);
    }

    private String readExt(File file ){
        String data = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                data = data + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public class WaitForLocationAsync extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... url) {

            if (ActivityCompat.checkSelfPermission(SetReminderOnScanActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                while (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) == null) {
                    //wait
                }
            }
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        @Override
        protected void onPostExecute(Location result) {
            super.onPostExecute(result);
            //Toast.makeText(MainActivity.this, "got it!", Toast.LENGTH_SHORT).show();

            lastLocation=result;

            updateUI();

        }
    }




}
