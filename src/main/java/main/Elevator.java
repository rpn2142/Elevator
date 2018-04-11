package main;

import api.ElevatorDriverController;
import api.ElevatorUserControl;
import model.ElevatorRequest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import static api.Config.*;

/**
 * Created by pramraj on 4/4/18.
 */
public class Elevator extends Thread implements ElevatorUserControl {

    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;
    private BlockingQueue<Integer> serviceQueue;
    private ElevatorDriverController elevatorDriverController;

    public Elevator(ElevatorDriverController elevatorDriverController, BlockingQueue<ElevatorRequest> elevatorRequestQueue) {
        this.elevatorRequestQueue = elevatorRequestQueue;
        this.serviceQueue = new ArrayBlockingQueue<Integer>(QUEUE_CAPACITY);
        this.elevatorDriverController = elevatorDriverController;
    }

    @Override
    public void run() {

        ElevatorRequest elevatorRequest = null;
        try {
            while( (elevatorRequest = elevatorRequestQueue.take()) != null) {
                if( elevatorRequest.getFloor().equals(SHUTDOWN_CODE) )
                    break;
                else
                    provideService(elevatorRequest);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void provideService(ElevatorRequest elevatorRequest) throws InterruptedException {
        Integer floor = elevatorRequest.getFloor();
        elevatorDriverController.gotoFloor(floor);
        elevatorRequest.getElevatorAvailableCallback().run(this);
        Integer destinationFloor = serviceQueue.poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if( destinationFloor != null )
            elevatorDriverController.gotoFloor(destinationFloor);
    }

    public void gotoFloor(Integer floor){
        serviceQueue.add(floor);
    }

    public void shutdown() {
        elevatorRequestQueue.add(new ElevatorRequest(SHUTDOWN_CODE, null));
    }
}
