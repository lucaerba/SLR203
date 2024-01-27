package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class PublishMessage {
    final String content;

    PublishMessage(String content) {
        this.content = content;
    }
}

class SetTopicMessage {
    final ActorRef topic;

    SetTopicMessage(ActorRef topic) {
        this.topic = topic;
    }
}

public class PublisherActor extends AbstractActor {
    private ActorRef topic;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SetTopicMessage.class, this::handleTopic)
                .match(PublishMessage.class, this::handlePublish)
                .build();
    }

    private void handleTopic(SetTopicMessage topic) {
        this.topic = topic.topic;
    }

    private void handlePublish(PublishMessage message) {
        if (topic != null) {
            System.out.println(getSelf().path().name() + " published message ( " + message.content+ ") on "  + topic.path().name());
            topic.tell(message, self());
        }
    }

    static Props props() {
        return Props.create(PublisherActor.class);
    }
}