import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by pramraj on 4/4/18.
 */
public class RequestProcessor extends Thread {

    private static final Integer SHUTDOWN_CODE = -1;
    private static final Long REQUEST_TIMEOUT_MS = 100l;

    private BlockingQueue<Integer> requestQueue;
    private Map<Integer, BlockingQueue<Integer>> serviceQueues;
    private ElevatorController elevatorController;

    public RequestProcessor(ElevatorController elevatorController) {
        this.requestQueue = new ArrayBlockingQueue<Integer>(1000);
        this.serviceQueues = new ConcurrentHashMap<Integer, BlockingQueue<Integer>>();
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
        initQueueIfNecessary(floor);
        Integer destinationFloor = serviceQueues.get(floor).poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if( destinationFloor != null )
            elevatorController.gotoFloor(destinationFloor);
    }


    public void addRequest(Integer floor) {
        requestQueue.add(floor);
    }

    public void gotoFloor(Integer fromFloor, Integer toFloor){
        initQueueIfNecessary(fromFloor);
        serviceQueues.get(fromFloor).add(toFloor);
    }

    private void initQueueIfNecessary(Integer fromFloor) {
        if( ! serviceQueues.containsKey(fromFloor) )
            serviceQueues.put(fromFloor, new ArrayBlockingQueue<Integer>(1000));
    }

    public void shutdown() {
        addRequest(SHUTDOWN_CODE);
    }
}
