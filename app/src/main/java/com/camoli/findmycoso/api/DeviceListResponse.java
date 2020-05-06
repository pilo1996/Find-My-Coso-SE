package com.camoli.findmycoso.api;

import com.camoli.findmycoso.models.Device;

import java.util.List;

public class DeviceListResponse {

    private String message;
    private List<Device> devices;
    private boolean error;

    public DeviceListResponse(boolean error, String message, List<Device> devices) {
        this.message = message;
        this.devices = devices;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public List<Device> getDeviceList() {
        return devices;
    }

    public boolean isError() {
        return error;
    }
}
