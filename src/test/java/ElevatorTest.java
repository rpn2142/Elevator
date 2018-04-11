import api.ElevatorAvailableCallback;
import api.ElevatorDriverController;
import api.ElevatorUserControl;
import main.ElevatorService;
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
        ElevatorService elevator = new ElevatorService(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

       elevator.requestElevator(getElevatorRequest(5, 10));
       elevator.requestElevator(getElevatorRequest(1, 3));
       elevator.shutdown();
       context.assertIsSatisfied();
    }

    @Test
    public void testRequestServiceButNoAction() throws InterruptedException {
        ElevatorService elevator = new ElevatorService(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

        elevator.requestElevator(getElevatorRequest(5, null));
        elevator.requestElevator(getElevatorRequest(1, 3));
        elevator.shutdown();
        context.assertIsSatisfied();
    }

    @Test
    public void testGotoMultipleFloorsInSingleRequest() throws InterruptedException {
        ElevatorService elevator = new ElevatorService(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(6); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

        elevator.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorUserControl elevator) {
                elevator.gotoFloor(6);
                elevator.gotoFloor(7);
                elevator.gotoFloor(10);
            }
        }));
        elevator.requestElevator(getElevatorRequest(1,3));
        elevator.shutdown();
        context.assertIsSatisfied();
    }

    @Test
    public void testGotoMultipleFloorsWithWrongValue() throws InterruptedException {
        ElevatorService elevator = new ElevatorService(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(6); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
        //    oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
        //    oneOf(elevatorController).gotoFloor(9); inSequence(callSequence);
        }});
        elevator.requestElevator(new ElevatorRequest(5, ElevatorRequest.Direction.UP, new ElevatorAvailableCallback() {
            public void run(ElevatorUserControl elevator) {
                elevator.gotoFloor(6);
                elevator.gotoFloor(7);
                elevator.gotoFloor(1);
            }
        }));


        elevator.shutdown();
        context.assertIsSatisfied();
    }


    //@Test
    public void testGoToService() {
        ElevatorService elevator = new ElevatorService(elevatorController);
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5);
        }});

        //elevator.gotoFloor(1,5);
    }

    private ElevatorRequest getElevatorRequest(Integer fromFloor, final Integer toFloor) {

        ElevatorRequest.Direction direction = (toFloor != null && toFloor > fromFloor) ? ElevatorRequest.Direction.UP : ElevatorRequest.Direction.DOWN;
        return new ElevatorRequest(fromFloor, direction, new ElevatorAvailableCallback() {
            public void run(ElevatorUserControl elevatorForUser) {
                if( toFloor != null )
                elevatorForUser.gotoFloor(toFloor);
            }
        });
    }

}
