package com.example.dasssurveillance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<String> historyList;
    private ArrayAdapter<String> adapter;
    private ArrayList<Violation> violationList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView historyListView = findViewById(R.id.historyListView);
        historyList = new ArrayList<>();
        violationList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        // Reference to the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("violations");

        // Fetch data
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear(); // Clear old data
                violationList.clear();

                for (DataSnapshot violationSnapshot : snapshot.getChildren()) {
                    Violation violation = violationSnapshot.getValue(Violation.class);
                    if (violation != null) {
                        String entry = "Type: " + violation.getType();
                        historyList.add(entry);
                        violationList.add(violation);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set item click listener
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation selectedViolation = violationList.get(position);
                Intent intent = new Intent(HistoryActivity.this, ViolationDetails.class);
                intent.putExtra("violation_type", selectedViolation.getType());
                intent.putExtra("violation_timestamp", selectedViolation.getDate());
                intent.putExtra("latitude", selectedViolation.getLatitude());
                intent.putExtra("longitude", selectedViolation.getLongitude());
                startActivity(intent);
            }
        });

        // Back button functionality
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}