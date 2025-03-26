package com.example.NamingServer.controller;

import com.example.namingserver.model.Node;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {

    // Sample endpoint to add a node
    @PostMapping("/addNode")
    public String addNode(@RequestBody Node node) {
        // Here we would handle adding the node to the map
        // For now, just return a simple response
        return "Node added: " + node.getName();
    }

    // Sample endpoint to get the IP address of a file
    @GetMapping("/getFileLocation")
    public String getFileLocation(@RequestParam String filename) {
        // Here, you will implement the file lookup logic
        return "File location for " + filename + ": 192.168.1.1"; // Dummy IP
    }
}
