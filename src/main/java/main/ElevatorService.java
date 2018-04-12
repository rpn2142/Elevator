package main;

import api.ElevatorDriverController;
import model.ElevatorRequest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static api.Config.QUEUE_CAPACITY;

/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorService {

    private Elevator elevator;
    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;


    public ElevatorService(ElevatorDriverController elevatorDriverController) {
        this.elevatorRequestQueue = new ArrayBlockingQueue<ElevatorRequest>(QUEUE_CAPACITY);
        elevator = new Elevator(elevatorDriverController, elevatorRequestQueue);
        elevator.start();
    }

    public void requestElevator(ElevatorRequest elevatorRequest) {
        try {
            if( elevator.isMovingInDirection(elevatorRequest.getDirection()) ) {
                boolean successfullyAdded = elevator.gotoFloor(elevatorRequest);
                if( successfullyAdded )
                    return;
            }

            elevatorRequestQueue.put(elevatorRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        elevator.shutdown();
        waitForShutdown();
    }

    private void waitForShutdown() {
        try {
            elevator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
