package api;

import model.ElevatorRequest;

/**
 * Created by pramraj on 4/11/18.
 */
public interface Elevator {
    boolean gotoFloor(Integer floor);
    boolean gotoFloor(ElevatorRequest elevatorRequest);
    void shutdown();
    void start();
    boolean isMovingInDirection(ElevatorRequest.Direction direction);
}
