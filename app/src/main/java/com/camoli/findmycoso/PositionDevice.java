package com.camoli.findmycoso;

import com.google.android.gms.maps.model.LatLng;

public class PositionDevice extends Device {

    private String Latitude, Longitude, daytime;

    public PositionDevice() {
        super();
        Latitude = "";
        Longitude = "";
        daytime = "";
    }

    public PositionDevice(Device thisOne, LatLng latLng, String daytime){
        new PositionDevice(thisOne.getUuid(), thisOne.getName(), thisOne.getId(), thisOne.getuserEmail(), latLng, daytime);
    }

    public PositionDevice(String uuid, String name, String id, String userEmail, LatLng latLng, String daytime) {
        super(uuid, name, id, userEmail);
        Latitude = latLng.latitude+"";
        Longitude = latLng.longitude+"";
        this.daytime = daytime;
    }

    public void setLatLng(LatLng latLng) {
        Latitude = latLng.latitude+"";
        Longitude = latLng.longitude+"";
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }
}
