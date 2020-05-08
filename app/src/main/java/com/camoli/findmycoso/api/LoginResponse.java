package com.camoli.findmycoso.api;

import com.camoli.findmycoso.models.User;

public class LoginResponse {

    private boolean error;
    private String message;
    private User user;

    public LoginResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public LoginResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
        this.user = new User(-1);
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
