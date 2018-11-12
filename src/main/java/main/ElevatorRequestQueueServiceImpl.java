package main;

import api.ElevatorRequestQueueService;
import model.ElevatorRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static api.Config.QUEUE_CAPACITY;

@Singleton
public class ElevatorRequestQueueServiceImpl implements ElevatorRequestQueueService {

    private BlockingQueue<ElevatorRequest> elevatorRequestQueue;

    @Inject
    public ElevatorRequestQueueServiceImpl() {
        this.elevatorRequestQueue = new ArrayBlockingQueue<ElevatorRequest>(QUEUE_CAPACITY);
    }
    public void putRequest(ElevatorRequest elevatorRequest) throws InterruptedException {
        elevatorRequestQueue.put(elevatorRequest);
    }

    public ElevatorRequest takeRequest() throws InterruptedException {
        return elevatorRequestQueue.take();
    }
}
