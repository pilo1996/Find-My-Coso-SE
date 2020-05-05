package com.camoli.findmycoso.models;

public class Position {

    private int positionID, deviceID, userID;
    private String latitude, longitude, dayTime, uuid, address, dateTime;

    public Position(int positionID, String latitude, String longitude, String dayTime, int deviceID, String uuid, int userID, String address, String dateTime) {
        this.deviceID = positionID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.deviceID = deviceID;
        this.uuid = uuid;
        this.userID = userID;
        this.address = address;
        this.dateTime = dateTime;
    }

    public Position(String latitude, String longitude, String dayTime, int deviceID, String uuid, int userID, String address, String dateTime) {
        this.deviceID = -2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.deviceID = deviceID;
        this.uuid = uuid;
        this.userID = userID;
        this.address = address;
        this.dateTime = dateTime;
    }

    public Position() {
        this.latitude = "";
        this.longitude = "";
        this.dayTime = "";
        this.deviceID = -1;
        this.uuid = "";
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

    public String getUuid() {
        return uuid;
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
