package com.anhtuan210501.myapplication;

import com.google.android.gms.maps.model.LatLng;

public class Node {
    private LatLng location;
    private int index;

    public Node(LatLng location, int index) {
        this.location = location;
        this.index = index;
    }

    public LatLng getLocation() {
        return location;
    }

    public int getIndex() {
        return index;
    }
}
