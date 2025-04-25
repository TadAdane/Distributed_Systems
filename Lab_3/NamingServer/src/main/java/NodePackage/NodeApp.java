package NodePackage;

import NodePackage.networkNode.MulticastNodeReceiver;
import NodePackage.networkNode.MulticastSender;
import NodePackage.networkNode.NodeUnicastReceiver;

public class NodeApp {

    // Creates a new node and broadcasts it to the network via multicast
    public Node createAndAnnounceNewNode(String name, String ipAddress, int unicastPort) {
        Node node = new Node(name, ipAddress);

        try {
            // Send a multicast message containing the node name, IP address, and its unicast port
            MulticastSender.sendMulticast(name, ipAddress, unicastPort);

            // Start the unicast receiver to listen for responses from existing nodes
            new NodeUnicastReceiver(node, unicastPort).start();
        } catch (Exception e) {
            System.err.println("Error while sending multicast:");
            e.printStackTrace();
        }

        // Start the multicast receiver so this node can also listen for new incoming nodes
        startReceiver(node);

        return node;
    }

    // Used to create an existing node with known neighbors (manual configuration)
    public Node createListeningNode(String name, String ipAddress, int prev, int next) {
        Node node = new Node(name, ipAddress);
        node.setPreviousID(prev); // Set the known previous node
        node.setNextID(next);     // Set the known next node

        // Start the multicast receiver to listen for new joining nodes
        startReceiver(node);
        return node;
    }

    // Launches the multicast receiver in a separate thread
    private void startReceiver(Node node) {
        try {
            MulticastNodeReceiver receiver = new MulticastNodeReceiver(node);
            new Thread(() -> {
                try {
                    receiver.listen(); // Continuously listens for multicast messages
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Could not start multicast receiver:");
            e.printStackTrace();
        }
    }
}
