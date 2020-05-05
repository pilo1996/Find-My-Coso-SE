package com.camoli.findmycoso.api;

import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.Position;

public class PositionResponse {

    private String message;
    private Position position;
    private boolean error;

    public PositionResponse(String message, Position position, boolean error) {
        this.message = message;
        this.position = position;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isError() {
        return error;
    }
}
