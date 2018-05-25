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


public class ReminderActivity extends AppCompatActivity implements SensorEventListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraMoveStartedListener, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnCameraIdleListener,   GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener {

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
        //NavUtils.navigateUpFromSameTask(this);
        Intent intent= new Intent(ReminderActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.backright,
                R.anim.backleft);
        //finish();
    }





    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
    ////////////////

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
        if (mMap != null) {
            VisibleRegion vr = mMap.getProjection().getVisibleRegion();
            LatLng farLeft = vr.farLeft;
            LatLng farRight = vr.farRight;
            double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude);
            //Toast.makeText(MainActivity.this, "moved",Toast.LENGTH_SHORT).show();
            double dist_camera = distanceFrom(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, cameracenter.latitude, cameracenter.longitude);
            double ratio = dist_h / dist_camera;


            // if((Math.abs(mMap.getCameraPosition().zoom-cameralevel)>0.4)||(ratio>(1/100)))
            //{
            wasMoving = true;
            mapMoved = true;
            //}
        }
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
        String zoomString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("gr.mapZoom", "10");
        //= sharedPref.getString("gr.mapZoom","");
        float zoomInt = Float.valueOf(zoomString);


        CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomInt);
        cameralevel = zoomInt;
        cameracenter = mMap.getCameraPosition().target;
        mMap.animateCamera(zoom);

        //boolean success = mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_nightmode)));



        Boolean styleBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkspace.style", false);
        //if(styleBoolean)
        {mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_reminder)));}
        //else{mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_blueish)));}

        double radius= 1565430.3392 * Math.cos(offlineLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);

        //mMap.clear();

        circle0 = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                .radius(radius*1.25)
                .fillColor(Color.DKGRAY)
                .strokeColor(Color.GRAY)
                .strokeWidth(10));

        //marker0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));

        if(readReminderLatitude()!=0&&readReminderLongitude()!=0) {
            mo = new MarkerOptions().position(new LatLng(readReminderLatitude(), readReminderLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));
            marker0 = mMap.addMarker(mo);
        }





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
                        .zoom(zoom)
                        .bearing(azimuth)
                        .build()));
                //mMap.moveCamera(center);
                //mMap.animateCamera(zoomCU);
            }

            double radius= 1565430.3392 * Math.cos(offlineLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
            circle0.setCenter(new LatLng(location0.getLatitude(),location0.getLongitude()));
            circle0.setRadius(radius);
            //updateUI();
            //Toast.makeText(MainActivity.this, "on location changed", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean gotit = false;

    private void updateUI() {

        String zoomString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("gr.mapZoom", "14");
        //= sharedPref.getString("gr.mapZoom","");
        float zoomInt = Float.valueOf(zoomString);

        // mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        // mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        // mLastUpdateTimeTextView.setText(mLastUpdateTime);
        if (ActivityCompat.checkSelfPermission(ReminderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


                if (lastLocation.getLatitude() != 40.640 && lastLocation.getLongitude() != 22.944) {

                    // if(!gotit) {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomInt);
                    if (mMap != null) {
                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);
                    }
                    // }

                } else if (lastLocation.getLatitude() == 40.640 && lastLocation.getLongitude() == 22.944) {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(40.640, 22.944));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomInt);
                    if (mMap != null) {
                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);
                    }
                }

            }

        }
    }


    public void loginUserAndGetProperties() {
        Backendless.UserService.login("a@b.g", "1234", new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser loggedUser) {
                //Toast.makeText( MainActivity.this, "User has been logged in: " + loggedUser, Toast.LENGTH_SHORT ).show();

                BackendlessUser user = Backendless.UserService.CurrentUser();
                if (user != null) {
                    // get user's email (i.e. mandatory/predefined property)
                    //String email = user.getEmail();
                    //Toast.makeText(MainActivity.this, email , Toast.LENGTH_SHORT).show();

                    //saveNewPoint();

                    /////////////////////////////////////////////////////////////////////////////////
//                    List<String> category = new ArrayList<String>();
//                    category.add( "parklist" );
//                    //categories.add( "cool_places" );
//
//                    Map<String, Object> meta = new HashMap<String, Object>();
//                    meta.put( "created", "" );
//
//                    Backendless.Geo.savePoint( lastLocation.getLatitude(), lastLocation.getLongitude(), category, meta, new AsyncCallback<GeoPoint>()
//                    {
//                        @Override
//                        public void handleResponse( GeoPoint geoPoint )
//                        {
//                           // System.out.println( geoPoint.getObjectId() );
//                        }
//
//                        @Override
//                        public void handleFault( BackendlessFault backendlessFault )
//                        {
//
//                        }
//                    });
                    ////////////////////////////////////////////////////////////////////////////////

                    BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
                    geoQuery.addCategory("parklist");
                    geoQuery.addCategory("Businesses");
                    geoQuery.setIncludeMeta(true);
                    //geoQuery.setLatitude(lastLocation.getLatitude());
                    //geoQuery.setLongitude(lastLocation.getLongitude());
                    //geoQuery.setRadius(1000000d);
                    //geoQuery.setUnits(Units.KILOMETERS);
                    //BackendlessCollection<GeoPoint> geoPoints = Backendless.Geo.getPoints( geoQuery );
                    Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<GeoPoint> points) {
                            //Toast.makeText(MainActivity.this, String.valueOf(points.getTotalObjects()), Toast.LENGTH_SHORT).show();
                            //points.getCurrentPage().get(0).toString();

                            Iterator<GeoPoint> iterator = points.getCurrentPage().iterator();

                            while (iterator.hasNext()) {
                                GeoPoint geo = iterator.next();

                                //geo.getLatitude();
                                //geo.getLongitude();
                                String metadata;
                                String tag = "";
                                metadata = String.valueOf(geo.getMetadata("category"));
                                tag = String.valueOf(geo.getMetadata("tag"));

                                // if (metadata != null) {
                                //Toast.makeText(MainActivity.this, metadata,Toast.LENGTH_SHORT).show();}

                                // if (metadata.contains("busin")) {
                                // mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re))).setTitle(tag);
                                // } else {
                                if (metadata.contains("busin")) {
                                    mMarkerOptArray.add(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.popular_new_re)));
                                } else {
                                    mMarkerOptArray.add(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re)));
                                }
                                //mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re)));
                                // }
                                // }
                                // Toast.makeText(MainActivity.this,String.valueOf(geo.getLongitude()),Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            // System.err.println( String.format( "searchByDateInRadius FAULT = %s", fault ) );
                        }
                    });


                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(intent);

                    //setContentView(R.layout.activity_main);


                } else {
                    //Toast.makeText(MainActivity.this, "User hasn't been logged", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                new AlertDialog.Builder(ReminderActivity.this).setMessage("Server reported an error: " + fault).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.ok, null).show();
            }
        });
    }


    public void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            Toast.makeText(ReminderActivity.this, String.valueOf(pair.getValue()), Toast.LENGTH_SHORT).show();
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION_INT:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        //Toast.makeText(SendLocation.this, "Location enabled by user!", Toast.LENGTH_LONG).show();

