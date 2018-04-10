import api.ElevatorDriverController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorService {
    private ElevatorDriverController elevatorDriverController;
    private Elevator elevator;
    private BlockingQueue<Integer> elevatorRequestQueue;


    public ElevatorService(ElevatorDriverController elevatorDriverController) {
        this.elevatorRequestQueue = new ArrayBlockingQueue<Integer>(1000);
        this.elevatorDriverController = elevatorDriverController;
        elevator = new Elevator(elevatorDriverController, elevatorRequestQueue);
        elevator.start();
    }

    public void requestElevator(Integer floor) {
        try {
            elevatorRequestQueue.put(floor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gotoFloor(Integer fromFloor, Integer toFloor) {
        elevator.gotoFloor(fromFloor, toFloor);
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
