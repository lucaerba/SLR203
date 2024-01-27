package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashSet;
import java.util.Set;

class TopicActor extends AbstractActor {
    private Set<ActorRef> subscribers = new HashSet<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SubscribeMessage.class, this::handleSubscribe)
                .match(UnsubscribeMessage.class, this::handleUnsubscribe)
                .match(PublishMessage.class, this::handlePublish)
                .build();
    }

    private void handleSubscribe(SubscribeMessage message) {
        System.out.println(message.subscriber.path().name() + " subscribed to " + getSelf().path().name());
        subscribers.add(message.subscriber);
    }

    private void handleUnsubscribe(UnsubscribeMessage message) {
        System.out.println(message.subscriber.path().name() + " unsubscribed to " + getSelf().path().name());
        subscribers.remove(message.subscriber);
    }

    private void handlePublish(PublishMessage message) {
        for (ActorRef subscriber : subscribers) {
            subscriber.tell(new PublishMessage(message.content), self());
        }
    }

    static Props props() {
        return Props.create(TopicActor.class);
    }
}