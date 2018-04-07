import junit.framework.TestCase;
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
    ElevatorController elevatorController = null;


    @Before
    public void setUp() {
        context = new JUnit4Mockery() {{
            setThreadingPolicy(new Synchroniser());
        }};
        elevatorController = context.mock(ElevatorController.class);

    }



    @Test
    public void testRequestService() throws InterruptedException {
        Elevator elevator = new Elevator(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(10); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

       elevator.requestService(5);
       elevator.requestService(1);
       elevator.gotoFloor(10);
       elevator.gotoFloor(3);
       elevator.shutdown();
       context.assertIsSatisfied();
    }

    @Test
    public void testRequestServiceButNoAction() throws InterruptedException {
        Elevator elevator = new Elevator(elevatorController);
        final Sequence callSequence = context.sequence("sequence-name");
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(1); inSequence(callSequence);
            oneOf(elevatorController).gotoFloor(3); inSequence(callSequence);
        }});

        elevator.requestService(5);
        Thread.sleep(120);
        elevator.requestService(1);
        elevator.gotoFloor(3);
        elevator.shutdown();
        context.assertIsSatisfied();
    }

    @Test
    public void testGoToService() {
        Elevator elevator = new Elevator(elevatorController);
        context.checking(new Expectations() {{
            oneOf(elevatorController).gotoFloor(5);
        }});

        elevator.gotoFloor(5);
    }

}
