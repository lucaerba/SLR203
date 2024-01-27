package demo;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

// Define messages
class ElectionMessage {
    final int uid;

    ElectionMessage(int uid) {
        this.uid = uid;
    }
}

class ElectedMessage {
    final int uid;

    ElectedMessage(int uid) {
        this.uid = uid;
    }
}

class LeaderElectedMessage {
    final int uid;

    LeaderElectedMessage(int uid) {
        this.uid = uid;
    }
}

class SetNextNodeMessage {
    final ActorRef nextNode;

    public SetNextNodeMessage(ActorRef nextNode) {
        this.nextNode = nextNode;
    }
}
// Add a new message class
class StartElectionMessage {}

public class ChangRobertsNode extends AbstractActor {
    private final int uid;
    private ActorRef nextNode;
    private boolean participant;
    private int leader;

    ChangRobertsNode(int uid) {
        this.uid = uid;
        this.participant = false;
    }

    public static Props props(int uid) {
        return Props.create(ChangRobertsNode.class, uid);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ElectionMessage.class, this::handleElectionMessage)
                .match(ElectedMessage.class, this::handleElectedMessage)
                .match(SetNextNodeMessage.class, this::handleSetNextNodeMessage)
                .match(LeaderElectedMessage.class, this::handleLeaderElectedMessage)
                .match(StartElectionMessage.class, message -> startElection()) // Handle StartElectionMessage
                .build();
    }



    // Add a method to start the election
    private void startElection() {
        // Your election logic here...
        System.out.println("Node " + uid + " is starting the election!");
        // Simulate a process noticing a lack of leader and starting an election
        participant = true;
        nextNode.tell(new ElectionMessage(uid), self());
    }
    private void handleLeaderElectedMessage(LeaderElectedMessage leaderElectedMessage) {
        participant = false;
        this.leader = leaderElectedMessage.uid;
        if(leaderElectedMessage.uid != uid){
            System.out.println("Node " + uid + " received new leader: Node " + leader );
            nextNode.tell(leaderElectedMessage, self());
        }
    }

    private void handleElectionMessage(ElectionMessage message) {

        //System.out.println("Node " + uid + " received ElectionMessage from Node " + message.uid);

        if (message.uid > uid) {
            // Forward the election message
            System.out.println("Node " + uid + " forwarding ElectionMessage to Node " + nextNode.path().name());
            participant = true;
            nextNode.tell(new ElectionMessage(message.uid), self());
        } else if (message.uid < uid && !participant) {
            // Replace UID and send the updated election message
            System.out.println("Node " + uid + " updating UID and forwarding ElectionMessage to Node " + nextNode.path().name());
            participant = true;
            nextNode.tell(new ElectionMessage(uid), self());
        }else if(!(message.uid == uid && participant)){
            System.out.println("Node " + uid + " discarding ElectionMessage");
        }
        // else discard the election message

        // Start acting as leader if UID matches
        if (message.uid == uid && participant) {
            startSecondPhase();
        }
    }

    private void handleElectedMessage(ElectedMessage message) {
        participant = false;

        System.out.println("Node " + uid + " received ElectedMessage from Node " + message.uid);

        if (message.uid != uid) {
            // Forward the elected message
            //System.out.println("Node " + uid + " forwarding ElectedMessage to Node " + nextNode.path().name());
            nextNode.tell(new ElectedMessage(message.uid), self());
        } else {
            // Leader discards the elected message, and the election is over
            System.out.println("Node " + uid + " is the leader!");
            nextNode.tell(new LeaderElectedMessage(uid), self());
        }
    }

    private void handleSetNextNodeMessage(SetNextNodeMessage message) {
        nextNode = message.nextNode;
    }

    private void startSecondPhase() {
        participant = false;
        // Send an elected message to the next node
        System.out.println("Node " + uid + " starting second phase and sending ElectedMessage to Node " + nextNode.path().name());
        nextNode.tell(new ElectedMessage(uid), self());
    }
}

