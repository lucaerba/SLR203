package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class SimpleLeaderElectionAlgorithm {
    public static void main(String[] args) {
        // Create actor system
        ActorSystem system = ActorSystem.create("ChangRobertsSystem");

        // Create Chang-Roberts nodes
        ActorRef node1 = system.actorOf(ChangRobertsNode.props(1), "Node1");
        ActorRef node2 = system.actorOf(ChangRobertsNode.props(2), "Node2");
        ActorRef node3 = system.actorOf(ChangRobertsNode.props(3), "Node3");
        ActorRef node4 = system.actorOf(ChangRobertsNode.props(4), "Node4");
        ActorRef node5 = system.actorOf(ChangRobertsNode.props(5), "Node5");
        ActorRef node6 = system.actorOf(ChangRobertsNode.props(6), "Node6");

        // Set up the ring structure
        node1.tell(new SetNextNodeMessage(node2), ActorRef.noSender());
        node2.tell(new SetNextNodeMessage(node3), ActorRef.noSender());
        node3.tell(new SetNextNodeMessage(node4), ActorRef.noSender());
        node4.tell(new SetNextNodeMessage(node5), ActorRef.noSender());
        node5.tell(new SetNextNodeMessage(node6), ActorRef.noSender());
        node6.tell(new SetNextNodeMessage(node1), ActorRef.noSender());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Simulate a process noticing a lack of leader and starting an election
        node1.tell(new StartElectionMessage(), ActorRef.noSender());
        node2.tell(new StartElectionMessage(), ActorRef.noSender());
        node6.tell(new SetNextNodeMessage(node2), ActorRef.noSender());
        system.stop(node1);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        node3.tell(new StartElectionMessage(), ActorRef.noSender());

        node5.tell(new SetNextNodeMessage(node2), ActorRef.noSender());
        system.stop(node6);
    }

}




