package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class GoMessage {}

class SetTargetMessage {
    final ActorRef target;

    SetTargetMessage(ActorRef target) {
        this.target = target;
    }
}

public class ConvergecastActor extends AbstractActor {
    private String name;
    private ActorRef target;

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
                .match(SetTargetMessage.class, this::handleSetTarget)
                .match(HiMessage.class, this::handleHi)
                .build();
    }

    private void handleGo(GoMessage message) {
        // Send a "hi" message to the target
        if (target != null) {
            target.tell(new HiMessage(name + " says hi!"), self());
        }
    }

    private void handleSetTarget(SetTargetMessage message) {
        this.target = message.target;
    }

    private void handleHi(HiMessage message) {
        System.out.println(name + " received: " + message.message);
    }
}
