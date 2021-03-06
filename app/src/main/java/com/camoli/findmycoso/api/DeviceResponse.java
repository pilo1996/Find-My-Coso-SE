package com.camoli.findmycoso.api;

import com.camoli.findmycoso.models.Device;

public class DeviceResponse {

    private String message;
    private Device device;
    private boolean error;

    public DeviceResponse(boolean error, String message, Device device) {
        this.message = message;
        this.device = device;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public Device getDevice() {
        return device;
    }

    public boolean isError() {
        return error;
    }
}
