package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

public class Actor extends UntypedAbstractActor{
    private final ActorRef broadcaster;

    public Actor(ActorRef broadcaster) {
        this.broadcaster = broadcaster;
    }

    public static Props createActor(ActorRef broadcaster) {
        return Props.create(Actor.class, () -> new Actor(broadcaster));
    }

    @Override
    public void preStart() {
        if (getSelf().path().name().equals("a")) {
            getContext().system().scheduler().scheduleOnce(
                    Duration.create(1000, TimeUnit.MILLISECONDS),
                    getSelf(),
                    "go",
                    getContext().system().dispatcher(),
                    ActorRef.noSender()
            );
        }else{
            joinBroadcaster();
        }
    }

    @Override
    public void onReceive(Object message) {
        if ("go".equals(message)) {
            broadcaster.tell("m", getSelf());
        }else {
            processMessage(String.valueOf(message));
        }
    }

    public void joinBroadcaster() {
        // Join the broadcaster when the actor is started
        broadcaster.tell("join", self());
    }

    public void processMessage(String message) {
        // Handle 'm' message as needed
        System.out.println(self().path().name() + " received message: " + message);
    }
}

