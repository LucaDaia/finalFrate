package com.example.finalfrate;

    public class MyLatLong {
        public double latitude;
        public double longitude;
        public String description;

        public MyLatLong() {
            // Default constructor is required by Firebase
        }

        public MyLatLong(double latitude, double longitude, String description) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.description = description;
        }
    }

