package demo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class KademliaProtocolSimulation {
    public static void main(String[] args) {
        // Create actor system
        ActorSystem system = ActorSystem.create("KademliaSystem");

        // Create DHT
        DHT dht = new DHT();

        // Create Kademlia nodes
        ActorRef node1 = system.actorOf(KademliaNode.props("Node1", dht), "Node1");
        ActorRef node2 = system.actorOf(KademliaNode.props("Node2", dht), "Node2");
        ActorRef node3 = system.actorOf(KademliaNode.props("Node3", dht), "Node3");

        // Simulate node joining by sharing node information
        node1.tell(new NodeInfoMessage("Node2"), ActorRef.noSender());
        node2.tell(new NodeInfoMessage("Node3"), ActorRef.noSender());
        node3.tell(new NodeInfoMessage("Node1"), ActorRef.noSender());

        // Simulate storing and retrieving data
        node1.tell(new StoreMessage("key1", "value1"), ActorRef.noSender());
        node2.tell(new RetrieveMessage("key1"), ActorRef.noSender());

        // Simulate FindNode operation to demonstrate DHT routing
        node1.tell(new FindNodeMessage("someKey"), ActorRef.noSender());


    }
}




