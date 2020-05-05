package com.camoli.findmycoso.models;

public class Device {
    private String uuid;
    private String name;
    private String email;
    private int id;
    private int ownerID;

    public  Device(){
        uuid = "error";
        name = "error";
        email = "error";
        id = -1;
        ownerID = -1;
    }

    public Device(String uuid, String name, int id, int ownerID, String email) {
        this.uuid = uuid;
        this.name = name;
        this.id = id;
        this.ownerID = ownerID;
        this.email = email;
    }

    public Device(String uuid, String name, int ownerID, String email) {
        this.uuid = uuid;
        this.name = name;
        this.id = -2;
        this.ownerID = ownerID;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public String getEmail() {
        return email;
    }
}
