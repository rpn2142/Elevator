package main;

import api.Elevator;
import api.ElevatorRequestQueueService;
import api.ElevatorService;
import model.ElevatorRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by pramraj on 4/4/18.
 */
@Singleton
public class ElevatorServiceImpl implements ElevatorService {

    private Elevator elevator;
    private ElevatorRequestQueueService elevatorRequestQueueService;

    @Inject
    public ElevatorServiceImpl(Elevator elevator, ElevatorRequestQueueService elevatorRequestQueueService) {
        this.elevator = elevator;
        this.elevatorRequestQueueService = elevatorRequestQueueService;
        elevator.start();
    }

    public void requestElevator(ElevatorRequest elevatorRequest) {
        try {
            if( isElevatorOnTheWay(elevatorRequest) ) {
                boolean successfullyAdded = elevator.gotoFloor(elevatorRequest);
                if( successfullyAdded )
                    return;
            }

            elevatorRequestQueueService.putRequest(elevatorRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isElevatorOnTheWay(ElevatorRequest elevatorRequest) {
        return elevator.isMovingInDirection(elevatorRequest.getDirection());
    }

    public void shutdown() {
        elevator.shutdown();
    }

}
