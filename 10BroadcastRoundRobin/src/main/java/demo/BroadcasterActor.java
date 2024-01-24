package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.Vector;

public class BroadcasterActor extends UntypedAbstractActor {

    private final Vector<ActorRef> subscribers;

    public BroadcasterActor() {
        this.subscribers = new Vector<>();
    }

    public static Props createActor() {
        return Props.create(BroadcasterActor.class, BroadcasterActor::new);
    }

    @Override
    public void onReceive(Object message) {
        if ("join".equals(message)) {
            // Handle join message
            subscribers.add(getSender());
        } else if ("m".equals(message)) {
            // Send the 'm' message to all subscribed actors
            for (ActorRef subscriber : subscribers) {
                subscriber.tell(message, self());
            }
        }
    }
}
