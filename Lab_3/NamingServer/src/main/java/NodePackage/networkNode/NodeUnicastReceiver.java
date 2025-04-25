package NodePackage.networkNode;

import NodePackage.Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NodeUnicastReceiver {

    private final Node localNode;     // The node instance that will be updated
    private final int listenPort;     // The port this node listens on for unicast messages

    public NodeUnicastReceiver(Node node, int port) {
        this.localNode = node;
        this.listenPort = port;
    }

    // Starts a new thread that continuously listens for unicast packets
    public void start() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(listenPort)) {
                System.out.println("Node listening for unicast on port " + listenPort);

                while (true) {
                    byte[] buf = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet); // Wait for a unicast message

                    String response = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Unicast response received: " + response);

                    // Split and log each part of the response
                    String[] parts = response.split(",");
                    for (String part : parts) {
                        System.out.println(part.trim());
                    }

                    // Extract prevID and nextID from the response
                    int prevID = -1;
                    int nextID = -1;

                    for (String part : parts) {
                        if (part.contains("prevID=")) {
                            prevID = Integer.parseInt(part.split("=")[1].trim());
                        } else if (part.contains("nextID=")) {
                            nextID = Integer.parseInt(part.split("=")[1].trim());
                        }
                    }

                    // If both values were found, update the node and print status
                    if (prevID != -1 && nextID != -1) {
                        localNode.setPreviousID(prevID);
                        localNode.setNextID(nextID);
                        System.out.println("Node updated → prevID=" + prevID + ", nextID=" + nextID);
                        localNode.printStatus();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error while receiving unicast:");
                e.printStackTrace();
            }
        }).start();
    }
}
