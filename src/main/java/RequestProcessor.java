import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by pramraj on 4/4/18.
 */
public class RequestProcessor extends Thread {

    private static final Integer SHUTDOWN_CODE = -1;
    private static final Long REQUEST_TIMEOUT_MS = 100l;

    private BlockingQueue<Integer> requestQueue;
    private BlockingQueue<Integer> serviceQueue;
    private ElevatorController elevatorController;

    public RequestProcessor(ElevatorController elevatorController) {
        this.requestQueue = new ArrayBlockingQueue<Integer>(1000);
        this.serviceQueue = new ArrayBlockingQueue<Integer>(1000);
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
                    provideService(floor);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void provideService(Integer floor) throws InterruptedException {
        elevatorController.gotoFloor(floor);
        Integer destinationFloor = serviceQueue.poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if( destinationFloor != null )
            elevatorController.gotoFloor(destinationFloor);
    }


    public void addRequest(Integer floor) {
        requestQueue.add(floor);
    }

    public void gotoFloor(Integer floor){
        serviceQueue.add(floor);
    }

    public void shutdown() {
        addRequest(SHUTDOWN_CODE);
    }
}
