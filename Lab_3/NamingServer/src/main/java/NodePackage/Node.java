package NodePackage;
import Functions.HashingFunction;
import NodePackage.networkNode.MulticastSender;
import NodePackage.networkNode.MulticastNodeReceiver;


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

    // Optional: display node info
    public void printStatus() {
        System.out.println("Node: " + name + " @ " + ipAddress);
        System.out.println("Prev: " + previousID + ", Next: " + nextID);
    }

    // Send next node's ID to the previous node
    public void sendNextNodeToPrevious() {
        if (previousID != -1) {
            try {
                // Send the next node's ID to the previous node using multicast
                MulticastSender.sendMulticast(String.valueOf(previousID), String.valueOf(nextID));
            } catch (Exception e) {
                System.err.println("Error sending multicast for next node to previous node:");
                e.printStackTrace();
            }
        }
    }

    // Send previous node's ID to the next node
    public void sendPreviousNodeToNext() {
        if (nextID != -1) {
            try {
                // Send the previous node ID to the next node using multicast
                MulticastSender.sendMulticast(String.valueOf(nextID), String.valueOf(previousID));
            } catch (Exception e) {
                System.err.println("Error sending multicast for previous node to next node:");
                e.printStackTrace();
            }
        }
    }


    // Close node connections (if needed)
    public void closeConnections() {
        // Logic to close any open connections, clean up resources, etc.
        System.out.println("Closing connections for node " + this.name);
    }

    // Perform shutdown operation
    public void shutdown() {
        // Inform the previous node of the next node's ID
        sendNextNodeToPrevious();

        // Inform the next node of the previous node's ID
        sendPreviousNodeToNext();

        // Close any resources, connections
        closeConnections();

        // Remove the node from nodeMap (if that's part of your design)
       //emoveNodeFromMap();

        System.out.println("Node " + this.name + " has successfully shut down.");
    }


    // Failure detection and handling
    public void handleFailure() {
        // Inform the previous node of the next node's ID
        sendNextNodeToPrevious();

        // Inform the next node of the previous node's ID
        sendPreviousNodeToNext();

        // Close any resources, connections
        closeConnections();

        System.out.println("Node " + this.name + " has failed and is being shut down.");
    }
}


