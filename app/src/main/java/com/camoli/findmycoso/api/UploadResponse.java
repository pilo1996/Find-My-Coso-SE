package com.camoli.findmycoso.api;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("url")
    private String url;

    public UploadResponse(boolean error, String message, String url) {
        this.error = error;
        this.message = message;
        this.url = url;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl(){
        return url;
    }

}
