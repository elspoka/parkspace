package tech.crossroads.parkspace;

import android.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Xristos on 28/3/2017.
 */

public class NotifyService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    GoogleApiClient googleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START YOUR TASKS

        String appVersion = "v1";
        Backendless.initApp(this, "B6A58452-911E-9A94-FF35-FF8ED63D1000", "4390E5FD-A1DE-1126-FFA9-0964FE46C000", appVersion);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(NotifyService.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private ArrayList<MarkerOptions> offlineFreeParkingSpots = new ArrayList<>();
    public HashMap<LatLng, Integer> sortList = new HashMap<LatLng, Integer>();

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

    public void loadFreeSpots() {
        {

            BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
            //geoQuery.addCategory("geoservice_sample");
            geoQuery.addCategory("parklist");
            //geoQuery.addCategory("Businesses");
            //geoQuery.addCategory("PopularFreeSpaces");
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

                    if(offlineFreeParkingSpots.size()>0)
                    {
                        offlineFreeParkingSpots.clear();
                    }


                    if(sortList.size()>0)
                    {
                        sortList.clear();
                    }

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
                        long createdMillis = Long.valueOf(created);

                        // Toast.makeText(MainActivity.this,String.valueOf(geo.getLongitude()),Toast.LENGTH_SHORT).show();

                        //mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitude(), geo.getLongitude())));
                        if(geo!=null&&geo.getLatitude()!=10&geo.getLongitude()!=10) {
                            LatLng location = new LatLng(geo.getLatitude(), geo.getLongitude());

                            long time = System.currentTimeMillis();

                            if (  (metadata.contains("free_"))  ){

                                MarkerOptions markeropt = new MarkerOptions().position(location).snippet(String.valueOf(time));
                                //MarkerOptions returnedMarker= returnMarker(markeropt, created);

                                if( createdMillis > (time-900000) ) {
                                    //if (returnedMarker != null) {

                                        offlineFreeParkingSpots.add(markeropt);

                                        sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi
                                    //}
                                }
                            }


                            //  }

                            //sortList.put(location, (int) distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), location.latitude, location.longitude));// taksinomisi analoga me apostasi




                        }
                    }

                    sortAndNotify();

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

    public void sortAndNotify()
    {
        Boolean onTheMoveBoolean = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("parkspace.onTheMove", true);



        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //String directionsStatus=prefs.getString("parkspace.directionsType", "1");
       // int value=0;
        //value=Integer.valueOf(directionsStatus);

        if(sortList.size()>1) {
            Object[] a = sortList.entrySet().toArray();
            Arrays.sort(a, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<LatLng, Integer>) o2).getValue()
                            .compareTo(((Map.Entry<LatLng, Integer>) o1).getValue());
                }
            });

            //SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
            //boolean savedStatus = sharedPref.getBoolean("parkverse_status", true);

            LatLng directionsDestination = ( (Map.Entry<LatLng, Integer>) a[a.length - 1]).getKey();


            //if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
               // if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)!=null)
               // {
                //    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
              //  }
           // }

            if(distanceFrom(lastLocation.getLatitude(),lastLocation.getLongitude(), directionsDestination.latitude,directionsDestination.longitude)<1000) {

                dist = distanceFrom(lastLocation.getLatitude(),lastLocation.getLongitude(), directionsDestination.latitude,directionsDestination.longitude);
                if(onTheMoveBoolean) {
                    notificationShow();
                }

            }

        }
        else if (sortList.size()==1)
        {

            LatLng directionsDestination=new LatLng(0,0);
            for (LatLng key : sortList.keySet()) {
                directionsDestination = key;
            }

            if( (distanceFrom(lastLocation.getLatitude(),lastLocation.getLongitude(), directionsDestination.latitude,directionsDestination.longitude)>50) &&
                    (distanceFrom(lastLocation.getLatitude(),lastLocation.getLongitude(), directionsDestination.latitude,directionsDestination.longitude)<1000) )
            {

                dist = distanceFrom(lastLocation.getLatitude(), lastLocation.getLongitude(), directionsDestination.latitude, directionsDestination.longitude);

                if(onTheMoveBoolean) {
                    notificationShow();
                }
            }
        }



    }

    double dist=0;

    public void notificationShow(){

        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        DecimalFormat df = new DecimalFormat("0.00");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.one_shadow_re)
                        .setContentTitle("Parking spot available")
                        .setContentText("Closest spot is "+ df.format(dist/1000)+" km away");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

            mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS

        if (googleApiClient != null) {
            if(googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationRequest locationRequest;

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connected", "Connected");

        try {
            // location2 = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // textc.setText(String.valueOf(location2.getLatitude()));
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(1 * 120000);
            locationRequest.setFastestInterval(1 * 120000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);


            //requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }


        }
        catch (Exception e){}
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    Location lastLocation = new Location("lastLocation");

    @Override
    public void onLocationChanged(Location location) {

       // Toast.makeText(NotifyService.this, "dfgdg", Toast.LENGTH_SHORT).show();
        lastLocation=location;
        if(isOnline())
        {
            loadFreeSpots();
        }

        //notificationShow();



    }

    public boolean isOnline() {
        //gia ckeck an yparxei internet//
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}