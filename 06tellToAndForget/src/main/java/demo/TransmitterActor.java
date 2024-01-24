package demo;

import akka.actor.*;
import akka.event.LoggingAdapter;
import akka.event.Logging;


public class TransmitterActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props createActor() {
        return Props.create(TransmitterActor.class, TransmitterActor::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, this::transmitMessage)
                .build();
    }

    private void transmitMessage(Message message) {
        // Change the sender to ref:a and forward the message to ref:b
        log.info("Transmitter forwarding message:");
        log.info("Transmitter:{}\nReceiver:{}", message.getSender(), message.getTo());
        message.getTo().forward(
                message, getContext());
    }
}