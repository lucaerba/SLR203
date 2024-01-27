package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

class GoMessage {}

class SetMergerMessage {
    final ActorRef merger;

    SetMergerMessage(ActorRef merger) {
        this.merger = merger;
    }
}
class JoinMessage {
    final ActorRef sender;

    JoinMessage(ActorRef sender) {
        this.sender = sender;
    }
}

public class ConvergecastActor extends AbstractActor {
    private String name;
    private ActorRef merger;

    ConvergecastActor(String name) {
        this.name = name;
    }

    static Props props(String name) {
        return Props.create(ConvergecastActor.class, name);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GoMessage.class, this::handleGo)
                .match(SetMergerMessage.class, this::handleSetMerger)
                .match(HiMessage.class, this::handleHi)
                .match(JoinMessage.class, this::handleJoin)
                .build();
    }

    private void handleGo(GoMessage message) {
        // Send a "hi" message to the target
        if (merger != null) {
            merger.tell(new HiMessage("hi"), self());
        }
    }

    private void handleSetMerger(SetMergerMessage message) {
        this.merger = message.merger;
    }

    private void handleHi(HiMessage message) {
        System.out.println(name + " received: " + message.message);
    }

    private void handleJoin(JoinMessage message) {
        this.merger = message.sender;
        getContext().system().scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                getSelf(),
                new GoMessage(),
                getContext().system().dispatcher(),
                ActorRef.noSender()
        );
        merger.tell(new JoinMessage(getSelf()), getSelf());
    }
}
