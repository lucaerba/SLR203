package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class SubscribeMessage {
    final ActorRef subscriber;

    SubscribeMessage(ActorRef subscriber) {
        this.subscriber = subscriber;
    }
}

class UnsubscribeMessage {
    final ActorRef subscriber;

    UnsubscribeMessage(ActorRef subscriber) {
        this.subscriber = subscriber;
    }
}

public class SubscriberActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PublishMessage.class, this::handlePublish)
                .build();
    }

    private void handlePublish(PublishMessage message) {
        System.out.println(self().path().name() + " received: " + message.content);
    }

    static Props props() {
        return Props.create(SubscriberActor.class);
    }
}