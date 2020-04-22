package com.camoli.findmycoso;

public class Position {

    private String latitude, longitude, dayTime, id, uuid, userID, address, dateTime;

    public Position(String latitude, String longitude, String dayTime, String id, String uuid, String userID, String address, String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.id = id;
        this.uuid = uuid;
        this.userID = userID;
        this.address = address;
        this.dateTime = dateTime;
    }

    public Position() {
        this.latitude = "";
        this.longitude = "";
        this.dayTime = "";
        this.id = "";
        this.uuid = "";
        this.userID = "";
        this.address = "";
        this.dateTime = "";
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

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserID() {
        return userID;
    }

    public String getAddress() {
        return address;
    }

    public String getDateTime() {
        return dateTime;
    }
}
