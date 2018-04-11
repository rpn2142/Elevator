package main;

import api.ElevatorDriverController;
import model.ElevatorRequest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorService {

    private Elevator elevator;
    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;


    public ElevatorService(ElevatorDriverController elevatorDriverController) {
        this.elevatorRequestQueue = new ArrayBlockingQueue<ElevatorRequest>(1000);
        elevator = new Elevator(elevatorDriverController, elevatorRequestQueue);
        elevator.start();
    }

    public void requestElevator(ElevatorRequest elevatorRequest) {
        try {
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
