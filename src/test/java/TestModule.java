import api.Elevator;
import api.ElevatorDriverController;
import api.ElevatorRequestQueueService;
import api.ElevatorService;
import com.google.inject.AbstractModule;
import main.ElevatorImpl;
import main.ElevatorRequestQueueServiceImpl;
import main.ElevatorServiceImpl;

public class TestModule extends AbstractModule {

    protected void configure() {
        bind(Elevator.class).to(ElevatorImpl.class);
        bind(ElevatorRequestQueueService.class).to(ElevatorRequestQueueServiceImpl.class);
        bind(ElevatorService.class).to(ElevatorServiceImpl.class);
        bind(ElevatorDriverController.class).to(NoOpElevatorDriverController.class);
    }
}
