package com.example.win8.nokia.Pojo;

/**
 * Created by Hemanth Kumar B on 9/2/2018.
 */

public class CurrentLocation_pojo {
    double latitude, longitude;

    public CurrentLocation_pojo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
