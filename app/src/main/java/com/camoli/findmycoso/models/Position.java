package com.camoli.findmycoso.models;

public class Position {

    private int positionID, deviceID, userID;
    private String latitude, longitude, dayTime, address, dateTime;

    public Position(int positionID, int deviceID, int userID, String address, String latitude, String longitude, String dayTime, String dateTime) {
        this.deviceID = positionID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.deviceID = deviceID;
        this.userID = userID;
        this.address = address;
        this.dateTime = dateTime;
    }

    public Position(int deviceID, int userID, String address, String latitude, String longitude, String dayTime, String dateTime) {
        this.deviceID = -2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.deviceID = deviceID;
        this.userID = userID;
        this.address = address;
        this.dateTime = dateTime;
    }

    public Position() {
        this.latitude = "";
        this.longitude = "";
        this.dayTime = "";
        this.deviceID = -1;
        this.userID = -1;
        this.address = "";
        this.dateTime = "";
    }

    public int getPositionID() {
        return positionID;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDayTime() {
        return dayTime;
    }

    public int getUserID() {
        return userID;
    }

    public String getAddress() {
        return address;
    }

    public String getDateTime() {
        return dateTime;
    }
}
