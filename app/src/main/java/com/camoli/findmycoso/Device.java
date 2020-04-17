package com.camoli.findmycoso;

public class Device {
    private String uuid;
    private String name;
    private String id;
    private String userEmail;

    public  Device(){
        uuid = "error";
        name = "error";
        id = "error";
        userEmail = "error";
    }

    public Device(String uuid, String name, String id, String userEmail) {
        this.uuid = uuid;
        this.name = name;
        this.id = id;
        this.userEmail = userEmail;
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
}
