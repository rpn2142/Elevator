package api;

import model.ElevatorRequest;

public interface ElevatorRequestQueueService {

    void putRequest(ElevatorRequest elevatorRequest) throws InterruptedException;
    ElevatorRequest takeRequest() throws InterruptedException;

}