//                        if (ActivityCompat.checkSelfPermission(SendLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                            if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
//                                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                                Toast.makeText(SendLocation.this, "got location", Toast.LENGTH_SHORT).show();
//
//                                if (isOnline()) {
//
//
//                                    loginUserAndSavePoint();
//                                    //Map map =Backendless.Persistence.of( "ServerTime" ).findById( "5CFA9FB2-DA4A-C033-FFF2-16FFED54C700" );
//
//                                }
//                                ////////////////
//                                //readyLocation();
//                                //MarkerOptions mo=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position)).position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).rotation(bearing).anchor(0.5f,0.5f);
//                                //Marker mark= mMap.addMarker(mo);
//                            }
//                        }

                        new WaitForLocationAsync().execute();

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(ReminderActivity.this, "Location not enabled", Toast.LENGTH_LONG).show();
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
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            //////////////////
                            //Toast.makeText(MainActivity.this, "Success, getting position", Toast.LENGTH_SHORT).show();

                            //BackendlessUser user0 = new BackendlessUser();
                            // user0.setEmail   ( "a@b.g" );
                            //emailString=email.getText().toString();
                            // user0.setPassword(  "1234" );
                            //passString=password.getText().toString();

//                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                                if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
//                                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                                }
//                                ////////////////
//                                //readyLocation();
//                                //MarkerOptions mo=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position)).position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).rotation(bearing).anchor(0.5f,0.5f);
//                                //Marker mark= mMap.addMarker(mo);
//                            }

                            new WaitForLocationAsync().execute();

//                            if (isOnline()) {
//
//
//                                loginUserAndGetProperties();
//                                //Map map =Backendless.Persistence.of( "ServerTime" ).findById( "5CFA9FB2-DA4A-C033-FFF2-16FFED54C700" );
////                                Backendless.Persistence.of("ServerTime").findById("5CFA9FB2-DA4A-C033-FFF2-16FFED54C700",
////                                        new AsyncCallback<Map>() {
////                                            @Override
////                                            public void handleResponse(Map response) {
////                                                // an object from the "Contact" table has been found by it's objectId
////
////                                                timeFromServer = String.valueOf(response.get("name"));
////
////                                            }
////
////                                            @Override
////                                            public void handleFault(BackendlessFault fault) {
////                                                // an error has occurred, the error code can be retrieved with fault.getCode()
////                                            }
////                                        });
//
//
//                            }

                            //saveNewPoint();

                            //updateUI();

//                            if (isOnline()) {
//                                if (lastLocation.getLongitude() != 10 && lastLocation.getLatitude() != 10) {
//                                    new SnapAsync().execute();
//                                }
//                            }
                            //locationProvider.configureIfNeeded(MainMap.this);
                            //fetch=true;
                            //updateTextCenter();

//                            if (internetConnection==true) {
//                                new FetchCordinates().execute();
//                                //new PingTaskAndSetICOF().execute("www.google.com","80");
//                            }
                            /////////////////////////////////////////////////////////////////////
