package NodePackage.NodeClients;

import NodePackage.NodeApp;

public class TestNode3 {
    public static void main(String[] args) {
        NodeApp app = new NodeApp();
        app.createAndAnnounceNewNode("TestNode3", 2201);
    }
}
