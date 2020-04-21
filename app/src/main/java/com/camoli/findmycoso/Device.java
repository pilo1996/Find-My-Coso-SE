package com.camoli.findmycoso;

public class Device {
    private String uuid;
    private String name;
    private String id;
    private String userEmail;
    private String ownerID;

    public  Device(){
        uuid = "error";
        name = "error";
        id = "error";
        userEmail = "error";
        ownerID = "error";
    }

    public Device(String uuid, String name, String id, String userEmail, String ownerID) {
        this.uuid = uuid;
        this.name = name;
        this.id = id;
        this.userEmail = userEmail;
        this.ownerID = ownerID;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getuserEmail(){
        return userEmail;
    }

    public String getOwnerID() {
        return ownerID;
    }
}
