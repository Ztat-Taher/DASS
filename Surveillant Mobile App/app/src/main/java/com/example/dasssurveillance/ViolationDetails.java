package com.example.dasssurveillance;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import org.osmdroid.util.GeoPoint;

public class ViolationDetails extends AppCompatActivity {

    private TextView violationTypeTextView;
    private TextView violationTimestampTextView;
    private MapView mapView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_details);

        // Initialize Views
        violationTypeTextView = findViewById(R.id.violationTypeTextView);
        violationTimestampTextView = findViewById(R.id.violationTimestampTextView);
        mapView = findViewById(R.id.ivMapPreview);

        // Get Data from Intent
        String violationType = getIntent().getStringExtra("violation_type");
        String violationTimestamp = getIntent().getStringExtra("violation_timestamp");
        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Set Data to Views
        violationTypeTextView.setText("Type: " + violationType);
        violationTimestampTextView.setText("Date: " + violationTimestamp);

        // Configure and Set Up Map
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        GeoPoint location = new GeoPoint(latitude, longitude);
        mapView.getController().setCenter(location);

        // Add a Marker for the Violation Location
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setTitle("Violation Location");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Back button functionality
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Needed for MapView to work
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Needed for MapView to work
    }
}
