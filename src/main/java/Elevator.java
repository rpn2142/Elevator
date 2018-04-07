import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by pramraj on 4/4/18.
 */
public class Elevator {
    private ElevatorController elevatorController;
    private RequestProcessor requestProcessor;


    public Elevator(ElevatorController elevatorController) {
        this.elevatorController = elevatorController;
        requestProcessor = new RequestProcessor(elevatorController);
        requestProcessor.start();
    }

    public void requestService(Integer floor) {
        requestProcessor.addRequest(floor);
    }

    public void gotoFloor(Integer fromFloor, Integer toFloor) {
        requestProcessor.gotoFloor(fromFloor, toFloor);
    }

    public void shutdown() {
        requestProcessor.shutdown();
        waitForShutdown();
    }

    private void waitForShutdown() {
        try {
            requestProcessor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
