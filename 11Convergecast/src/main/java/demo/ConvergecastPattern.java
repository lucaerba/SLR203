package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class ConvergecastPattern {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ConvergecastSystem");

        // Create actors
        ActorRef a = system.actorOf(ConvergecastActor.props("A"), "A");
        ActorRef b = system.actorOf(ConvergecastActor.props("B"), "B");
        ActorRef c = system.actorOf(ConvergecastActor.props("C"), "C");
        ActorRef merger = system.actorOf(MergerActor.props(), "Merger");
        ActorRef d = system.actorOf(ConvergecastActor.props("D"), "D");

        // Set up the convergence structure
        a.tell(new JoinMessage(merger), ActorRef.noSender());
        b.tell(new JoinMessage(merger), ActorRef.noSender());
        c.tell(new JoinMessage(merger), ActorRef.noSender());
        merger.tell(new SetTargetMessage(d), ActorRef.noSender());

        //schedule c unjoin
        system.scheduler().scheduleOnce(
                Duration.create(2000, TimeUnit.MILLISECONDS),
                merger,
                new UnjoinMessage(c),
                system.dispatcher(),
                ActorRef.noSender()
        );
        system.scheduler().scheduleOnce(
                Duration.create(3, TimeUnit.SECONDS),
                a,
                new GoMessage(),
                system.dispatcher(),
                ActorRef.noSender()
        );

        system.scheduler().scheduleOnce(
                Duration.create(3, TimeUnit.SECONDS),
                b,
                new GoMessage(),
                system.dispatcher(),
                ActorRef.noSender()
        );

        // Shutdown the actor system after a delay
        system.scheduler().scheduleOnce(
                Duration.create(6, TimeUnit.SECONDS),
                system::terminate,
                system.dispatcher()
        );
    }
}
