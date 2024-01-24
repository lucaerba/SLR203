package demo;

import akka.actor.ActorRef;
import java.util.HashMap;
import java.util.Map;

public class DHT {

    private final Map<String, ActorRef> nodeTable;

    DHT() {
        this.nodeTable = new HashMap<>();
    }

    void addNode(String nodeId, ActorRef actorRef) {
        nodeTable.put(nodeId, actorRef);
    }

    ActorRef findClosestNode(String key) {
        // Simulate Kademlia routing by finding the closest node based on some heuristic
        // In a real implementation, this logic would be more sophisticated
        return nodeTable.values().iterator().next(); // For simplicity, just return the first node
    }

    void handleFindValue(String key, ActorRef sender) {
        // Simulate the FindValue operation
        ActorRef closestNode = findClosestNode(key);
        closestNode.tell(new FindNodeMessage(key), sender);
    }
}