package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.ArrayList;
import java.util.List;

public class ControlledFloodingPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("controlled-flooding");

        // Create actors
        final int numberOfActors = 5;
        List<ActorRef> actors = new ArrayList<>();

        for (int i = 0; i < numberOfActors; i++) {
            actors.add(system.actorOf(ActorNode.createActor(), "actor" + i));
        }

        // Define the adjacency matrix for the communication topology (No cycles)
        int[][] adjacencyMatrixCycles = {
                {0, 1, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0}
        };

        System.out.println("cycles controlled: ");
        // Send the list of known actors to each actor
        for (int i = 0; i < numberOfActors; i++) {
            List<ActorRef> knownActors = new ArrayList<>();
            for (int j = 0; j < numberOfActors; j++) {
                if (adjacencyMatrixCycles[i][j] == 1) {
                    knownActors.add(actors.get(j));
                }
            }
            actors.get(i).tell(new ActorNode.InitMessage(knownActors), ActorRef.noSender());
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Start the flooding algorithm from actor A
        actors.get(0).tell(new ActorNode.ControlledFloodMessage(0), ActorRef.noSender());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        actors.get(0).tell(new ActorNode.ControlledFloodMessage(1), ActorRef.noSender());
        actors.get(0).tell(new ActorNode.ControlledFloodMessage(2), ActorRef.noSender());

    }
}



