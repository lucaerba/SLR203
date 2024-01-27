package demo;

import akka.actor.*;

class SimpleActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }
}
public class ActorCreator extends AbstractActor {
    private int actorCounter = 1;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("CREATE", this::handleCreate)
                .match(ActorIdentity.class, this::handleActorIdentity)
                .build();
    }

    private void handleCreate(String message) {
        // Create an actor with name "actorX"
        ActorRef newActor = getContext().actorOf(Props.create(SimpleActor.class), "actor" + actorCounter);

        // Print the path of the newly created actor
        System.out.println("New Actor Path: " + newActor.path());

        // Search for the actors
        actorCounter++;
    }

    private void handleActorIdentity(ActorIdentity identity) {
        if (identity.getActorRef().isPresent()) {
            ActorRef actorRef = identity.getActorRef().get();
            System.out.println("Actor found! Path: " + actorRef.path());
        } else {
            System.out.println("Actor not found for path: " + identity.correlationId());
        }
    }
}