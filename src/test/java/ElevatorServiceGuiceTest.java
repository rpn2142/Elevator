import api.Elevator;
import api.ElevatorAvailableCallback;
import api.ElevatorService;
import model.ElevatorRequest;
import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(TestModule.class)
public class ElevatorServiceGuiceTest {

    @Inject
    ElevatorService elevatorService;

    @Test
    public void testIfEverythingGetsInjectedProperlyAndRuns() {
        elevatorService.requestElevator(new ElevatorRequest(1, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(Elevator elevator) {
                elevator.gotoFloor(5);
            }
        }));
    }
}
