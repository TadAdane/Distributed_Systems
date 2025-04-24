package NodePackage;

public class Node {

    private String name;
    private String ipAddress;

    private int previousID = -1;  // wordt ingesteld na bootstrap
    private int nextID = -1;      // wordt ingesteld na bootstrap
    private int totalNodes = 0; // Veld om het totaal aantal nodes in het netwerk bij te houden

    public Node(String name, String ipAddress) {
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

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    // Optional: toon info
    public void printStatus() {
        System.out.println("NodePackage.Node: " + name + " @ " + ipAddress);
        System.out.println("Prev: " + previousID + ", Next: " + nextID);
    }
}