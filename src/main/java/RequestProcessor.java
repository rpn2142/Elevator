import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by pramraj on 4/4/18.
 */
public class RequestProcessor extends Thread {

    private static final Integer SHUTDOWN_CODE = -1;
    private BlockingQueue<Integer> requestQueue;
    private ElevatorController elevatorController;

    public RequestProcessor(ElevatorController elevatorController) {
        this.requestQueue = new ArrayBlockingQueue<Integer>(1000);
        this.elevatorController = elevatorController;
    }

    @Override
    public void run() {

        Integer floor = null;
        try {
            while( (floor = requestQueue.take()) != null) {
                if( floor.equals(SHUTDOWN_CODE) )
                    break;
                else
                    elevatorController.gotoFloor(floor);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void addRequest(Integer floor) {
        requestQueue.add(floor);
    }

    public void shutdown() {
        addRequest(SHUTDOWN_CODE);
    }
}
