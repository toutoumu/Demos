package com.example.livedata;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;

public class LocationLiveData extends LiveData<Location> {
  private static LocationLiveData sInstance;
  private final Context mContext;
  private LocationManager locationManager;

  @MainThread
  public static LocationLiveData get(Context context) {
    if (sInstance == null) {
      sInstance = new LocationLiveData(context.getApplicationContext());
    }
    return sInstance;
  }

  private SimpleLocationListener listener = new SimpleLocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      setValue(location);
    }
  };

  private LocationLiveData(Context context) {
    this.mContext = context.getApplicationContext();
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  @Override
  protected void onActive() {
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED
      && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
  }

  @Override
  protected void onInactive() {
    locationManager.removeUpdates(listener);
  }

  public static class SimpleLocationListener implements LocationListener {

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
}