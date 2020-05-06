package com.camoli.findmycoso.api;

public class UploadResponse {

    private boolean error;
    private String message;
    private String urlImg;

    public UploadResponse(boolean error, String message, String urlImg) {
        this.error = error;
        this.message = message;
        this.urlImg = urlImg;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getUrlImg(){
        return urlImg;
    }

}
