package com.myapplicationdev.android.demolocationdetection;

import android.Manifest;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button btnGetLastLocation, btnGetLastLocationUpdate, btnRemoveLastLocationUpdate;
    FusedLocationProviderClient client;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetLastLocation = findViewById(R.id.btnGetLastLocation);
        btnGetLastLocationUpdate = findViewById(R.id.btnGetLastLocationUpdate);
        btnRemoveLastLocationUpdate = findViewById(R.id.btnRemoveLocationUpdate);

        client = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    Toast.makeText(MainActivity.this, "New Location Detected\nLat: " + lat + "\nLong: " + lng, Toast.LENGTH_SHORT).show();
                }
            };
        };

        btnGetLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission() == true) {
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String msg = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = "No Last Known Location Found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String msg = "Permission is not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
            }
        });

        btnGetLastLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission() == true) {
                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setSmallestDisplacement(100);

                    client.requestLocationUpdates(locationRequest,locationCallback, null);

                } else {
                    String msg = "Permission is not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
            }
        });

        btnRemoveLastLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.removeLocationUpdates(locationCallback);
            }
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
