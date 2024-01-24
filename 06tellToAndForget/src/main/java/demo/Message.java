package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;


public class Message {
    private final String content;
    private final ActorRef sender;
    private final ActorRef to;
    protected ActorRef ref1;
    protected ActorRef ref2;


    public Message(String content, ActorRef sender, ActorRef to) {
        this.content = content;
        this.sender = sender;
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public ActorRef getSender() {
        return sender;
    }
    public ActorRef getTo() {
        return to;
    }

    public void setRef1(ActorRef ref1){
        this.ref1 = ref1;
    }
    public void setRef2(ActorRef ref2){
        this.ref2 = ref2;
    }
}
