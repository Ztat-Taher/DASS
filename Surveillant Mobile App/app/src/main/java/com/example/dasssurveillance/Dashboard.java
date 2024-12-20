package com.example.dasssurveillance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    // UI Elements
    private TextView totalViolationsTextView;
    private TextView lastViolationTextView;
    private FloatingActionButton fabLiveTracking;
    private TextView speedTextView;
    private com.google.android.material.progressindicator.CircularProgressIndicator circularSpeedIndicator;

    // Dummy data for now
    private int totalViolations = 5;
    private String lastViolation = "Red Light at Main St.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initializing UI elements
        totalViolationsTextView = findViewById(R.id.totalViolationsTextView);
        lastViolationTextView = findViewById(R.id.lastViolationTextView);
        fabLiveTracking = findViewById(R.id.fabLiveTracking);
        speedTextView = findViewById(R.id.speedTextView);
        circularSpeedIndicator = findViewById(R.id.circularSpeedIndicator);

        // Set the values dynamically
        updateDashboardData();

        // Handle Floating Action Button (Live Tracking)
        fabLiveTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Live Tracking (Start Tracking)
                openLiveTracking();
            }
        });
        FloatingActionButton fabHistory = findViewById(R.id.fabHistory);
        fabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);
        fabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // Reference to the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("sensor-data/Speed");

        // Fetch data
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer speed = snapshot.getValue(Integer.class);
                speedTextView.setText(speed + " km/h");
                circularSpeedIndicator.setProgress(speed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    // Method to update the dashboard with current data
    private void updateDashboardData() {
        totalViolationsTextView.setText("Total Violations: " + totalViolations);
        lastViolationTextView.setText("Last Violation: " + lastViolation);
    }

    // Method to handle the live tracking button click
    private void openLiveTracking() {
        // Logic to open the live tracking page or start tracking
        // For now, you can simply log this event or transition to another activity.
        // Example: Start a new activity for live tracking
        Intent intent = new Intent(Dashboard.this, LiveTrackingActivity.class);
        startActivity(intent);
    }
}
