package model;

import api.ElevatorAvailableCallback;

/**
 * Created by pramraj on 4/11/18.
 */
public class ElevatorRequest {
    private Integer floor;
    private ElevatorAvailableCallback elevatorAvailableCallback;

    public ElevatorRequest(Integer floor, ElevatorAvailableCallback elevatorAvailableCallback) {
        this.floor = floor;
        this.elevatorAvailableCallback = elevatorAvailableCallback;
    }

    public Integer getFloor() {
        return floor;
    }

    public ElevatorAvailableCallback getElevatorAvailableCallback() {
        return elevatorAvailableCallback;
    }
}
