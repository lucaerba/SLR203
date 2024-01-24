package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class BroadcastRoundRobinPattern {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("system");

        // Create actors
        final ActorRef broadcaster = system.actorOf(BroadcasterActor.createActor(), "broadcaster");
        final ActorRef a = system.actorOf(Actor.createActor(broadcaster), "a");
        final ActorRef b = system.actorOf(Actor.createActor(broadcaster), "b");
        final ActorRef c = system.actorOf(Actor.createActor(broadcaster), "c");
    }
}
