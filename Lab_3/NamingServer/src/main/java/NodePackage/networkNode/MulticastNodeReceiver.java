package NodePackage.networkNode;

import Functions.HashingFunction;
import NodePackage.Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastNodeReceiver {

    private static final String MULTICAST_IP = "230.0.0.0";
    private static final int PORT = 4446;

    private final Node localNode;

    public MulticastNodeReceiver(Node node) {
        this.localNode = node;
    }

    public void listen() throws Exception {
        MulticastSocket socket = new MulticastSocket(PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group);

        System.out.println("Node receiver listening for other nodes...");

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("📥 Received multicast: " + received);

            String[] parts = received.split(",");
            if (parts.length != 2) continue;

            String newNodeName = parts[0];
            String newNodeIP = parts[1];
            int newNodeID = HashingFunction.hashNodeName(newNodeName);

            int currentID = HashingFunction.hashNodeName(localNode.getName());
            int prevID = localNode.getPreviousID();
            int nextID = localNode.getNextID();

            // Node komt tussen current en next
            if (currentID < newNodeID && newNodeID < nextID) {
                localNode.setNextID(newNodeID);
                System.out.println("🔁 Updated nextID → " + newNodeID);

                // Stuur unicast terug naar de nieuwe node
                String response = "currentID=" + currentID +
                        ", prevID=" + prevID +
                        ", nextID=" + nextID;

                InetAddress targetAddress = InetAddress.getByName(newNodeIP);
                int targetPort = 4447; // vaste poort waarop nieuwe node luistert

                DatagramPacket reply = new DatagramPacket(
                        response.getBytes(),
                        response.length(),
                        targetAddress,
                        targetPort
                );

                DatagramSocket unicastSocket = new DatagramSocket();
                unicastSocket.send(reply);
                unicastSocket.close();

                System.out.println("📤 Sent unicast response to " + newNodeIP + ":4447 → " + response);



            }


            // Node komt tussen previous en current
            else if (prevID < newNodeID && newNodeID < currentID) {
                localNode.setPreviousID(newNodeID);
                System.out.println("🔁 Updated previousID → " + newNodeID);

                // Stuur unicast terug naar de nieuwe node
                String response = "currentID=" + currentID +
                        ", prevID=" + prevID +
                        ", nextID=" + nextID;

                InetAddress targetAddress = InetAddress.getByName(newNodeIP);
                int targetPort = 4447; // vaste poort waarop nieuwe node luistert

                DatagramPacket reply = new DatagramPacket(
                        response.getBytes(),
                        response.length(),
                        targetAddress,
                        targetPort
                );

                DatagramSocket unicastSocket = new DatagramSocket();

                unicastSocket.send(reply);
                unicastSocket.close();

                System.out.println("📤 Sent unicast response to " + newNodeIP + ":4447 → " + response);

            }
        }
    }
}