//
//                                try {
//                                     List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                                    //cityName = addresses.get(0).getAddressLine(1);
//                                    countryCode = addresses.get(0).getCountryCode().toLowerCase();
//                                    //countryCode=countryCode.toLowerCase();
//
//                        //            if(addresses.get(0).getAdminArea()==null)
//                        //            {adminName = addresses.get(0).getPremises().toLowerCase();}
//                        //            else
//                        //            {adminName = addresses.get(0).getAdminArea().toLowerCase();}
//                                    //adminName = addresses.get(0).getSubAdminArea();
//                                    //adminName=adminName.toLowerCase();
//
//                                    adminName = "thessaloniki";
//
//                                    cityName = addresses.get(0).getLocality().toLowerCase();
//
//                                    //cityName=cityName.toLowerCase();
//                                    //cityName = addresses.get(0).getAddressLine(0).split(" ");
//                                    //////////////////////////////////////////////////////////////
//                                    String[] parts = addresses.get(0).getAddressLine(0).split(" ");
//                                    streetName=parts[0];
//                                    if (!isNumeric(parts[1])){streetName=streetName+" "+parts[1];}
//                                    //////////////////////////
//                                    textc = (TextView) findViewById(R.id.v0);
//                                    if(streetName==null)
//                                    {textc.setText("No address");}
//                                    else {
//                                       // textc.setTypeface(typeFace);
//                                        textc.setText(streetName);
//                                    }
//                                    //////////////////////////////////////
//                                    streetName=streetName.toLowerCase();
//
//                                    // streetName=streetName.toLowerCase();
//                                    //currentLocation = getCurrentLocationViaJSON(latitude, longitude);
//
//                                    //currentLocation =
//
//                                    //String stateName = addresses.get(0).getAddressLine(1);
//                                    //String countryName = addresses.get(0).getAddressLine(2);
//
                            ///////////////////////////////////////////////////////////////////////////


                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        ReminderActivity.this, REQUEST_LOCATION_INT);
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
                            .zoom(mMap.getCameraPosition().zoom)
                            .bearing(azimuth)
                            .build()));
                }

                previousAzimuth = azimuth;
                //mMap.animateCamera(cu);

                double radius= 1565430.3392 * Math.cos(lastLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
                circle0.setCenter(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                circle0.setRadius(radius*1.25);

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
        setContentView(R.layout.activity_reminder);

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


        ivReminder = (ImageView) findViewById(R.id.imageViewReminder);
        ivReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(isOnline()) {
                  //  new SnapAsync().execute();
                //}
                if(lastLocation!=null) {
                    double lat = lastLocation.getLatitude();
                    double lng = lastLocation.getLongitude();
                    if (readReminderLatitude() == 0 && readReminderLongitude() == 0) {
                        mo = new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.remindercaricon));
                        marker0 = mMap.addMarker(mo);
                    } else {
                        marker0.setPosition(new LatLng(lat, lng));
                    }

                    writeReminderLocation(lat, lng);
                }
                Toast.makeText(ReminderActivity.this, "Parking reminder saved", Toast.LENGTH_SHORT).show();

            }
        });

        ivReminder.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
//                            if(lastLocation!=null) {
//                                if(mMap!=null){
//                                    offlineMapUpdateOnClick();
//                                }
//                            }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });




        //Thessaloniki
        //lastLocation.setLatitude(40.640);
        //lastLocation.setLongitude(22.944);

//        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
//        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
//        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
////                if (tabId == R.id.tab_favorites) {
////                    // The tab with id R.id.tab_favorites was selected,
////                    // change your content accordingly.
////                }
//            }
//        });

        //blurView = (ImageView) findViewById(R.id.blur_view);

        //LinearLayout viewMenu = (LinearLayout) findViewById(R.id.linearImageMenu);

//        imBar1 = (ImageView) findViewById(R.id.bottomBarImage1);
//        imBar2 = (ImageView) findViewById(R.id.bottomBarImage2);
//        imBar3 = (ImageView) findViewById(R.id.bottomBarImage3);
//
//        imBar1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(status1)
//                {
//
//                    imBar1.setImageResource(R.drawable.one_shadow_re_unselected);
//                    status1=false;
//                }
//                else
//                {
//
//                    imBar1.setImageResource(R.drawable.one_shadow_re);
//                    status1=true;
//                }
//
//                //if(isOnline()) {
//                if(lastLocation!=null) {
//                    if(mMap!=null){
//                        offlineMapUpdateOnClick();
//
//                    }
//                }
//                // }
//
//            }
//        });

//        imBar1.setOnTouchListener(new View.OnTouchListener() {
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

//        imBar2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(status2)
//                {
//
//                    imBar2.setImageResource(R.drawable.business_unselected);
//                    status2=false;
//                }
//                else
//                {
//
//                    imBar2.setImageResource(R.drawable.popular_new_re);
//                    status2=true;
//                }
//
//                if(lastLocation!=null) {
//                    if(mMap!=null){
//                        offlineMapUpdateOnClick();
//
//                    }
//                }
//            }
//        });

//        imBar2.setOnTouchListener(new View.OnTouchListener() {
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
////                        if(lastLocation!=null) {
////                            if(mMap!=null){
////                                offlineMapUpdateOnClick();
////                            }
////                        }
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
//
//        imBar3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(status3)
//                {
//
//                    imBar3.setImageResource(R.drawable.free_spaces_unselected);
//                    status3=false;
//                }
//                else
//                {
//
//                    imBar3.setImageResource(R.drawable.free_new_re);
//                    status3=true;
//                }
//
//                if(lastLocation!=null) {
//                    if(mMap!=null){
//                        offlineMapUpdateOnClick();
//
//                    }
//                }
//            }
//        });
//
//        imBar3.setOnTouchListener(new View.OnTouchListener() {
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
////                        if(lastLocation!=null) {
////                            if(mMap!=null){
////                                offlineMapUpdateOnClick();
////                            }
////                        }
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




//        Blurry.with(MainActivity.this)
//                .radius(1)
//                .sampling(8)
//                .color(Color.argb(66, 45, 67, 50))
//                .async()
//                .capture(findViewById(R.id.parkverseBunner))
//                .into((ImageView) findViewById(R.id.parkverseBunner));






        //SharedPreferences prefs = getSharedPreferences(MyBackupAgent.Prefs, Context.MODE_PRIVATE);
        // prefs.edit();

        //valuer = prefs.getString(MyBackupAgent.PREFS_BACKUP_KEY, "");
        //Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();
        //backupManager.dataChanged();

