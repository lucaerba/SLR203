package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.ArrayList;
import java.util.List;

public class ActorNode extends UntypedAbstractActor {

    private List<ActorRef> knownActors;
    private List<Integer> receivedSequenceNumbers;

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

    public static class ControlledFloodMessage {
        private final int sequenceNumber;

        public ControlledFloodMessage(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.receivedSequenceNumbers = new ArrayList<>();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof InitMessage) {
            // Initialize known actors
            this.knownActors = ((InitMessage) message).getKnownActors();
            System.out.println(getSelf().path().name() + " knows: " + knownActors);
        } else if (message instanceof ControlledFloodMessage) {

            // Handle controlled flooding message
            ControlledFloodMessage controlledFloodMessage = (ControlledFloodMessage) message;
            int sequenceNumber = controlledFloodMessage.getSequenceNumber();
            System.out.println(getSelf().path().name() + " received message with sequence number: " + sequenceNumber);

            if (!receivedSequenceNumbers.contains(sequenceNumber)) {
                // If the sequence number is not yet received, process the message
                receivedSequenceNumbers.add(sequenceNumber);

                // Forward the controlled flooding message to known actors
                for (ActorRef knownActor : knownActors) {
                    knownActor.tell(controlledFloodMessage, self());
                }
            }
        }
    }
}