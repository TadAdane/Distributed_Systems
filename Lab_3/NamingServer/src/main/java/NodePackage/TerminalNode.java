package NodePackage;

public class TerminalNode {
    public static void main(String[] args) {
        NodeApp app = new NodeApp();

        // Pas hier zelf aan:
        app.createAndAnnounceNewNode("nodeZ", "192.168.0.78", 5000, 10000);
    }
}
