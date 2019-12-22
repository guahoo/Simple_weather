package com.example.simpleweather.Utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simpleweather.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.core.app.ActivityCompat;

public final class GeoLocationFinder implements LocationListener {
    private int PERMISSION_ID = 44;

    FusedLocationProviderClient mFusedLocationClient;
    Context context;
    SharedPreferences sharedPreferences;

    public static String getLatitude() {
        return latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    private static String latitude, longitude;


    public GeoLocationFinder(Context context, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.context=context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    @SuppressLint("MissingPermission")
     void getLastLocation(){
        if (!requestPermissions() && !requestLastLocation()) {
            startActivityTurnOnLocation();
        }
    }

    private boolean requestLastLocation() {
        if (!isLocationEnabled()) {
            return false;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                task -> {
                    Location location = task.getResult();
//                    if (location == null) {
                    requestNewLocationData();
//                        return;
//                    }
                    latitude = String.valueOf((location.getLatitude()));
                    longitude = String.valueOf((location.getLongitude()));
                    new searchByGeoposition(context, sharedPreferences).execute();
                }
        );
        return true;
    }

    private void startActivityTurnOnLocation() {
        Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }


    @SuppressLint("MissingPermission")
       public void requestNewLocationData(){
//TODO: разобраться с обновлением местоположения
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

     private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude= String.valueOf((mLastLocation.getLatitude()));
            longitude= String.valueOf((mLastLocation.getLongitude()));

        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean requestPermissions() {
        if (checkPermissions()) {
            return false;
        }
        ActivityCompat.requestPermissions(
                (MainActivity)context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID

        );
        return true;
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}