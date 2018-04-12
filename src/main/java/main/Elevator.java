package main;

import api.ElevatorDriverController;
import api.ElevatorUserControl;
import model.ElevatorRequest;
import model.ElevatorState;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import static api.Config.*;
import static model.ElevatorState.getElevatorState;

/**
 * Created by pramraj on 4/4/18.
 */
public class Elevator extends Thread implements ElevatorUserControl {

    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;
    private BlockingQueue<Integer> serviceQueue;
    private ElevatorDriverController elevatorDriverController;
    private ElevatorState currentElevatorState;

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
        currentElevatorState = getElevatorState(elevatorRequest);
        elevatorRequest.getElevatorAvailableCallback().run(this);
        Integer destinationFloor = serviceQueue.poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        gotoFloors(destinationFloor);
    }

    private void gotoFloors(Integer destinationFloor) throws InterruptedException {

        if( destinationFloor == null )
            return;

        SortedSet<Integer> floors = new TreeSet<Integer>();
        floors.add(destinationFloor);

        while( ! floors.isEmpty() ) {
            grabAllGoToRequests(floors);
            elevatorDriverController.gotoFloor(pollFirst(floors));
        }
    }

    private Integer pollFirst(SortedSet<Integer> floors) {
        Integer floor = floors.first();
        floors.remove(floor);
        return floor;
    }


    private void grabAllGoToRequests(SortedSet<Integer> floors) throws InterruptedException {
        while( ! serviceQueue.isEmpty() )
            floors.add(serviceQueue.poll(1l, TimeUnit.MILLISECONDS));
    }

    public void gotoFloor(Integer floor){
        if( currentElevatorState != null && ! currentElevatorState.isValidGotoFloor(floor) )
            return;
        serviceQueue.add(floor);
    }

    public void shutdown() {
        elevatorRequestQueue.add(new ElevatorRequest(SHUTDOWN_CODE, ElevatorRequest.Direction.UP, null));
    }
}
