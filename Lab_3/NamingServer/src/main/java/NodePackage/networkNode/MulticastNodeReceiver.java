package NodePackage.networkNode;

import Functions.HashingFunction;
import NodePackage.Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastNodeReceiver {

    // Multicast IP and port that all nodes use to send and receive multicast discovery messages
    private static final String MULTICAST_IP = "230.0.0.0";
    private static final int PORT = 4446;

    // The local node object that this receiver instance represents
    private final Node localNode;

    // Constructor: assigns the node that will use this multicast receiver
    public MulticastNodeReceiver(Node node) {
        this.localNode = node;
    }

    // Main method to start listening for multicast packets
    public void listen() throws Exception {
        // Open a multicast socket and join the multicast group
        MulticastSocket socket = new MulticastSocket(PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group);

        System.out.println("Node receiver listening for other nodes...");

        // Continuously listen for new multicast messages
        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet); // Wait for a multicast message

            // Read and print the message
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received multicast: " + received);

            // Expecting format: name,ip,unicastPort (e.g. nodeA,192.168.0.78,4449)
            String[] parts = received.split(",");
            if (parts.length != 3) continue; // If the format is incorrect, ignore this message

            // Extract node info from the received message
            String newNodeName = parts[0];
            String newNodeIP = parts[1];
            int targetPort = Integer.parseInt(parts[2].trim()); // Port where the new node is listening for unicast

            // Compute the hash values for the new node and for this local node
            int newNodeID = HashingFunction.hashNodeName(newNodeName);
            int currentID = HashingFunction.hashNodeName(localNode.getName());
            int prevID = localNode.getPreviousID();
            int nextID = localNode.getNextID();

            // CASE 1: If the new node logically fits between this node and its next node
            // Then this node should update its next pointer and notify the new node
            if (currentID < newNodeID && newNodeID < nextID) {
                localNode.setNextID(newNodeID);
                System.out.println("Updated nextID to " + newNodeID);

                // Construct the unicast response message with current, prev and next IDs
                String response = "currentID=" + currentID +
                        ", prevID=" + prevID +
                        ", nextID=" + nextID;

                // Send the unicast response to the new node’s IP and port
                InetAddress targetAddress = InetAddress.getByName(newNodeIP);
                DatagramPacket reply = new DatagramPacket(
                        response.getBytes(),
                        response.length(),
                        targetAddress,
                        targetPort
                );

                DatagramSocket unicastSocket = new DatagramSocket();
                unicastSocket.send(reply);
                unicastSocket.close();

                System.out.println("Sent unicast response to " + newNodeIP + ":" + targetPort + " → " + response);
            }

            // CASE 2: If the new node logically fits between this node’s previous and this node
            // Then this node should update its previous pointer and notify the new node
            else if (prevID < newNodeID && newNodeID < currentID) {
                localNode.setPreviousID(newNodeID);
                System.out.println("Updated previousID to " + newNodeID);

                // Construct the unicast response message with current, prev and next IDs
                String response = "currentID=" + currentID +
                        ", prevID=" + prevID +
                        ", nextID=" + nextID;

                // Send the unicast response to the new node’s IP and port
                InetAddress targetAddress = InetAddress.getByName(newNodeIP);
                DatagramPacket reply = new DatagramPacket(
                        response.getBytes(),
                        response.length(),
                        targetAddress,
                        targetPort
                );

                DatagramSocket unicastSocket = new DatagramSocket();
                unicastSocket.send(reply);
                unicastSocket.close();

                System.out.println("Sent unicast response to " + newNodeIP + ":" + targetPort + " → " + response);
            }
        }
    }
}
