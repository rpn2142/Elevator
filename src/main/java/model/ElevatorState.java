package model;

/**
 * Created by pramraj on 4/12/18.
 */
public class ElevatorState {
    private Integer currentFloor;
    private ElevatorRequest.Direction direction;

    private ElevatorState(Integer currentFloor, ElevatorRequest.Direction direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;
    }

    public Integer getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorRequest.Direction getDirection() {
        return direction;
    }

    public static ElevatorState getElevatorState(ElevatorRequest elevatorRequest) {
        return new ElevatorState(elevatorRequest.getFloor(), elevatorRequest.getDirection());
    }

    public boolean isValidGotoFloor(Integer floor) {
        if( floor.equals(currentFloor) )
            return false;
        else if( direction.equals(ElevatorRequest.Direction.UP) && floor < currentFloor )
            return false;
        else if( direction.equals(ElevatorRequest.Direction.DOWN) && floor > currentFloor )
            return false;

        return true;
    }
}
