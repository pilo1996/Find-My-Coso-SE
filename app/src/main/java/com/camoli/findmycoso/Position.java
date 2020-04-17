package com.camoli.findmycoso;

public class Position {

    private String latitude, longitude, dayTime, id, uuid;

    public Position(String latitude, String longitude, String dayTime, String id, String uuid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
        this.id = id;
        this.uuid = uuid;
    }

    public Position() {
        this.latitude = "";
        this.longitude = "";
        this.dayTime = "";
        this.id = "";
        this.uuid = "";
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