//        if(isExternalStorageReadable()){
//            File root = android.os.Environment.getExternalStorageDirectory();
//            File file = new File(root.getAbsolutePath() + "/external_storage_dir/vs/vs0");
//            if( file.exists() )
//            {
//
//
//                String readTickets = readExt(file);
//                ticketsNumber = Integer.valueOf(readTickets);
//                if(ticketsNumber>0)
//                {
//                    ticketsNumber--;
//                    if(isExternalStorageWritable())
//                    {
//                        String writeTickets=String.valueOf(ticketsNumber);
//                        writeToSDFile(writeTickets);
//                    }
//
//                }
//
//                Toast.makeText(MainActivity.this,readExt(file),Toast.LENGTH_SHORT).show();
//
//
//            }
//            else
//            {
//
//                if(isExternalStorageWritable())
//                {
//                    writeToSDFile("10");
//                    Toast.makeText(MainActivity.this,"file written",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//
//
//        }


        //String appStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("gr.appStatus", "appNotRun");
        //Toast.makeText(MainActivity.this, appStatus, Toast.LENGTH_SHORT).show();
        //String readtick="";

//        if (appStatus == "appNotRun") {
//            //writeTickets(20);
//            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("gr.appStatus", "appStarted").commit();
//            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("gr.mapZoom", "14").commit();
//
//
//            //String filename = "parkverse.tickets";
//            //String string = "10";
//            //FileOutputStream outputStream;
//
//            //readtick="10";
//
////            try {
////                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
////                outputStream.write(string.getBytes());
////                outputStream.close();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//
//        }
//        else
//        {
////            readtick = readtext("parkverse.tickets");
////            int writetick= Integer.valueOf(readtick) - 1;
////            String writeStr= String.valueOf(writetick);
////
////            String filename = "parkverse.tickets";
////
////            String string = writeStr;
////            FileOutputStream outputStream;
////
////
////            try {
////                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
////                outputStream.write(string.getBytes());
////                outputStream.close();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//
//        }

//        backupManager.requestRestore(new RestoreObserver() {
//            @Override
//            public void restoreFinished(int error) {
//                super.restoreFinished(error);
//
//                Toast.makeText(MainActivity.this, "finished backup", Toast.LENGTH_SHORT).show();
//                readtext("parkverse.tickets");
//
//            }
//        });










//        if (readTickets() > 0) {
//            ticketsAvailable = readTickets();
//            ticketsAvailable--;
//            writeTickets(ticketsAvailable);
//        }


        //Toast.makeText(MainActivity.this, String.valueOf(readTickets()), Toast.LENGTH_SHORT).show();

        //SharedPreferences preferences = getSharedPreferences("prefName", MODE_PRIVATE);
        //SharedPreferences.Editor edit= preferences.edit();

        // edit.putBoolean("isFirstRun", false);
        // edit.commit();

//        backupManager.requestRestore(new RestoreObserver() {
//            @Override
//            public void restoreFinished(int error) {
//                super.restoreFinished(error);
//
//                Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
//
//            }
//        });


        ////////////////
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
//
        // mHandler = new Handler();
//
        //drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //navigationView = (NavigationView) findViewById(R.id.nav_view);
//        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        //navHeader = navigationView.getHeaderView(0);
        //navHeader = navigationView.inflateHeaderView(R.id.view_container;
        // txtName = (TextView) navHeader.findViewById(R.id.name);
        // txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        // imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        /// imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        //activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // load nav menu header data
        //loadNavHeader();

        // initializing navigation menu
        //setUpNavigationView();

        //if (savedInstanceState == null) {
        //    navItemIndex = 0;
        //   CURRENT_TAG = TAG_HOME;
        //   loadHomeFragment();
        // }
        ////////////////

        //LinearLayout outer = (LinearLayout)findViewById(R.id.activity);

        final SupportMapFragment mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reminderMap);
        // mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(ReminderActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            //googleApiClient.connect();
        }
        //mylocationlistener
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        // Define the criteria how to select the location provider
//        criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);    //default
//        //criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
//
//        criteria.setCostAllowed(false);
//        // get the best provider depending on the criteria
//        provider = locationManager.getBestProvider(criteria, false);
//
//        // the last known location of this provider
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        mylistener = new MyLocationListener();
//
//        if (location != null) {
//            mylistener.onLocationChanged(location);
//        } else {
//
//            // Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            //startActivity(intent);
//
//        }
//        // location updates: at least 1 meter and 100millsecs change
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//
//            locationManager.requestLocationUpdates(provider, 100, 1, mylistener);
//            // return;
//        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //String appVersion = "v1";
        //Backendless.initApp(this, "B6A58452-911E-9A94-FF35-FF8ED63D1000", "4390E5FD-A1DE-1126-FFA9-0964FE46C000", appVersion);

        //final Handler h = new Handler();
        //final int delay = 10000; //milliseconds
