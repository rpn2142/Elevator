package model;

import api.ElevatorAvailableCallback;

/**
 * Created by pramraj on 4/11/18.
 */
public class ElevatorRequest implements Comparable<ElevatorRequest> {

    public enum Direction {
        UP, DOWN, NONE
    };
    private Integer floor;
    private ElevatorAvailableCallback elevatorAvailableCallback;
    private Direction direction;

    public ElevatorRequest(Integer floor, Direction direction, ElevatorAvailableCallback elevatorAvailableCallback) {
        this.floor = floor;
        this.elevatorAvailableCallback = elevatorAvailableCallback;
        this.direction = direction;
    }

    public Integer getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public ElevatorAvailableCallback getElevatorAvailableCallback() {
        return elevatorAvailableCallback;
    }

    public int compareTo(ElevatorRequest elevatorRequest) {
        return this.getFloor().compareTo(elevatorRequest.getFloor());
    }
}
