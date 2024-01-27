package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

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

        // Simulate a process noticing a lack of leader and starting an election
        scheduleElection(node1, system, 500);

        // Simulate three processes noticing a lack of leader and starting an election
        scheduleElection(node1, system, 1000);
        scheduleElection(node2, system, 1000);
        scheduleElection(node4, system, 1000);

        // Simulate two processes noticing a lack of leader and starting an election, then 1 of them stops
        scheduleElection(node1, system, 1500);
        scheduleElection(node2, system, 1500);
        scheduleStopAndSetNextNode(node1, node6, node2, system, 1500);

        // Simulate two processes noticing a lack of leader and starting an election, then the biggest UID stops
        scheduleElection(node3, system, 2000);
        scheduleStopAndSetNextNode(node6, node5, node2, system, 2000);
    }

    private static void scheduleElection(ActorRef node, ActorSystem system, int delay) {
        system.scheduler().scheduleOnce(
                Duration.create(delay, TimeUnit.MILLISECONDS),
                () -> node.tell(new StartElectionMessage(), ActorRef.noSender()),
                system.dispatcher()
        );
    }

    private static void scheduleStopAndSetNextNode(ActorRef node, ActorRef beforeNode, ActorRef nextNode, ActorSystem system, int delay) {
        system.scheduler().scheduleOnce(
                Duration.create(delay, TimeUnit.MILLISECONDS),
                () -> {
                    beforeNode.tell(new SetNextNodeMessage(nextNode), ActorRef.noSender());
                    system.stop(node);
                    System.out.println(node.path().name() + " being deleted");
                },
                system.dispatcher()
        );
    }
}
