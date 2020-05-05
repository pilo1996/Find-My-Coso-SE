package com.camoli.findmycoso.api;

import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.Position;

import java.util.List;

public class PositionListResponse {

    private String message;
    private List<Position> positions;
    private boolean error;

    public PositionListResponse(String message, List<Position> positions, boolean error) {
        this.message = message;
        this.positions = positions;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public List<Position> getPositionList() {
        return positions;
    }

    public boolean isError() {
        return error;
    }
}
