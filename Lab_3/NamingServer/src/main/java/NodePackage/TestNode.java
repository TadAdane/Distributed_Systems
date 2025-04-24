package NodePackage;

public class TestNode {
    public static void main(String[] args) {
        NodeApp app = new NodeApp();

        // 👇 Deze node multicast zichzelf en start de receiver
        app.createAndAnnounceNewNode("nodeX", "192.168.0.77", 5000, 10000);

        // of gewoon een luisterende node
        // app.createListeningNode("nodeA", "192.168.0.55", 3000, 7000);
    }
}
