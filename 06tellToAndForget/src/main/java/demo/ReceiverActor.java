package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;


public class ReceiverActor extends AbstractActor {
    private ActorRef b;
    private ActorRef transmitter;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props createActor() {
        return Props.create(ReceiverActor.class, ReceiverActor::new);
    }

    @Override
    public Receive createReceive() {
        ReceiveBuilder builder = ReceiveBuilder.create();

        builder.match(
                Message.class,
                this::receiveMessage);

        // do some other stuff in between

        //builder.matchAny(o -> log.info("received unknown message"));

        return builder.build();
    }

    private void receiveMessage(Message message) {

        log.info("{} received message: {}, sender: {}",
                getSelf().path().name(), message.getContent(), message.getSender());
// Respond with a reply

        if(message.getSender() == null){
            if(message.getContent().equals("start")){
                Message m = new Message("hello", self(), b);
                transmitter.tell(m, self());
            }else {
                transmitter = message.ref1;
                b = message.ref2;
            }
        }

        if(message.getContent().equals("hello")){
            message.getSender().tell(new Message("hi!", self(), message.getSender()), self());
        }

    }
}
