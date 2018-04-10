import api.ElevatorDriverController;
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

       elevator.requestElevator(5);
       elevator.requestElevator(1);
       elevator.gotoFloor(5, 10);
       elevator.gotoFloor(1, 3);
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

        elevator.requestElevator(5);
        elevator.requestElevator(1);
        elevator.gotoFloor(1, 3);
        elevator.shutdown();
        context.assertIsSatisfied();
    }

//    @Test
//    public void testRequestServiceAndMultiFloorOptimization() throws InterruptedException {
//        Elevator elevator = new Elevator(elevatorController);
//        final Sequence callSequence = context.sequence("sequence-name");
//        context.checking(new Expectations() {{
//            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
//            oneOf(elevatorController).gotoFloor(6); inSequence(callSequence);
//            oneOf(elevatorController).gotoFloor(7); inSequence(callSequence);
//            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
//            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
//            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
//        }});
//
//        elevator.requestService(5);
//        elevator.gotoFloor(5, 6);
//        elevator.gotoFloor(5, 7);
//        elevator.gotoFloor(5, 10);
//        elevator.requestService(1);
//        elevator.gotoFloor(1, 3);
//        elevator.shutdown();
//        context.assertIsSatisfied();
//    }


    @Test
    public void testGoToService() {
        ElevatorService elevator = new ElevatorService(elevatorController);
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5);
        }});

        elevator.gotoFloor(1,5);
    }

}
