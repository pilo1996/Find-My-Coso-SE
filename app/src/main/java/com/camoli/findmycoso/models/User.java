package com.camoli.findmycoso.models;

public class User {

    private int userID;
    private String nome;
    private String email;
    private String plainPassword;
    private String profile_pic;
    private int isValidated;
    private int selectedDeviceID;

    public User(int userID, String nome, String email, String plainPassword, String profile_pic, int isValidated, int selectedDeviceID) {
        this.userID = userID;
        this.nome = nome;
        this.email = email;
        this.plainPassword = plainPassword;
        this.profile_pic = profile_pic;
        this.isValidated = isValidated;
        this.selectedDeviceID = selectedDeviceID;
    }

    public User(int userID,String nome, String email, String plainPassword) {
        this.userID = userID;
        this.nome = nome;
        this.email = email;
        this.plainPassword = plainPassword;
    }

    public User(int userID) {
        this.userID = userID;
        this.nome = "error";
        this.email = "error";
        this.plainPassword = "error";
        this.profile_pic = "error";
        this.isValidated = 0;
        this.selectedDeviceID = -1;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public int getValidated() {
        return isValidated;
    }

    public void setValidated(int validated) {
        isValidated = validated;
    }

    public int getSelectedDeviceID() {
        return selectedDeviceID;
    }

    public void setSelectedDeviceID(int selectedDeviceID) {
        this.selectedDeviceID = selectedDeviceID;
    }
}
