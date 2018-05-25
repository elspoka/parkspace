package tech.crossroads.parkspace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SendLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener{

    final static int REQUEST_LOCATION_INT = 1000;
    Handler handler2 = new Handler();
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    PendingResult<LocationSettingsResult> result;
    Status status;
    LocationSettingsStates state;
    Location lastLocation = null;
    String timeFromServer = "0";
    String timeFromDevice = "0";

    public boolean isOnline() {
        //gia ckeck an yparxei internet//
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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

    // Trigger new location updates at interval
    public void startLocationUpdates() {
        // Create the location request

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    public class WaitForLocationAsync extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... url) {

            if (ActivityCompat.checkSelfPermission(SendLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                while (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) == null) {
                    //wait
                }
            }
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        @Override
        protected void onPostExecute(Location result) {
            super.onPostExecute(result);
            //Toast.makeText(SendLocation.this, "got it!", Toast.LENGTH_SHORT).show();

            lastLocation=result;

            //Toast.makeText(SendLocation.this,lastLocation.getLatitude()+" " +lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();

            if (isOnline()) {
                loginUserAndSavePoint();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connected", "Connected");
        //Toast.makeText(SendLocation.this, "connected", Toast.LENGTH_SHORT).show();



        try {
            // location2 = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // textc.setText(String.valueOf(location2.getLatitude()));
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(1 * 500);
            locationRequest.setFastestInterval(1 * 500);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);


            //requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************
            // if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) MainActivity.this);}

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
            //startLocationUpdates();

            result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    status = result.getStatus();  //final Status
                    state = result.getLocationSettingsStates();   //final LocationSettingsStates
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            //Toast.makeText(SendLocation.this, "success", Toast.LENGTH_SHORT).show();
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            //////////////////
                            //Toast.makeText(MainActivity.this, "Success, getting position", Toast.LENGTH_SHORT).show();

                            //BackendlessUser user0 = new BackendlessUser();
                            // user0.setEmail   ( "a@b.g" );
                            //emailString=email.getText().toString();
                            // user0.setPassword(  "1234" );
                            //passString=password.getText().toString();
                            new WaitForLocationAsync().execute();


//                            handler2.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    Intent a = new Intent(Intent.ACTION_MAIN);
//                                    a.addCategory(Intent.CATEGORY_HOME);
//                                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(a);
//
//                                }
//                            }, 5000);
//
//                            if (ActivityCompat.checkSelfPermission(SendLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                                if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)!=null) {
//                                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                                    Toast.makeText(SendLocation.this, "got location", Toast.LENGTH_SHORT).show();
//
//                                    if (isOnline()) {
//
//
//                                        loginUserAndSavePoint();
//                                        //Map map =Backendless.Persistence.of( "ServerTime" ).findById( "5CFA9FB2-DA4A-C033-FFF2-16FFED54C700" );
//
//                                    }
//                                    ////////////////
//                                    //readyLocation();
//                                    //MarkerOptions mo=new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position)).position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).rotation(bearing).anchor(0.5f,0.5f);
//                                    //Marker mark= mMap.addMarker(mo);
//                                }
//                            }


                            //saveNewPoint();

                            //updateUI();


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
                                        SendLocation.this, REQUEST_LOCATION_INT);
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
                        Toast.makeText(SendLocation.this, "Location not enabled", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    public void loginUserAndSavePoint() {

        Backendless.UserService.login("a@b.g", "1234", new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser loggedUser) {
                //Toast.makeText( MainActivity.this, "User has been logged in: " + loggedUser, Toast.LENGTH_SHORT ).show();

                //Toast.makeText(SendLocation.this, "logged in", Toast.LENGTH_SHORT).show();

                BackendlessUser user = Backendless.UserService.CurrentUser();
                if (user != null) {

//                    Backendless.Persistence.of("ServerTime").findById("5CFA9FB2-DA4A-C033-FFF2-16FFED54C700",
//                            new AsyncCallback<Map>() {
//                                @Override
                                //public void handleResponse(Map response)
                                {
                                    // an object from the "Contact" table has been found by it's objectId

                                    //timeFromServer = String.valueOf(response.get("name"));
                                    long timeIs = System.currentTimeMillis();
                                    timeFromDevice = String.valueOf(timeIs);


                                    //Toast.makeText(SendLocation.this, "device time", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(SendLocation.this, timeFromServer, Toast.LENGTH_SHORT).show();

                                    if (isOnline()) {
                                        //Toast.makeText(SendLocation.this, "snap", Toast.LENGTH_SHORT).show();

                                        new SnapAsync().execute();
                                    }

                                }

//                                @Override
//                                public void handleFault(BackendlessFault fault) {
//                                    // an error has occurred, the error code can be retrieved with fault.getCode()
//                                }
//                            });
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

//                    BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
//                    geoQuery.addCategory("parklist");
//                    geoQuery.addCategory("Businesses");
//                    geoQuery.setIncludeMeta( true );
                    //geoQuery.setLatitude(lastLocation.getLatitude());
                    //geoQuery.setLongitude(lastLocation.getLongitude());
                    //geoQuery.setRadius(1000000d);
                    //geoQuery.setUnits(Units.KILOMETERS);
                    //BackendlessCollection<GeoPoint> geoPoints = Backendless.Geo.getPoints( geoQuery );
//                    Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
//                        @Override
//                        public void handleResponse(BackendlessCollection<GeoPoint> points) {
//                            //Toast.makeText(MainActivity.this, String.valueOf(points.getTotalObjects()), Toast.LENGTH_SHORT).show();
//                            //points.getCurrentPage().get(0).toString();
//
//                            Iterator<GeoPoint> iterator = points.getCurrentPage().iterator();
//
//                            while (iterator.hasNext()) {
//                                GeoPoint geo = iterator.next();
//
//                                //geo.getLatitude();
//                                //geo.getLongitude();
//                                String metadata;
//                                String tag = "";
//                                metadata = String.valueOf(geo.getMetadata("category"));
//                                tag = String.valueOf(geo.getMetadata("tag"));
//
//                                // if (metadata != null) {
//                                //Toast.makeText(MainActivity.this, metadata,Toast.LENGTH_SHORT).show();}
//
//                                // if (metadata.contains("busin")) {
//                                // mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re))).setTitle(tag);
//                                // } else {
//                                mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.one_shadow_re)));
//                                // }
//                                // }
//                                // Toast.makeText(MainActivity.this,String.valueOf(geo.getLongitude()),Toast.LENGTH_SHORT).show();
//                            }
//
//
//                        }
//
//                        @Override
//                        public void handleFault(BackendlessFault fault) {
//                            // System.err.println( String.format( "searchByDateInRadius FAULT = %s", fault ) );
//                        }
//                    });


                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(intent);

                    //setContentView(R.layout.activity_main);


                } else {
                    //Toast.makeText(MainActivity.this, "User hasn't been logged", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // new AlertDialog.Builder(SendLocation.this).setMessage("Server reported an error: " + fault).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.ok, null).show();
            }
        });
    }

    public class SnapAsync extends AsyncTask<String, String, JSONObject> {

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
                    String timeEpoch = timeFromDevice;

                    List<String> category = new ArrayList<String>();
                    category.add("parklist");
                    //categories.add( "cool_places" );

                    Map<String, Object> meta = new HashMap<String, Object>();
                    meta.put("created", timeEpoch);
                    meta.put("category", "free_spots");

                    Backendless.Geo.savePoint(snapLat, snapLong, category, meta, new AsyncCallback<GeoPoint>() {
                        @Override
                        public void handleResponse(GeoPoint geoPoint) {
                            // System.out.println( geoPoint.getObjectId() );

                            Toast.makeText(SendLocation.this, "Park spot saved", Toast.LENGTH_SHORT).show();
                            writePlusOneTicket();



                            startCountdown();

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                        }
                    });

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

    private int ticketsNumber=0;

    public void writePlusOneTicket(){
        if(isExternalStorageReadable()) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/external_storage_dir/vs/vs0");
            if (file.exists()) {
                String readTickets = readExt(file);
                ticketsNumber = Integer.valueOf(readTickets);
                if (ticketsNumber >= 0) {
                    ticketsNumber++;
                    if (isExternalStorageWritable()) {
                        String writeTickets = String.valueOf(ticketsNumber);
                        writeToSDFile(writeTickets);
                    }
                }
                //Toast.makeText(MainActivity.this, readExt(file), Toast.LENGTH_SHORT).show();
            }
        }
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
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_location);

        String appVersion = "v1";
        Backendless.initApp(this, "B6A58452-911E-9A94-FF35-FF8ED63D1000", "4390E5FD-A1DE-1126-FFA9-0964FE46C000", appVersion);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(SendLocation.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            //googleApiClient.connect();
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Intent a = new Intent(Intent.ACTION_MAIN);
//                a.addCategory(Intent.CATEGORY_HOME);
//                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(a);
//
//            }
//        }, 5000);

        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
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
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
