package com.example.NamingServer.NamingServer.model;

// Represents a node in the distributed system, used by the Naming Server
public class Node {

    // Unique name of the node (used for hashing and identification)
    private String name;

    // IP address of the node
    private String ipAddress;

    // Optional: IDs of the previous and next nodes (used in routing or logical ring structure)
    int previousNodeID;
    int nextNodeID;

    // Default constructor (required for deserialization or empty initialization)
    public Node() {}

    // Constructor with parameters for direct creation
    public Node(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    // Returns the name of the node
    public String getName() {
        return name;
    }

    // Sets the name of the node
    public void setName(String name) {
        this.name = name;
    }

    // Returns the IP address of the node
    public String getIpAddress() {
        return ipAddress;
    }

    // Sets the IP address of the node
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
