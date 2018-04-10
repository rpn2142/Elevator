import api.ElevatorDriverController;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static api.Config.*;

/**
 * Created by pramraj on 4/4/18.
 */
public class Elevator extends Thread {

    private BlockingQueue<Integer> elevatorRequestQueue;
    private Map<Integer, BlockingQueue<Integer>> serviceQueues;
    private ElevatorDriverController elevatorDriverController;

    public Elevator(ElevatorDriverController elevatorDriverController, BlockingQueue<Integer> elevatorRequestQueue) {
        this.elevatorRequestQueue = elevatorRequestQueue;
        this.serviceQueues = new ConcurrentHashMap<Integer, BlockingQueue<Integer>>();
        this.elevatorDriverController = elevatorDriverController;
    }

    @Override
    public void run() {

        Integer floor = null;
        try {
            while( (floor = elevatorRequestQueue.take()) != null) {
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
        elevatorDriverController.gotoFloor(floor);
        initQueueIfNecessary(floor);
        Integer destinationFloor = serviceQueues.get(floor).poll(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if( destinationFloor != null )
            elevatorDriverController.gotoFloor(destinationFloor);
    }


    private void addRequest(Integer floor) {
        elevatorRequestQueue.add(floor);
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
