package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UncontrolledFloodingPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("uncontrolled-flooding");

        // Create actors
        final int numberOfActors = 5;
        List<ActorRef> actors = new ArrayList<>();

        for (int i = 0; i < numberOfActors; i++) {
            actors.add(system.actorOf(ActorNode.createActor(), "actor" + i));
        }

        System.out.println("no cycles: ");
        // Define the adjacency matrix for the communication topology (No cycles)
        int[][] adjacencyMatrixNoCycles = {
                {0, 1, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0}
        };

        // Send the list of known actors to each actor
        for (int i = 0; i < numberOfActors; i++) {
            List<ActorRef> knownActors = new ArrayList<>();
            for (int j = 0; j < numberOfActors; j++) {
                if (adjacencyMatrixNoCycles[i][j] == 1) {
                    knownActors.add(actors.get(j));
                }
            }
            actors.get(i).tell(new ActorNode.InitMessage(knownActors), ActorRef.noSender());
        }

        // Start the flooding algorithm from actor A
        /*
        system.scheduler().scheduleOnce(
                Duration.create(1000, TimeUnit.MILLISECONDS),
                actors.get(0),
                "start-flood",
                system.dispatcher(),
                ActorRef.noSender()
        );*/
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        actors.get(0).tell("start-flood", ActorRef.noSender());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Define the adjacency matrix for the communication topology (No cycles)
        int[][] adjacencyMatrixCycles = {
                {0, 1, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0}
        };

        System.out.println("cycles: ");
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
        actors.get(0).tell("start-flood", ActorRef.noSender());
        try {
            Thread.sleep(100);
            system.terminate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}



