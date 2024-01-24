package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorNode extends UntypedAbstractActor {

    private List<ActorRef> knownActors;
    private Map<Integer, Integer> receivedSequenceNumbers;

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
        private final int length;

        public ControlledFloodMessage(int sequenceNumber, int length) {
            this.sequenceNumber = sequenceNumber;
            this.length = length;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public int getLength() {
            return length;
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.receivedSequenceNumbers = new HashMap<>();
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
            int length = controlledFloodMessage.getLength();
            System.out.println(getSelf().path().name() + " received message with sequence number: " + sequenceNumber +
                    ", length: " + length);

            if (!receivedSequenceNumbers.containsKey(sequenceNumber) || receivedSequenceNumbers.get(sequenceNumber) > length) {
                // If the sequence number is not yet received or the length is greater than the stored length, process the message
                receivedSequenceNumbers.put(sequenceNumber, length);

                // Increment the length and forward the controlled flooding message to known actors
                for (ActorRef knownActor : knownActors) {
                    knownActor.tell(new ControlledFloodMessage(sequenceNumber, length + 1), self());
                }
            }
        }
    }
}