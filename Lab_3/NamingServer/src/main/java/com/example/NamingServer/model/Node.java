package com.example.NamingServer.model;

public class Node {

    private String name; // Node name (used for hashing)
    private String ipAddress; // IP address of the node

    // Constructors, getters, setters
    public Node() {}

    public Node(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
