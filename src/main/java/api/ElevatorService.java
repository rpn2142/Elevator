package api;

import model.ElevatorRequest;

public interface ElevatorService {
    void requestElevator(ElevatorRequest elevatorRequest);
    void shutdown();
}