//        h.postDelayed(new Runnable() {
//            public void run() {
//                //do something
//                //if(mapMoved)
//
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//                String directionsStatus=prefs.getString("directionsType", "1");
//                int value=0;
//                value=Integer.valueOf(directionsStatus);
//                //Toast.makeText(MainActivity.this, red,Toast.LENGTH_SHORT).show();
//
//                loadData();
//                clearMap();
//                //currentTime = (int) System.currentTimeMillis();
//
//                {
//                    for (MarkerOptions markeropt : mMarkerOptArray) {
//
//                        updateMap(markeropt);
//                        // markeropt.setVisible(false);
//                        //Marker marker =  mMap.addMarker(markeropt);
//                        //marker.remove(); //<-- works too!
//                    }
//                    //mapMoved = false;
//                }
//
//                showBack();
//
//                if(sortList.size()>1) {
//                    Object[] a = sortList.entrySet().toArray();
//                    Arrays.sort(a, new Comparator() {
//                        public int compare(Object o1, Object o2) {
//                            return ((Map.Entry<LatLng, Integer>) o2).getValue()
//                                    .compareTo(((Map.Entry<LatLng, Integer>) o1).getValue());
//                        }
//                    });
//
//
////                for (Object e : a) {
////                   // System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
////                           // ..+
////                                    Toast.makeText(MainActivity.this, String.valueOf(((Map.Entry<LatLng, Integer>) e).getKey()),Toast.LENGTH_SHORT).show();
////                }
//
//                    //Toast.makeText(MainActivity.this, String.valueOf(((Map.Entry<LatLng, Integer>) a[a.length - 1]).getKey()), Toast.LENGTH_SHORT).show();
//                    /////////////////////////////////////////////////////////////////////////////////
//
//                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
//                    //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
//                    boolean savedStatus = sharedPref.getBoolean("parkverse_status", true);
//
//                    LatLng directionsDestination = ( (Map.Entry<LatLng, Integer>) a[a.length - 1]).getKey();
//
//                    if(value<3) {
//                        String output = "json";
//                        String url = "https://maps.googleapis.com/maps/api/directions/"
//                                + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=" + directionsDestination.latitude + "," + directionsDestination.longitude + "&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";
//
//                        //String url = getMapsApiDirectionsUrl();
//                        ReadTask downloadTask = new ReadTask();
//                        downloadTask.execute(url);
//                    }
//                    /////////////////////////////////////////////////////////////////////////////////
//                //LatLng latlong = ((Map.Entry<LatLng, Integer>) a[a.length-1]).getKey();
//                }
////                String output = "json";
////                String url = "https://maps.googleapis.com/maps/api/directions/"
////                        + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=" + latlong.latitude + "," + latlong.longitude + "&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";
////
////                //String url = getMapsApiDirectionsUrl();
////                ReadTask downloadTask = new ReadTask();
////                downloadTask.execute(url);
//
//                sortList.clear();
//
//                ////////////
//                //Object myKey = sortHashMapByValuesD(sortList).keySet().toArray()[0];
//                //LatLng latlong= sortHashMapByValuesD(sortList).values().toArray()[0]
//
//                //Map.Entry<LatLng, Double> entry = sortHashMapByValuesD(sortList).entrySet().iterator().next();
//
////                String output = "json";
////                String url = "https://maps.googleapis.com/maps/api/directions/"
////                        + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";
////
////                //String url = getMapsApiDirectionsUrl();
////                ReadTask downloadTask = new ReadTask();
////                downloadTask.execute(url);
////////////////////////////////////
//
//
//
//                h.postDelayed(this, delay);
//            }
//
//        }, delay);



        //h.postDelayed(runnable,delay);


//        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
//            @Override
//            public void onCameraMove() {
//                CameraPosition cameraPosition = mMap.getCameraPosition();
////                if(cameraPosition.zoom > 18.0) {
////                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
////                } else {
////                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
////                }
//            }
//        });


        //   loginUserAndGetProperties();

//        viewMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawer.openDrawer(Gravity.RIGHT);
//            }
//        });



//        viewMenu.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        ImageView view = (ImageView) v;
//                        //overlay is black with transparency of 0x77 (119)
//                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
//                        view.invalidate();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:
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

    }





    public void sortAndShowDirections()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReminderActivity.this);
        String directionsStatus=prefs.getString("parkspace.directionsType", "1");
        int value=0;
        value=Integer.valueOf(directionsStatus);

        if(sortList.size()>1) {
            Object[] a = sortList.entrySet().toArray();
            Arrays.sort(a, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<LatLng, Integer>) o2).getValue()
                            .compareTo(((Map.Entry<LatLng, Integer>) o1).getValue());
                }
            });


