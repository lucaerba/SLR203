package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.ArrayList;
import java.util.List;

public class CommunicationTopologyPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("communication-topology");

        // Create actors
        final int numberOfActors = 4;
        List<ActorRef> actors = new ArrayList<>();

        for (int i = 0; i < numberOfActors; i++) {
            actors.add(system.actorOf(ActorNode.createActor(), "actor" + (i+1)));
        }

        // Define the adjacency matrix for the communication topology
        int[][] adjacencyMatrix = {
                {0, 1, 1, 0},
                {0, 0, 0, 1},
                {1, 0, 0, 1},
                {1, 0, 0, 1},
        };

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
    }
}


