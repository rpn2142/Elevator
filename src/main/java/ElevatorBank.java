/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorBank {
    private ElevatorDriverController elevatorController;
    private Elevator requestProcessor;


    public ElevatorBank(ElevatorDriverController elevatorController) {
        this.elevatorController = elevatorController;
        requestProcessor = new Elevator(elevatorController);
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
