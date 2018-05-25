package tech.crossroads.parkspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectShareActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener {


    final static int REQUEST_LOCATION_INT = 1000;
    Handler handler2 = new Handler();
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    PendingResult<LocationSettingsResult> result;
    Status status;
    LocationSettingsStates state;
    Location lastLocation = null;

    public class WaitForLocationAsync extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... url) {

            if (ActivityCompat.checkSelfPermission(SelectShareActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                while (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) == null) {
                    //wait
                }
            }
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        @Override
        protected void onPostExecute(Location result) {
            super.onPostExecute(result);
            lastLocation=result;

        }
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    public class WaitForLocationAndWrite extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... url) {

            if (ActivityCompat.checkSelfPermission(SelectShareActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                while (lastLocation == null) {
                    //wait
                }
            }
            return lastLocation;
        }
        @Override
        protected void onPostExecute(Location result) {
            super.onPostExecute(result);

            writeReminderLocation(lastLocation.getLatitude(),lastLocation.getLongitude());
            Toast.makeText(SelectShareActivity.this,"Parking reminder saved", Toast.LENGTH_SHORT).show();
            startCountdown();

        }
    }

    public void startCountdown(){

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);

            }
        }, 2000);

    }

    @Override
    protected void onStart() {

        googleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onPause(){
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(SendLocation.this,String.valueOf(location.getLatitude()+" lat"),Toast.LENGTH_SHORT).show();
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
    public void onConnected(Bundle bundle) {
        Log.d("Connected", "Connected");

        try {
            // location2 = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // textc.setText(String.valueOf(location2.getLatitude()));
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(1 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
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
                                        SelectShareActivity.this, REQUEST_LOCATION_INT);
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


    }

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
    RelativeLayout relativeLayoutShare;
    RelativeLayout relativeLayoutSetRem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_share);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(SelectShareActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            //googleApiClient.connect();
        }

        relativeLayoutShare= (RelativeLayout) findViewById(R.id.relativeLayoutShare);
        relativeLayoutSetRem = (RelativeLayout) findViewById(R.id.relativeLayoutSetReminder);

        relativeLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectShareActivity.this, SendLocation.class);
                startActivity(intent);
            }
        });

        relativeLayoutSetRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new WaitForLocationAndWrite().execute();
            }
        });


    }

    public void writeReminderLocation (double lat, double lng){
        //float mapZoom = mMap.getCameraPosition().zoom;
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parkspace.reminder.latitude", String.valueOf(lat)).commit();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parkspace.reminder.longitude", String.valueOf(lng)).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        //mHideHandler.removeCallbacks(mHideRunnable);
       // mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
