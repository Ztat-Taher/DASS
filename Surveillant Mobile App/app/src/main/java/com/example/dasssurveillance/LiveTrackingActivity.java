package com.example.dasssurveillance;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

public class LiveTrackingActivity extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;
    private ImageButton backButton;
    private Marker carMarker; // Declare a Marker variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);

        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));

        // Initialize Map
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapController = mapView.getController();
        mapController.setZoom(16);
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(scaleBarOverlay);

        // Back button functionality
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Reference to the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor-data");

        // Fetch data
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child("Latitude").getValue(Double.class);
                Double longitude = snapshot.child("Longitude").getValue(Double.class);

                if (latitude != null && longitude != null) {
                    // Car location
                    GeoPoint carLocation = new GeoPoint(latitude, longitude);
                    mapController.setCenter(carLocation);

                    // Check if the carMarker already exists
                    if (carMarker == null) {
                        // If it doesn't exist, create a new marker
                        carMarker = new Marker(mapView);
                        carMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapView.getOverlays().add(carMarker); // Add marker to map
                    }

                    // Update marker's position
                    carMarker.setPosition(carLocation);
                } else {
                    Toast.makeText(LiveTrackingActivity.this, "Invalid GPS data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LiveTrackingActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
