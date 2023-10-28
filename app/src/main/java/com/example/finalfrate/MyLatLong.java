package com.example.finalfrate;

    public class MyLatLong {
        public double latitude;
        public double longitude;

        public MyLatLong() {
            // Default constructor is required by Firebase
        }

        public MyLatLong(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

