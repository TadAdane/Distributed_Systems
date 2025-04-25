package NodePackage;

import NodePackage.networkNode.MulticastNodeReceiver;
import NodePackage.networkNode.MulticastSender;

public class NodeApp {

    public Node createAndAnnounceNewNode(String name, String ipAddress, int prev, int next) {
        Node node = new Node(name, ipAddress);
        node.setPreviousID(prev);
        node.setNextID(next);

        try {
            MulticastSender.sendMulticast(name, ipAddress);
        } catch (Exception e) {
            System.err.println("Fout bij multicast:");
            e.printStackTrace();
        }

        startReceiver(node);
        return node;
    }

    public Node createListeningNode(String name, String ipAddress, int prev, int next) {
        Node node = new Node(name, ipAddress);
        node.setPreviousID(prev);
        node.setNextID(next);

        startReceiver(node);
        return node;
    }

    private void startReceiver(Node node) {
        try {
            MulticastNodeReceiver receiver = new MulticastNodeReceiver(node);
            new Thread(() -> {
                try {
                    receiver.listen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Receiver kon niet gestart worden:");
            e.printStackTrace();
        }
    }
}
