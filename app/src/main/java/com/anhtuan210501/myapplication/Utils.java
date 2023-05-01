package com.anhtuan210501.myapplication;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Utils {

    public static float getBearing(LatLng begin, LatLng end) {
        Location beginLocation = new Location("");
        beginLocation.setLatitude(begin.latitude);
        beginLocation.setLongitude(begin.longitude);

        Location endLocation = new Location("");
        endLocation.setLatitude(end.latitude);
        endLocation.setLongitude(end.longitude);

        return beginLocation.bearingTo(endLocation);
    }

    public static float calculateHeading(LatLng from, LatLng to) {
        double fromLat = Math.toRadians(from.latitude);
        double toLat = Math.toRadians(to.latitude);
        double deltaLng = Math.toRadians(to.longitude - from.longitude);

        double y = Math.sin(deltaLng) * Math.cos(toLat);
        double x = Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat) * Math.cos(toLat) * Math.cos(deltaLng);

        double bearing = Math.atan2(y, x);
        return (float) Math.toDegrees(bearing);
    }
}
