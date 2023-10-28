package com.example.finalfrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private MapView mapView;
    private LatLng coordinatesToSend;
    private GoogleMap googleMap;

    private List<Marker> markers = new ArrayList<>();

    Button btnAlert;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("locations");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        btnAlert = findViewById(R.id.sendToFirebaseButton);
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(coordinatesToSend!=null) {
                    sendCoordinatesToFirebase(coordinatesToSend);
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // You can customize the map and add markers, polylines, etc. here.
// Inside the onMapReady method
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    MyLatLong myLatLng = locationSnapshot.getValue(MyLatLong.class);

                    // Create markers on the map for each saved location
                    if (myLatLng != null) {
                        LatLng latLng = new LatLng(myLatLng.latitude, myLatLng.longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title("Location")
                                .snippet("Saved Location Description");
                        Marker newMarker = map.addMarker(markerOptions);
                        markers.add(newMarker);
                    }
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database Error: " + error.getMessage());
            }
        });

        // Enable the My Location layer on the map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        googleMap.setMyLocationEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Clear existing markers

                // Add a marker at the clicked location
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Your Marker Title")
                        .snippet("Marker Description");

                coordinatesToSend = latLng; // for button click event

                // Add the new marker to the map and the list
                Marker newMarker = map.addMarker(markerOptions);
                markers.add(newMarker);
            }



        });

        // Set up a location change listener to zoom to the user's location
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@NonNull Location location) {
                // Get the user's current location
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Zoom to the user's location
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.5f));

                // Remove the listener to avoid unnecessary zooming
                googleMap.setOnMyLocationChangeListener(null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    // Send the coordinates to Firebase
    private void sendCoordinatesToFirebase(LatLng latLng) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("locations");

        LatLng cord = coordinatesToSend; // Your LatLng object
        MyLatLong myLatLng = new MyLatLong(latLng.latitude, latLng.longitude);
        String key = databaseReference.push().getKey();

        if (key != null) {
            databaseReference.child(key).setValue(myLatLng);
        }
    }

    // Handle other lifecycle methods like onPause, onDestroy, etc.
}