//                for (Object e : a) {
//                   // System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
//                           // ..+
//                                    Toast.makeText(MainActivity.this, String.valueOf(((Map.Entry<LatLng, Integer>) e).getKey()),Toast.LENGTH_SHORT).show();
//                }

            //Toast.makeText(MainActivity.this, String.valueOf(((Map.Entry<LatLng, Integer>) a[a.length - 1]).getKey()), Toast.LENGTH_SHORT).show();
            /////////////////////////////////////////////////////////////////////////////////

            SharedPreferences sharedPref = ReminderActivity.this.getPreferences(Context.MODE_PRIVATE);
            //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
            boolean savedStatus = sharedPref.getBoolean("parkverse_status", true);

            LatLng directionsDestination = ( (Map.Entry<LatLng, Integer>) a[a.length - 1]).getKey();


            if (ActivityCompat.checkSelfPermission(ReminderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)!=null)
                {
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }
            }

            if(lastLocation.getLatitude()!=10&&lastLocation.getLongitude()!=10) {
                // syntetagmenes
                //Toast.makeText(MainActivity.this,lastLocation.getLatitude()+":"+lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();

                if (value < 3) {
                    String output = "json";
                    String url = "https://maps.googleapis.com/maps/api/directions/"
                            + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=" + directionsDestination.latitude + "," + directionsDestination.longitude + "&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";

                    //String url = getMapsApiDirectionsUrl();
                    if (isOnline()) {
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);
                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////
            //LatLng latlong = ((Map.Entry<LatLng, Integer>) a[a.length-1]).getKey();
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

    public void loadData() {
        {

            BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
            //geoQuery.addCategory("geoservice_sample");
            geoQuery.addCategory("parklist");
            geoQuery.addCategory("Businesses");
            geoQuery.addCategory("PopularFreeSpaces");
            geoQuery.setIncludeMeta( true );
            //geoQuery.setLatitude(mMap.getCameraPosition().target.latitude);
            //geoQuery.setLongitude(mMap.getCameraPosition().target.longitude);
            //geoQuery.setRadius(100000d);
            //geoQuery.setUnits(Units.KILOMETERS);
            //BackendlessCollection<GeoPoint> geoPoints = Backendless.Geo.getPoints( geoQuery );
            Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
                @Override
                public void handleResponse(BackendlessCollection<GeoPoint> points) {
                    //Toast.makeText(MainActivity.this, String.valueOf(points.getTotalObjects()), Toast.LENGTH_SHORT).show();
                    //points.getCurrentPage().get(0).toString();

                    Iterator<GeoPoint> iterator = points.getCurrentPage().iterator();

                    if(mMarkerOptArray.size()>0)
                    {
                        mMarkerOptArray.clear();
                    }

                    if(offlineMarkerOptions.size()>0)
                    {
                        offlineMarkerOptions.clear();
                    }

                    if(offlineFreeParkingSpots.size()>0)
                    {
                        offlineFreeParkingSpots.clear();
                    }

                    if(offlineFreePopularSpots.size()>0)
                    {
                        offlineFreePopularSpots.clear();
                    }

                    if(offlineParkingBusinessSpots.size()>0)
                    {
                        offlineParkingBusinessSpots.clear();
                    }


                    if(sortList.size()>0)
                    {
                        sortList.clear();
                    }
                    //mMap.clear();
                    int count=0;
                    while (iterator.hasNext())
                    {
                        GeoPoint geo = iterator.next();
                        count++;
                        //geo.getLatitude();
                        //geo.getLongitude();
                        String metadata;
                        String tag = "";
                        String created = "0";
                        metadata = String.valueOf(geo.getMetadata("category"));
                        tag = String.valueOf(geo.getMetadata("tag"));
                        created= String.valueOf(geo.getMetadata("created"));

                        // Toast.makeText(MainActivity.this,String.valueOf(geo.getLongitude()),Toast.LENGTH_SHORT).show();

                        //mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())));
                        if(geo!=null&&geo.getLatitude()!=10&geo.getLongitude()!=10) {
                            LatLng location = new LatLng(geo.getLatitude(), geo.getLongitude());

                            long time = System.currentTimeMillis();
//                        if (metadata != null) {
//                            //Toast.makeText(MainActivity.this, metadata,Toast.LENGTH_SHORT).show();}
//
//                            if (metadata.contains("busin")) {
//                                mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.business))).setTitle(tag);
//                            } else {
//                                mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re)));
//                            }
//                        }
                            // if (metadata != null) {
                            //Toast.makeText(MainActivity.this, metadata,Toast.LENGTH_SHORT).show();}

                            if (  (metadata.contains("busin")) && (status2==true)) {
                                MarkerOptions markeropt = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.popular_new_re)).title(tag);


                                //.title(mData.title)
                                //.snippet(mData.snippet));

                                mMarkerOptArray.add(markeropt);

                                offlineMarkerOptions.add(markeropt);
                                offlineParkingBusinessSpots.add(markeropt);

                                sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi

                            } else if ( (metadata.contains("popular")) && (status3==true) ){
                                MarkerOptions markeropt = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.free_new_re)).title(tag);


                                //.title(mData.title)
                                //.snippet(mData.snippet));

                                mMarkerOptArray.add(markeropt);

                                offlineMarkerOptions.add(markeropt);
                                offlineFreePopularSpots.add(markeropt);

                                sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi

                            }
                            else if (  (metadata.contains("free_")) && (status1==true) ){

                                MarkerOptions markeropt = new MarkerOptions().position(location).snippet(String.valueOf(time));
                                MarkerOptions returnedMarker= returnMarker(markeropt, created);


                                //.title(mData.title)
                                //.snippet(mData.snippet));
                                if(returnedMarker!=null) {
                                    mMarkerOptArray.add(returnedMarker);

                                    offlineMarkerOptions.add(returnedMarker);
                                    offlineFreeParkingSpots.add(returnedMarker);

                                    sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi
                                }
                            }


                            //  }

                            //sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi




                        }
                    }

                    sortAndShowDirections();

                    //for (MarkerOptions markeropt : mMarkerOptArray) {
                    //     mMap.addMarker(markeropt);
                    // markeropt.setVisible(false);
                    //Marker marker =  mMap.addMarker(markeropt);
                    //marker.remove(); //<-- works too!
                    // }


//                    ValueAnimator ani = ValueAnimator.ofFloat(1, 0); //change for (0,1) if you want a fade in
//                    ani.setDuration(500);
//                    ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            marker.setAlpha((float) animation.getAnimatedValue());
//
//                        }
//                    });
//                    ani.start();

                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    // System.err.println( String.format( "searchByDateInRadius FAULT = %s", fault ) );
                }
            });

            //cameralevel=mMap.getCameraPosition().zoom;
            //cameracenter=mMap.getCameraPosition().target;
        }
    }

    public MarkerOptions returnMarker(MarkerOptions mo, String serverTime){

        //MarkerOptions retMarker = new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_)).snippet(String.valueOf());
        MarkerOptions retMarker = mo;
        if(serverTime!=null)
        {
            long timeMillis= Long.valueOf(serverTime);
            long dif = Math.abs(System.currentTimeMillis() - timeMillis);

            if(dif<=60000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re));}
            else if(dif<=120000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.two_shadow_re));}
            else if(dif<=180000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.three_shadow_re));}
            else if(dif<=240000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.four_shadow_re));}
            else if(dif<=300000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.five_shadow_re));}
            else if(dif<=900000)
            {retMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.fiveplus_shadow_re));}
            else
            {retMarker=null;}

        }

        return retMarker;
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

            double radius= 1565430.3392 * Math.cos(offlineLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);
            circle0.setCenter(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
            circle0.setRadius(radius*1.25);

        }
        wasMoving = false;
    }

    @Override
    public void onCameraMove() {

        if(mMap!=null) {
            if((Math.abs(mMap.getCameraPosition().zoom - cameralevel)>0.4)) {

                VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                LatLng farLeft = vr.farLeft;
                LatLng farRight = vr.farRight;
                double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude);

                double dist_camera = distanceFrom(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, cameracenter.latitude, cameracenter.longitude);
                double ratio = dist_h / dist_camera;

                //offlineMapUpdate();

                cameralevel = mMap.getCameraPosition().zoom;

                //Toast.makeText(MainActivity.this,"moved",Toast.LENGTH_SHORT).show();


            }
        }
