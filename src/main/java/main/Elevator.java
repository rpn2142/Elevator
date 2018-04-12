package main;

import api.ElevatorAvailableCallback;
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
import static model.ElevatorState.getDefaultElevatorState;
import static model.ElevatorState.getElevatorState;

/**
 * Created by pramraj on 4/4/18.
 */
public class Elevator extends Thread implements ElevatorUserControl {

    ElevatorAvailableCallback EMPTY_CALLBACK = new ElevatorAvailableCallback() {
        public void run(ElevatorUserControl elevator) {

        }
    };

    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;
    private BlockingQueue<ElevatorRequest> serviceQueue;
    private ElevatorDriverController elevatorDriverController;
    private ElevatorState currentElevatorState;

    public Elevator(ElevatorDriverController elevatorDriverController, BlockingQueue<ElevatorRequest> elevatorRequestQueue) {
        this.elevatorRequestQueue = elevatorRequestQueue;
        this.serviceQueue = new ArrayBlockingQueue<ElevatorRequest>(QUEUE_CAPACITY);
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
        gotoFloors();
    }

    private void gotoFloors() throws InterruptedException {

        ElevatorRequest destinationFloor = serviceQueue.poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if( destinationFloor == null )
            return;

        SortedSet<ElevatorRequest> floors = new TreeSet<ElevatorRequest>();
        floors.add(destinationFloor);

        while( ! floors.isEmpty() ) {
            grabAllGoToRequests(floors);
            ElevatorRequest elevatorRequest = pollFirst(floors);
            elevatorDriverController.gotoFloor(elevatorRequest.getFloor());
            elevatorRequest.getElevatorAvailableCallback().run(this);
        }
    }

    private ElevatorRequest pollFirst(SortedSet<ElevatorRequest> floors) {
        ElevatorRequest floor = floors.first();
        floors.remove(floor);
        return floor;
    }


    private void grabAllGoToRequests(SortedSet<ElevatorRequest> floors) throws InterruptedException {
        while( ! serviceQueue.isEmpty() )
            floors.add(serviceQueue.poll(1l, TimeUnit.MILLISECONDS));
    }

    public boolean gotoFloor(Integer floor){
        return gotoFloor(new ElevatorRequest(floor, null, EMPTY_CALLBACK));
    }

    public boolean gotoFloor(ElevatorRequest elevatorRequest) {
        if( currentElevatorState != null && ! currentElevatorState.isValidGotoFloor(elevatorRequest.getFloor()) )
            return false;

        serviceQueue.add(elevatorRequest);
        return true;
    }

    public void shutdown() {
        elevatorRequestQueue.add(new ElevatorRequest(SHUTDOWN_CODE, ElevatorRequest.Direction.UP, null));
    }

    public ElevatorState getCurrentElevatorState() {
        return currentElevatorState;
    }
}
