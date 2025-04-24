import NodePackage.Node;
import network.MulticastSender;

public class NodeApp {

    // Maakt een Node object aan met naam en IP-adres
    public Node createExternalNode(String name, String ipAddress) {
        return new Node(ipAddress, name);
    }

    // Verstuurt de Node via multicast naar het netwerk
    public void sendMulticast(Node multicastNode) {
        try {
            // Roep je bestaande MulticastSender aan
            MulticastSender.sendMulticast(multicastNode.getName(), multicastNode.getIpAddress());
        } catch (Exception e) {
            System.err.println("⚠️ Fout bij multicast versturen:");
            e.printStackTrace();
        }
    }
}

