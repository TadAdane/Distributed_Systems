package NodePackage.networkNode;

import NodePackage.Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NodeUnicastReceiver {

    private final Node localNode;
    private final int listenPort;

    public NodeUnicastReceiver(Node node, int port) {
        this.localNode = node;
        this.listenPort = port;
    }

    public void start() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(listenPort)) {
                System.out.println(" Node listening for unicast on port " + listenPort);

                while (true) {
                    byte[] buf = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    String response = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(" Unicast response received: " + response);

                    //  Extra debugoutput per veld
                    String[] parts = response.split(",");
                    for (String part : parts) {
                        System.out.println( part.trim());
                    }

                    //  Parse velden en update node
                    int prevID = -1;
                    int nextID = -1;

                    for (String part : parts) {
                        if (part.contains("prevID=")) {
                            prevID = Integer.parseInt(part.split("=")[1].trim());
                        } else if (part.contains("nextID=")) {
                            nextID = Integer.parseInt(part.split("=")[1].trim());
                        }
                    }

                    if (prevID != -1 && nextID != -1) {
                        localNode.setPreviousID(prevID);
                        localNode.setNextID(nextID);
                        System.out.println(" Node updated → prevID=" + prevID + ", nextID=" + nextID);
                        localNode.printStatus(); // optioneel
                    }
                }
            } catch (Exception e) {
                System.err.println(" Fout bij ontvangen van unicast:");
                e.printStackTrace();
            }
        }).start();
    }
}
