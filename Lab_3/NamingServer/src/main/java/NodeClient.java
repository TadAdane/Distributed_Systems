
public class NodeClient {

    private String name;
    private String ipAddress;

    private int previousID = -1;  // wordt ingesteld na bootstrap
    private int nextID = -1;      // wordt ingesteld na bootstrap

    public NodeClient(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPreviousID() {
        return previousID;
    }

    public void setPreviousID(int previousID) {
        this.previousID = previousID;
    }

    public int getNextID() {
        return nextID;
    }

    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    // Optional: toon info
    public void printStatus() {
        System.out.println("Node: " + name + " @ " + ipAddress);
        System.out.println("Prev: " + previousID + ", Next: " + nextID);
    }
}
