package com.example.mariohernandez.supergeolocator;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mariohernandez on 17/09/15.
 */
public class LocationService implements LocationListener {
    private final Context ctx;
    Location location;
    boolean gpsActivo;
    LocationManager locationManager;

    public LocationService(Context con){
        super();
        this.ctx = con;
        getLocation();
    }

    public void getLocation(){

        try{
            locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(gpsActivo){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60 ,10,this);
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

          /*  if(location != null) {
                Toast.makeText(getApplicationContext(), "Coordenadas : " + location, Toast.LENGTH_LONG).show();
            } */
        }



    }

    public Location darUbicacion(){
        return location;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        this.location = location;
     /*   if(location != null) {
            Toast.makeText(getApplicationContext(), "Coordenadas : " + location, Toast.LENGTH_LONG).show();
        }*/
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
