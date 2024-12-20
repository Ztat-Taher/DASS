package com.example.dasssurveillance;

public class Violation {
    private String type;
    private double latitude;
    private double longitude;
    private String date;

    public Violation() { } // Default constructor

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getDate() { return date; }
    public void setTimestamp(String timestamp) { this.date = timestamp; }
}
