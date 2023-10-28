package com.example.finalfrate;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class Utils {

    public static void clearMarkers(List<Marker> markers) {
        for (Marker marker: markers) {
            marker.remove();
        }
        markers.clear();
    }
}

