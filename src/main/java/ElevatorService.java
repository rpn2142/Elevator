/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorService {
    private ElevatorDriverController elevatorDriverController;
    private Elevator elevator;


    public ElevatorService(ElevatorDriverController elevatorDriverController) {
        this.elevatorDriverController = elevatorDriverController;
        elevator = new Elevator(elevatorDriverController);
        elevator.start();
    }

    public void requestService(Integer floor) {
        elevator.addRequest(floor);
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
