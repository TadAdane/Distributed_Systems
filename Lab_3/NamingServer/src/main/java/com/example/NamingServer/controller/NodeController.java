package com.example.NamingServer.controller;

import com.example.namingserver.model.Node;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {

    private static final int MAX = Integer.MAX_VALUE; // 2_147_483_647
    private static final int MIN = -Integer.MAX_VALUE; // -2_147_483_647

    // Hashing function (node name -> 0 to 32768)
    private int hashNodeName(String nodeName) {
        int hash = nodeName.hashCode(); // can be negative
        return (int) (((long) hashCode + MAX) * 32768 / ((long) MAX + Math.abs((long) MIN)));
    }

    // Sample endpoint to add a node
    @PostMapping("/addNode")
    public String addNode(@RequestBody Node node) {
        int hash = hashNodeName(node.getName());

        // Here we would handle adding the node to the map
        // For now, just return a simple response
        return "Node added: " + node.getName() + " (hash: " + hash + ")";
    }

    // Sample endpoint to get the IP address of a file
    @GetMapping("/getFileLocation")
    public String getFileLocation(@RequestParam String filename) {
        // Here, you will implement the file lookup logic
        return "File location for " + filename + ": 192.168.1.1"; // Dummy IP
    }
}
