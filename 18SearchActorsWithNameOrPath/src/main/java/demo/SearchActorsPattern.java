package demo;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class SearchActorsPattern{

    public static void main(String[] args) {
        // Create actor system
        ActorSystem system = ActorSystem.create("ActorCreationSystem");

        // Create ActorA
        ActorRef actorA = system.actorOf(Props.create(ActorCreator.class), "a");

        // Send CREATE messages to actorA
        actorA.tell("CREATE", ActorRef.noSender());
        actorA.tell("CREATE", ActorRef.noSender());

        searchActor(system, "a", actorA);
        searchActor(system, "a/actor1", actorA);
        searchActor(system, "a/actor1", actorA);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Print paths of all local actors in the top-level scopes
        Object identifyId = 1;

        System.out.println("Paths under /user:");
        system.actorSelection("/user/*").tell(new Identify(identifyId), actorA);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Paths under /system:");
        system.actorSelection("/system/*").tell(new Identify(identifyId), actorA);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Paths under /deadLetters:");
        system.actorSelection("/deadLetters/*").tell(new Identify(identifyId), actorA);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Paths under /temp:");
        system.actorSelection("/temp/*").tell(new Identify(identifyId), actorA);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Paths under /remote:");
        system.actorSelection("/remote/*").tell(new Identify(identifyId), actorA);


    }

    private static void searchActor(ActorSystem system, String actorPath, ActorRef sender) {
        system.actorSelection("/user/"+actorPath).tell(new Identify(0), sender);
    }
}