/*

        if((Math.abs(mMap.getCameraPosition().zoom-cameralevel)>0.4)||(ratio>(1/50)))
        {

            BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
            geoQuery.addCategory("geoservice_sample");
            geoQuery.setLatitude(mMap.getCameraPosition().target.latitude);
            geoQuery.setLongitude(mMap.getCameraPosition().target.longitude);
            geoQuery.setRadius(dist_h / 2);
            geoQuery.setUnits(Units.METERS);
            //BackendlessCollection<GeoPoint> geoPoints = Backendless.Geo.getPoints( geoQuery );
            Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
                @Override
                public void handleResponse(BackendlessCollection<GeoPoint> points) {

                    //points.getCurrentPage().get(0).toString();

                    Iterator<GeoPoint> iterator = points.getCurrentPage().iterator();
                    mMarkerOptArray.clear();
                    //mMap.clear();
                    while (iterator.hasNext()) {
                        GeoPoint geo = iterator.next();

                        //geo.getLatitude();
                        //geo.getLongitude();

                        //mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())));

                        LatLng location = new LatLng(geo.getLatitude(), geo.getLongitude());
                        long time=System.currentTimeMillis();
                        MarkerOptions markeropt = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_)).snippet(String.valueOf(time));
                                //.title(mData.title)
                                //.snippet(mData.snippet));

                        mMarkerOptArray.add(markeropt);




                    }

                    //for (MarkerOptions markeropt : mMarkerOptArray) {
                   //     mMap.addMarker(markeropt);
                       // markeropt.setVisible(false);
                        //Marker marker =  mMap.addMarker(markeropt);
                        //marker.remove(); //<-- works too!
                   // }


//                    ValueAnimator ani = ValueAnimator.ofFloat(1, 0); //change for (0,1) if you want a fade in
//                    ani.setDuration(500);
//                    ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            marker.setAlpha((float) animation.getAnimatedValue());
//
//                        }
//                    });
//                    ani.start();

                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    // System.err.println( String.format( "searchByDateInRadius FAULT = %s", fault ) );
                }
            });

            cameralevel=mMap.getCameraPosition().zoom;
            cameracenter=mMap.getCameraPosition().target;
        }


*/

    }

    private ArrayList<MarkerOptions> mMarkerOptArray = new ArrayList<MarkerOptions>();

    private ArrayList<MarkerOptions> offlineMarkerOptions = new ArrayList<>();
    private ArrayList<LatLng> offlinePointsLatLng = new ArrayList<>();
    private Location offlineLocation = new Location("offlineLocation");

    private ArrayList<MarkerOptions> offlineFreeParkingSpots = new ArrayList<>();
    private ArrayList<MarkerOptions> offlineFreePopularSpots = new ArrayList<>();
    private ArrayList<MarkerOptions> offlineParkingBusinessSpots = new ArrayList<>();

    private void updateOfflineLocation (){
        if (ActivityCompat.checkSelfPermission(ReminderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)!=null)
            {
                offlineLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
        }
    }

    public void offlineMapUpdate(){

        updateOfflineLocation();

        PolylineOptions offlinePolylineOptions = new PolylineOptions();
        offlinePolylineOptions.width(25);
        offlinePolylineOptions.color(Color.DKGRAY);
        offlinePolylineOptions.addAll(offlinePointsLatLng);

        LatLng positionStart = new LatLng(offlineLocation.getLatitude() , offlineLocation.getLongitude());

        double radius= 1565430.3392 * Math.cos(offlineLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);

        mMap.clear();

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(positionStart)
                .radius(radius*1.25)
                .fillColor(Color.DKGRAY)
                .strokeColor(Color.GRAY)
                .strokeWidth(10));

        if(offlinePointsLatLng!=null){
            mMap.addPolyline(offlinePolylineOptions);
        }

        if(offlineMarkerOptions!=null) {
            //Toast.makeText(MainActivity.this,"size:"+String.valueOf(mMarkerOptArray.size()),Toast.LENGTH_LONG).show();

            for (MarkerOptions offMarkerOpt : offlineMarkerOptions) {

                updateMap(offMarkerOpt);

            }
        }
    }

    public void offlineMapUpdateOnClick(){

        updateOfflineLocation();

        PolylineOptions offlinePolylineOptions = new PolylineOptions();
        offlinePolylineOptions.width(25);
        offlinePolylineOptions.color(Color.DKGRAY);
        offlinePolylineOptions.addAll(offlinePointsLatLng);


        LatLng positionStart = new LatLng(offlineLocation.getLatitude() , offlineLocation.getLongitude());

        double radius= 1565430.3392 * Math.cos(offlineLocation.getLatitude() * Math.PI / 180) / Math.pow(2, mMap.getCameraPosition().zoom);

        mMap.clear();

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(positionStart)
                .radius(radius*1.25)
                .fillColor(Color.DKGRAY)
                .strokeColor(Color.GRAY)
                .strokeWidth(10));

        if(status1||status2||status3) {
            if (offlinePointsLatLng != null) {
                mMap.addPolyline(offlinePolylineOptions);
            }
        }

        if(status1) {
            if (offlineFreeParkingSpots != null) {
                for (MarkerOptions offMarkerOpt : offlineFreeParkingSpots) {
                    updateMap(offMarkerOpt);
                }
            }
        }


        if(status2) {
            if (offlineParkingBusinessSpots != null) {
                for (MarkerOptions offMarkerOpt : offlineParkingBusinessSpots) {
                    updateMap(offMarkerOpt);
                }
            }
        }
        if(status3) {
            if (offlineFreePopularSpots != null) {
                for (MarkerOptions offMarkerOpt : offlineFreePopularSpots) {
                    updateMap(offMarkerOpt);
                }
            }
        }

    }




