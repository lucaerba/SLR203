package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.ArrayList;
import java.util.List;

public class ShortestLengthFloodingPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("uncontrolled-flooding");

        // Create actors
        final int numberOfActors = 18;
        List<ActorRef> actors = new ArrayList<>();

        for (int i = 0; i < numberOfActors; i++) {
            actors.add(system.actorOf(ActorNode.createActor(), String.valueOf((char)('A' + i))));
        }

        // Define the adjacency matrix for the communication topology
        int[][] adjacencyMatrix = {
                {0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // A
                {1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, // B
                {1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, // C
                {1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // D
                {1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // E
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, // F
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // G
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, // H
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, // I
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // J
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // K
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // L
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, // M
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, // N
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, // O
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, // Q
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}  // R
              // A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R

        };


        System.out.println("cycles controlled: ");
        // Send the list of known actors to each actor
        for (int i = 0; i < numberOfActors; i++) {
            List<ActorRef> knownActors = new ArrayList<>();
            for (int j = 0; j < numberOfActors; j++) {
                if (adjacencyMatrix[i][j] == 1) {
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
        /*
        Run the algorithm by starting with actor A and answer those questions:
            - how many messages are received by P ?
            - What is the lowest value "length" received by P ?
         */
        System.out.println("from A");
        actors.get(0).tell(new ActorNode.ControlledFloodMessage(0, 0), ActorRef.noSender());

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        /*
        Now run the algorithm by starting with actor L and answer those questions:
            - how many messages are received by P ?
            - What is the lowest value "length" received by P ?
         */
        System.out.println("from L");
        actors.get(11).tell(new ActorNode.ControlledFloodMessage(1, 0), ActorRef.noSender());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        /*
        Now run the algorithm by starting with actor I and answer those questions:
            - how many messages are received by Q ?
            - What is the lowest value "length" received by Q ?
         */

        System.out.println("from I");
        actors.get(8).tell(new ActorNode.ControlledFloodMessage(2, 0), ActorRef.noSender());
        try {
            Thread.sleep(200);
            system.terminate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}



