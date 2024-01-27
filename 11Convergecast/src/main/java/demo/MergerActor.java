package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashSet;
import java.util.Set;



class HiMessage {
    final String message;

    HiMessage(String message) {
        this.message = message;
    }
}

class UnjoinMessage {
    final ActorRef sender;

    UnjoinMessage(ActorRef sender) {
        this.sender = sender;
    }
}

class SetTargetMessage {
    final ActorRef target;

    SetTargetMessage(ActorRef target) {
        this.target = target;
    }
}

public class MergerActor extends AbstractActor {
    private final Set<ActorRef> participants = new HashSet<>();
    private ActorRef target;
    private int hiCount = 0;

    static Props props() {
        return Props.create(MergerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SetTargetMessage.class, this::handleSetTarget)
                .match(JoinMessage.class, this::handleJoin)
                .match(HiMessage.class, this::handleHi)
                .match(UnjoinMessage.class, this::handleUnjoin)
                .build();
    }

    private void handleJoin(JoinMessage message) {
        participants.add(message.sender);
        System.out.println(message.sender.path().name() + " joined the convergence");
    }

    private void handleHi(HiMessage message) {
        hiCount++;
        System.out.println(getSender().path().name() + " sent: " + message.message);

        if (hiCount == participants.size()) {
            // All participants have sent their "hi" messages, notify the target
            System.out.println("All participants said hi");
            target.tell(new HiMessage("hi"), self());
            hiCount = 0;
        }
    }

    private void handleUnjoin(UnjoinMessage message) {
        participants.remove(message.sender);
        System.out.println(message.sender.path().name() + " left the convergence");

        if (participants.isEmpty()) {
            // All participants have left, reset the state
            hiCount = 0;
        }
    }

    private void handleSetTarget(SetTargetMessage message) {
        this.target = message.target;
    }
}

