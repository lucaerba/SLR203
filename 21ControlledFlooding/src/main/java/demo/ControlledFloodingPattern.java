package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


        // Start the flooding algorithm from actor A
        system.scheduler().scheduleOnce(
                Duration.create(500, TimeUnit.MILLISECONDS),
                () -> actors.get(0).tell(new ActorNode.ControlledFloodMessage(0), ActorRef.noSender()),
                system.dispatcher()
        );

        // Start the flooding algorithm from actor A
        system.scheduler().scheduleOnce(
                Duration.create(1000, TimeUnit.MILLISECONDS),
                () -> actors.get(0).tell(new ActorNode.ControlledFloodMessage(1), ActorRef.noSender()),
                system.dispatcher()
        );// Start the flooding algorithm from actor A
        system.scheduler().scheduleOnce(
                Duration.create(1000, TimeUnit.MILLISECONDS),
                () -> actors.get(0).tell(new ActorNode.ControlledFloodMessage(2), ActorRef.noSender()),
                system.dispatcher()
        );
        system.scheduler().scheduleOnce(
                Duration.create(1500, TimeUnit.MILLISECONDS),
                system::terminate,
                system.dispatcher()
        );
    }
}



