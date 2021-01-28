package com.example.mosis_ispit.addon;

public class MyLocationListener implements LocationListener {

    public void onLocationChanged(Location location) {
        currentLocation = new GeoPoint(location);
        displayMyCurrentLocationOverlay();
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}