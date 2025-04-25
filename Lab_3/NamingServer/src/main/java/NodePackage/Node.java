package NodePackage;

public class Node {

    private String name;
    private String ipAddress;

    private int previousID = -1;
    private int nextID = -1;
    private int totalNodes = 0;

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


    public void printStatus() {
        System.out.println("🟢 Node status:");
        System.out.println("   → Name: " + name);
        System.out.println("   → IP: " + ipAddress);
        System.out.println("   → Previous ID: " + previousID);
        System.out.println("   → Next ID: " + nextID);
    }


}