package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.List;

public class ActorNode extends UntypedAbstractActor {

    private List<ActorRef> knownActors;

    public static Props createActor() {
        return Props.create(ActorNode.class, ActorNode::new);
    }

    public static class InitMessage {
        private final List<ActorRef> knownActors;

        public InitMessage(List<ActorRef> knownActors) {
            this.knownActors = knownActors;
        }

        public List<ActorRef> getKnownActors() {
            return knownActors;
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof InitMessage) {
            // Initialize known actors
            this.knownActors = ((InitMessage) message).getKnownActors();
            System.out.println(getSelf().path().name() + " knows: " + knownActors);
        } else if ("start-flood".equals(message)) {
            // Start the flooding algorithm
            System.out.println(getSelf().path().name() + " received: " + message);
            flood("message m");
        } else if (message instanceof String) {
            // Handle other messages (for the flooding algorithm)
            System.out.println(getSelf().path().name() + " received: " + message);

            // Forward the message to known actors
            for (ActorRef knownActor : knownActors) {
                knownActor.tell(message, self());
            }
        }
    }

    private void flood(String message) {
        // Start the flooding algorithm by sending a message to known actors
        for (ActorRef knownActor : knownActors) {
            knownActor.tell(message, self());
        }
    }
}

