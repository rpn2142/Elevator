import api.ElevatorAvailableCallback;
import api.ElevatorDriverController;
import api.ElevatorForUser;
import api.ElevatorService;
import main.ElevatorServiceImpl;
import model.ElevatorRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by pramraj on 4/4/18.
 */
public class ElevatorTest {

    Mockery context = null;
    ElevatorDriverController elevatorController = null;


    @Before
    public void setUp() {
        context = new JUnit4Mockery() {{
            setThreadingPolicy(new Synchroniser());
        }};
        elevatorController = context.mock(ElevatorDriverController.class);

    }

    @Test
    public void testRequestService() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

       elevatorService.requestElevator(getElevatorRequest(5, 10));
       elevatorService.requestElevator(getElevatorRequest(1, 3));
       elevatorService.shutdown();
       context.assertIsSatisfied();
    }

    @Test
    public void testRequestServiceButNoAction() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

        elevatorService.requestElevator(getElevatorRequest(5, null));
        elevatorService.requestElevator(getElevatorRequest(1, 3));
        elevatorService.shutdown();
        context.assertIsSatisfied();
    }

    @Test
    public void testGotoMultipleFloorsInSingleRequest() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(6); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

        elevatorService.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorForUser elevator) {
                elevator.gotoFloor(6);
                elevator.gotoFloor(7);
                elevator.gotoFloor(10);
            }
        }));
        elevatorService.requestElevator(getElevatorRequest(1,3));
        elevatorService.shutdown();
        context.assertIsSatisfied();
    }

    @Test
    public void testGotoMultipleFloorsWithWrongValue() throws InterruptedException {
        ElevatorService elevatorService = new ElevatorServiceImpl(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(6); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
        }});
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
        context.assertIsSatisfied();
    }


    @Test
    public void testOptimizationForRequestsAlongTheWay() throws InterruptedException {
        final ElevatorService elevatorService = new ElevatorServiceImpl(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(9); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
        }});
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
        context.assertIsSatisfied();
    }


    //@Test
    public void testGoToService() {
        ElevatorService elevator = new ElevatorServiceImpl(elevatorController);
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5);
        }});

        //elevator.gotoFloor(1,5);
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