//    @Override
//    public void onCameraIdle() {
//
////        if(wasMoving)
////        {
////
////            for (MarkerOptions markeropt : mMarkerOptArray) {
////                mMap.addMarker(markeropt);
////                //markeropt.setVisible(false);
////                //Marker marker =  mMap.addMarker(markeropt);
////                //marker.remove(); <-- works too!
////            }
////
////        }
//
//        wasMoving = false;
//    }

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


    private String getMapsApiDirectionsUrl() {
        // String waypoints = "waypoints=optimize:true|"
        //  + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
        //  + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
        //  + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
        //  + WALL_STREET.longitude;
        //String startLoc="origin=Toronto";
        // String destLoc="&destination=Montreal";
        //String sensor = "sensor=false";
        //String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "origin=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&destination=";//+RouteActivity.getRouteFinishLat()+","+RouteActivity.getRouteFinishLong()+"&key=AIzaSyALyxTBvX1YaD9ZJerQQb_mvnP-OP8xu0E";
        return url;
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


    private class PingTask extends AsyncTask<String, Void, Boolean> {
        public PingTask asyncPing;

        @Override
        protected void onPreExecute() {
            ////////////////////////////////////////////////////////////////////////////////////////
            asyncPing = this;
            new CountDownTimer(10000, 10000) {
                public void onTick(long millisUntilFinished) {
                    // You can monitor the progress here as well by changing the onTick() time
                }

                public void onFinish() {
                    // stop async task if not in progress
                    if (asyncPing.getStatus() == AsyncTask.Status.RUNNING) {
                        asyncPing.cancel(true);
                        // Add any specific task you wish to do as your extended class variable works here as well.
                    }
                }
            }.start();
            ////////////////////////////////////////////////////////////////////////////////////////
        }

        protected Boolean doInBackground(String... params) {
            String url = params[0];
            int port = Integer.parseInt(params[1]);
            boolean success = false;

//            try {
//                //success = pingURL(url, port);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return success;
        }

        protected void onPostExecute(Boolean result) {
            // do something when a result comes from the async task.
            if (result) {
                //internetConnection=true;
                ///////////////////////////// Toast.makeText(MainActivity.this, "internet on", Toast.LENGTH_SHORT).show();
                try {
                    //String url = getMapsApiDirectionsUrl();
                    //ReadTask downloadTask = new ReadTask();
                    //downloadTask.execute(url);
                } catch (Exception e) {
                }

                ////////////////////
                //repeatHandler();
                /////////////////////
            }
            // else {internetConnection=false;}

        }

        @Override
        protected void onCancelled() {
            //internetConnection=false;

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

    private void setUpNavigationView() {
        navigationView.setItemIconTintList(null);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        drawer.closeDrawers();

                        break;
//                    case R.id.nav_photos:
//                        navItemIndex = 1;
//                        CURRENT_TAG = TAG_PHOTOS;
//                        break;
//                    case R.id.nav_movies:
//                        navItemIndex = 2;
//                        CURRENT_TAG = TAG_MOVIES;
//                        break;
//                    case R.id.nav_notifications:
//                        navItemIndex = 3;
//                        CURRENT_TAG = TAG_NOTIFICATIONS;
//                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        drawer.closeDrawers();
                        Intent intent= new Intent(ReminderActivity.this,MyPreferencesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left,
                                R.anim.left_to_right);
                        break;
                    case R.id.nav_tutorial:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_TUTORIAL;
                        drawer.closeDrawers();
                        Intent intentTutorial= new Intent(ReminderActivity.this,IntroActivity.class);
                        startActivity(intentTutorial);
                        overridePendingTransition(R.anim.right_to_left,
                                R.anim.left_to_right);
                        break;

                    case R.id.nav_parkversetag:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_PARKVERSETAG;
                        drawer.closeDrawers();
                        openWebURL("http://www.parkverse.tech/parkverse-sticker");
                        //overridePendingTransition(R.anim.right_to_left,
                        //      R.anim.left_to_right);
                        break;

                    case R.id.nav_tickets:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_TICKETS;
                        drawer.closeDrawers();
                        Intent intentTickets= new Intent(ReminderActivity.this,ShowTicketsActivity.class);
                        startActivity(intentTickets);
                        overridePendingTransition(R.anim.right_to_left,R.anim.left_to_right);
                        break;

                    case R.id.nav_privacypolicy:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_REMINDER;
                        drawer.closeDrawers();
                        Intent intentPolicy= new Intent(ReminderActivity.this,PolicyActivity.class);
                        startActivity(intentPolicy);
                        overridePendingTransition(R.anim.right_to_left,R.anim.left_to_right);
                        break;


//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    //break;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        // drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        // actionBarDrawerToggle.syncState();
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

            if (ActivityCompat.checkSelfPermission(ReminderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
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
//            if (isOnline()) {
//                loginUserAndSavePoint();
//            }

//            if(isOnline())
//            {
//
//
//                if (lastLocation.getLongitude() != 10 && lastLocation.getLatitude() != 10) {
//                    new SnapAsync().execute();
//                }
//            }
        }
    }

    /** Method to read in a text file placed in the res/raw directory of the application. The
     method reads in all lines of the file sequentially. */








}
