package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

public class TellToAndForgetPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("system");

        final ActorRef main = system.actorOf(ReceiverActor.createActor(), "main");
        final ActorRef a = system.actorOf(ReceiverActor.createActor(), "a");
        final ActorRef transmitter = system.actorOf(TransmitterActor.createActor(), "transmitter");
        final ActorRef b = system.actorOf(ReceiverActor.createActor(), "b");
        Message m1 = new Message("",  null, a);
        m1.setRef1(transmitter);
        m1.setRef2(b);
        // Send messages
        a.tell(m1, ActorRef.noSender());
        a.tell(new Message("start", null, a), ActorRef.noSender());

        // Wait for a while to allow messages to be processed
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }
}



