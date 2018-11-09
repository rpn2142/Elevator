import api.ElevatorAvailableCallback;
import api.ElevatorDriverController;
import api.ElevatorForUser;
import api.ElevatorService;
import main.ElevatorServiceImpl;
import model.ElevatorRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

/**
 * Created by pramraj on 4/4/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ElevatorTest {

    @Mock
    ElevatorDriverController elevatorDriverController;

    @Before
    public void setUp() {
    }

    @Test
    public void testRequestService() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorDriverController);

        elevatorService.requestElevator(getElevatorRequest(5, 10));
        elevatorService.requestElevator(getElevatorRequest(1, 3));
        elevatorService.shutdown();

        InOrder inOrder = inOrder(elevatorDriverController);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(10);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(1);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(3);

    }

    @Test
    public void testRequestServiceButNoAction() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorDriverController);

        elevatorService.requestElevator(getElevatorRequest(5, null));
        elevatorService.requestElevator(getElevatorRequest(1, 3));
        elevatorService.shutdown();

        InOrder inOrder = inOrder(elevatorDriverController);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(1);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(3);
    }

    @Test
    public void testGotoMultipleFloorsInSingleRequest() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorDriverController);

        elevatorService.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                elevator.gotoFloor(6);
                elevator.gotoFloor(7);
                elevator.gotoFloor(10);
            }
        }));
        elevatorService.requestElevator(getElevatorRequest(1,3));
        elevatorService.shutdown();

        InOrder inOrder = inOrder(elevatorDriverController);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(6);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(7);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(10);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(1);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(3);

    }

    @Test
    public void testGotoMultipleFloorsWithWrongValue() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorDriverController);

        elevatorService.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                elevator.gotoFloor(7);
                elevator.gotoFloor(6);
                elevator.gotoFloor(1);
            }
        }));

        elevatorService.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.DOWN, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                elevator.gotoFloor(7);
                elevator.gotoFloor(6);
                elevator.gotoFloor(1);
            }
        }));
        elevatorService.shutdown();

        InOrder inOrder = inOrder(elevatorDriverController);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(6);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(7);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(1);

    }

    @Test
    public void testOptimizationForRequestsAlongTheWay() throws InterruptedException {
        final ElevatorService elevatorService = new ElevatorServiceImpl(elevatorDriverController);

        elevatorService.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                try {
                    Thread.sleep(30l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                elevator.gotoFloor(10);
            }
        }));
        Thread.sleep(20l);
        elevatorService.requestElevator(new ElevatorRequest(7, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                elevator.gotoFloor(9);
            }
        }));
        Thread.sleep(60l);
        elevatorService.shutdown();
        InOrder inOrder = inOrder(elevatorDriverController);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(5);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(7);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(9);
        inOrder.verify(elevatorDriverController, times(1)).gotoFloor(10);
    }

    private ElevatorRequest getElevatorRequest(Integer fromFloor, final Integer toFloor) {

        ElevatorRequest.Direction direction = (toFloor != null && toFloor > fromFloor) ? ElevatorRequest.Direction.UP : ElevatorRequest.Direction.DOWN;
        return new ElevatorRequest(fromFloor, direction, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevatorForUser) {
                if( toFloor != null )
                elevatorForUser.gotoFloor(toFloor);
            }
        });
    }

}
