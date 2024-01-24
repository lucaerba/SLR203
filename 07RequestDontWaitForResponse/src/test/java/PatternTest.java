
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import demo.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

public class PatternTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testTellToAndForgetPattern() {

        // Create actors
        final ActorRef a = system.actorOf(ReceiverActor.createActor(), "a");
        final ActorRef transmitter = system.actorOf(TransmitterActor.createActor(), "transmitter");
        final ActorRef b = system.actorOf(ReceiverActor.createActor(), "b");
        Message m1 = new Message("",  null, a);
        m1.setRef1(transmitter);
        m1.setRef2(b);
        // Send messages
        a.tell(m1, ActorRef.noSender());
        a.tell(new Message("start", null, a), ActorRef.noSender());

    }
}