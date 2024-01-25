package demo;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

// Define messages
class StoreMessage {
    final String key;
    final String value;

    StoreMessage(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

class RetrieveMessage {
    final String key;

    RetrieveMessage(String key) {
        this.key = key;
    }
}

class NodeInfoMessage {
    final String nodeId;

    NodeInfoMessage(String nodeId) {
        this.nodeId = nodeId;
    }
}

class FindNodeMessage {
    final String targetKey;

    FindNodeMessage(String targetKey) {
        this.targetKey = targetKey;
    }
}

public class KademliaNode extends AbstractActor {
    private final String nodeId;
    private final Map<String, String> data;
    private final DHT dht;

    KademliaNode(String nodeId, DHT dht) {
        this.nodeId = nodeId;
        this.data = new HashMap<>();
        this.dht = dht;
    }

    static Props props(String nodeId, DHT dht) {
        return Props.create(KademliaNode.class, () -> new KademliaNode(nodeId, dht));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StoreMessage.class, this::handleStore)
                .match(RetrieveMessage.class, this::handleRetrieve)
                .match(NodeInfoMessage.class, this::handleNodeInfo)
                .match(FindNodeMessage.class, this::handleFindNode)
                .build();
    }

    private void handleStore(StoreMessage message) {
        data.put(message.key, message.value);
        System.out.println("Node " + nodeId + " stored data: " + message.key + " -> " + message.value);
    }

    private void handleRetrieve(RetrieveMessage message) {
        String value = data.get(message.key);
        System.out.println("Node " + nodeId + " retrieved data for key " + message.key + ": " + value);
    }

    private void handleNodeInfo(NodeInfoMessage message) {
        System.out.println("Node " + nodeId + " received info about Node " + message.nodeId);
        dht.addNode(message.nodeId, sender());
    }

    private void handleFindNode(FindNodeMessage message) {
        System.out.println("Node " + nodeId + " handling FindNode for key " + message.targetKey);
        dht.findClosestNode(message.targetKey).tell(new FindNodeMessage(message.targetKey), self());
        System.out.println("Node " + nodeId + " responded to FindNode for key " + message.targetKey);
    }
}
