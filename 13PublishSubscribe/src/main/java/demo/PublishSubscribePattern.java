package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class PublishSubscribePattern {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("PubSubSystem");

        ActorRef topic1 = system.actorOf(TopicActor.props(), "Topic1");
        ActorRef topic2 = system.actorOf(TopicActor.props(), "Topic2");

        ActorRef publisher1 = system.actorOf(Props.create(PublisherActor.class), "Publisher1");
        ActorRef publisher2 = system.actorOf(Props.create(PublisherActor.class), "Publisher2");

        publisher1.tell(new SetTopicMessage(topic1), ActorRef.noSender());
        publisher2.tell(new SetTopicMessage(topic2), ActorRef.noSender());

        ActorRef subscriberA = system.actorOf(Props.create(SubscriberActor.class), "SubscriberA");
        ActorRef subscriberB = system.actorOf(Props.create(SubscriberActor.class), "SubscriberB");
        ActorRef subscriberC = system.actorOf(Props.create(SubscriberActor.class), "SubscriberC");


        system.scheduler().scheduleOnce(
                Duration.create(1000, TimeUnit.MILLISECONDS),// delay
                () -> {
                    // Subscribers subscribe to the topics
                    topic1.tell(new SubscribeMessage(subscriberA), ActorRef.noSender());
                    topic1.tell(new SubscribeMessage(subscriberB), ActorRef.noSender());

                    topic2.tell(new SubscribeMessage(subscriberB), ActorRef.noSender());
                    topic2.tell(new SubscribeMessage(subscriberC), ActorRef.noSender());
                },
                system.dispatcher()
        );

        system.scheduler().scheduleOnce(
                Duration.create(2, TimeUnit.SECONDS), // delay
                () -> {
                    // Publishers publish messages to the topics
                    publisher1.tell(new PublishMessage("hello"), ActorRef.noSender());
                },
                system.dispatcher()
        );
        system.scheduler().scheduleOnce(
                Duration.create(3, TimeUnit.SECONDS), // delay
                () -> {
                    // Publishers publish messages to the topics
                    publisher2.tell(new PublishMessage("world"), ActorRef.noSender());
                },
                system.dispatcher()
        );
        system.scheduler().scheduleOnce(
                Duration.create(4, TimeUnit.SECONDS), // delay
                () -> {
                    // Subscribers unsubscribe from Topic1
                    topic1.tell(new UnsubscribeMessage(subscriberA), ActorRef.noSender());
                },
                system.dispatcher()
        );

        system.scheduler().scheduleOnce(
                Duration.create(5, TimeUnit.SECONDS), // delay
                () -> {
                    // Publisher1 publishes another message to Topic1
                    publisher1.tell(new PublishMessage("hello2"), ActorRef.noSender());
                },
                system.dispatcher()
        );

        // Terminate the actor system after a delay
        system.scheduler().scheduleOnce(
                Duration.create(6, TimeUnit.SECONDS), // delay
                () -> system.terminate(),
                system.dispatcher()
        );
    }
}
