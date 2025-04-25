package NodePackage;

public class TestNode {
    public static void main(String[] args) {
        NodeApp app = new NodeApp();
        app.createAndAnnounceNewNode("nodeX", "192.168.0.77", 5000, 10000);
    }
}
