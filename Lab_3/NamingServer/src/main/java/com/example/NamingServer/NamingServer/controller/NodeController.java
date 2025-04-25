package com.example.NamingServer.NamingServer.controller;

import NodePackage.Node;
import Functions.HashingFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.io.IOException;
import java.util.*;

//newest version, updated at 18:23, newest
@RestController
public class NodeController {

    private static final int MAX = Integer.MAX_VALUE;
    private static final int MIN = -Integer.MAX_VALUE;

    private Node nextNode;
    private Node previousNode;



    // NodePackage.Node hash → IP
    private TreeMap<Integer, String> nodeMap = new TreeMap<>();

    // IP → Node name (to be able to reconstruct full Node objects if needed)
    private Map<String, String> ipToName = new HashMap<>();

    // Hash → full metadata including prev/next info
    private final Map<Integer, NodeMeta> nodeMetadataMap = new HashMap<>();

    // filename → nodeHash (owner)
    private Map<String, Integer> fileToNodeMap = new HashMap<>();



    // nodeHash → list of owned files
    private Map<Integer, List<String>> localFiles = new HashMap<>();

    // nodeHash → list of replicated files
    private Map<Integer, List<String>> replicas = new HashMap<>();



    @Autowired
    private RestTemplate restTemplate; // hier voeg je hem toe

    private void sendHttpPost(String url, Object body) { // hier voeg je hem toe
        try {
            restTemplate.postForObject(url, body, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
            // extra logging aanbevolen
        }
    }


//    // Hashing function to map input to 0–32768
//    private int hashNodeName(String nodeName) {
//        int hash = 0;
//        for (int i = 0; i < nodeName.length(); i++) {
//            hash = 31 * hash + nodeName.charAt(i); // A better approach for string hashing
//        }
//        return (Math.abs(hash) % 32768); // Ensure the result is within the 0-32768 range
//    }

    // Saves the current nodeMap to a JSON file on disk
    private void saveNodeMapToDisk() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Writes the map to a file named "nodeMap.json"
            mapper.writeValue(new File("nodeMap.json"), nodeMap);
        } catch (IOException e) {
            // Print the error if saving fails
            e.printStackTrace();
        }
    }


//    @PostMapping("/getPrevious")
//    public String getPrevious(@RequestBody Node node) {
//        int hash = hashNodeName(node.getName());
//        Integer prevHash = nodeMap.lowerKey(hash);
//
//        // If there is no lower number (begin of the ring), pick the last node
//        if (prevHash == null) {
//            prevHash = nodeMap.lastKey();
//        }
//
//        return nodeMap.get(prevHash);
//    }

    @PostMapping("/getPrevious")
    public Node getPrevious(@RequestBody Node node) {
        int hash = HashingFunction.hashNodeName(node.getName());
        Integer prevHash = nodeMap.lowerKey(hash);

        // If there's no lower key, it means we're at the beginning — loop to the last
        if (prevHash == null) {
            prevHash = nodeMap.lastKey();
        }

        String prevIp = nodeMap.get(prevHash);
        String name  = getNodeNameFromIp(prevIp);

        Node prevNode = new Node(prevIp, name);

        return prevNode;
    }


//    @PostMapping("/getNext")
//    public String getNext(@RequestBody Node node) {
//        int hash = hashNodeName(node.getName());
//        Integer nextHash = nodeMap.higherKey(hash);
//
//        // If there is no higher number (end of the ring), pick the first node
//        if (nextHash == null) {
//            nextHash = nodeMap.firstKey();
//        }
//
//        return nodeMap.get(nextHash);
//    }

    @PostMapping("/getNext")
    public Node getNext(@RequestBody Node node) {
        int hash = HashingFunction.hashNodeName(node.getName());
        Integer nextHash = nodeMap.higherKey(hash);

        // If there's no higher key, we're at the end — loop to the first
        if (nextHash == null) {
            nextHash = nodeMap.firstKey();
        }

        String nextIp = nodeMap.get(nextHash);
        String name = getNodeNameFromIp(nextIp);

        Node nextNode = new Node(nextIp, name);

        return nextNode;
    }

    private String getNodeNameFromIp(String ip) {
        if (ipToName.containsKey(ip)) {
            return ipToName.get(ip);
        } else {
            return "unknown";
        }
    }


    // Add a node
    @PostMapping("/addNode")
    public String addNode(@RequestBody Node node) {
        int hash = HashingFunction.hashNodeName(node.getName());

        if (nodeMap.containsKey(hash)) {
            return "NodePackage.Node with name already exists (hash collision): " + hash;
        }

        // Add the node to the map
        nodeMap.put(hash, node.getIpAddress());

        // Update neighbors after adding the node
        updateNodeNeighbors();

        // Persist the updated map to disk
        saveNodeMapToDisk();

        return "NodePackage.Node added: " + node.getName() + " (hash: " + hash + ")";
    }


    private void updateNodeNeighbors() {
        List<Integer> keys = new ArrayList<>(nodeMap.keySet());

        for (int i = 0; i < keys.size(); i++) {
            int current = keys.get(i);
            int prev = keys.get((i - 1 + keys.size()) % keys.size());
            int next = keys.get((i + 1) % keys.size());

            nodeMetadataMap.put(
                    current,
                    new NodeMeta(
                            nodeMap.get(current),  // current IP
                            prev,
                            nodeMap.get(prev),
                            next,
                            nodeMap.get(next)
                    )
            );
        }
    }

    public class NodeMeta {
        private String ip;
        private int prevHash;
        private String prevIp;
        private int nextHash;
        private String nextIp;

        public NodeMeta(String ip, int prevHash, String prevIp, int nextHash, String nextIp) {
            this.ip = ip;
            this.prevHash = prevHash;
            this.prevIp = prevIp;
            this.nextHash = nextHash;
            this.nextIp = nextIp;
        }

    }



    // Remove a node
    @PostMapping("/removeNode")
    public String removeNode(@RequestBody Node node) {
        int hash = HashingFunction.hashNodeName(node.getName());

        if (!nodeMap.containsKey(hash)) {
            return "NodePackage.Node not found for removal: " + node.getName();
        }

        nodeMap.remove(hash);
        localFiles.remove(hash);
        replicas.remove(hash);


        // Optional: remove files from fileToNodeMap that belonged to this node
        fileToNodeMap.values().removeIf(value -> value == hash);

        // Persist the updated map to disk
        saveNodeMapToDisk();

        return "NodePackage.Node removed: " + node.getName();
    }

    // Register a file to a node (owner) + replica to node based on file hash
    @PostMapping("/registerFile")
    public String registerFile(@RequestParam String filename, @RequestParam String nodeName) {
        int nodeHash = HashingFunction.hashNodeName(nodeName);

        if (!nodeMap.containsKey(nodeHash)) {
            return "NodePackage.Node not registered: " + nodeName;
        }

        fileToNodeMap.put(filename, nodeHash);
        localFiles.computeIfAbsent(nodeHash, k -> new ArrayList<>()).add(filename);

        int fileHash = HashingFunction.hashNodeName(filename);
        Integer replicaNode = nodeMap.floorKey(fileHash);
        if (replicaNode == null) replicaNode = nodeMap.lastKey();

        if (!replicaNode.equals(nodeHash)) {
            replicas.computeIfAbsent(replicaNode, k -> new ArrayList<>()).add(filename);
        }

        return "File '" + filename + "' registered to node '" + nodeName + "' (hash: " + nodeHash + "), replica at node hash: " + replicaNode;
    }

    // Get location (IP) of a file based on file name
    @GetMapping("/getFileLocation")
    public String getFileLocation(@RequestParam String filename) {
        Integer nodeHash = fileToNodeMap.get(filename);
        if (nodeHash == null) {
            return "File '" + filename + "' not registered.";
        }

        String ip = nodeMap.get(nodeHash);
        if (ip == null) {
            return "NodePackage.Node with hash " + nodeHash + " not found.";
        }

        return "File location for '" + filename + "' → NodePackage.Node hash: " + nodeHash + " → IP: " + ip;
    }

    // Optional: file fallback using hash-based logic (like in slide spec)
    @GetMapping("/getFileLocationHashed")
    public String getFileLocationHashed(@RequestParam String filename) {
        int fileHash = HashingFunction.hashNodeName(filename);
        Integer nodeHash = nodeMap.floorKey(fileHash);
        if (nodeHash == null) nodeHash = nodeMap.lastKey();

        String ip = nodeMap.get(nodeHash);
        return "File hash = " + fileHash + ", routed to node hash: " + nodeHash + " → IP: " + ip;
    }

    // Return all registered nodes
    @GetMapping("/getAllNodes")
    public Map<Integer, String> getAllNodes() {
        return nodeMap;
    }

    // Return files locally owned by a node
    @GetMapping("/getLocalFiles")
    public List<String> getLocalFiles(@RequestParam String nodeName) {
        int hash = HashingFunction.hashNodeName(nodeName);
        return localFiles.getOrDefault(hash, Collections.emptyList());
    }

    // Return replicated files of a node
    @GetMapping("/getReplicas")
    public List<String> getReplicas(@RequestParam String nodeName) {
        int hash = HashingFunction.hashNodeName(nodeName);
        return replicas.getOrDefault(hash, Collections.emptyList());
    }

    @GetMapping("/getNodesWithFiles")
    public Map<String, Object> getNodesWithFiles() {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<Integer, String> entry : nodeMap.entrySet()) {
            Integer nodeHash = entry.getKey();
            String ip = entry.getValue();

            Map<String, Object> nodeInfo = new LinkedHashMap<>();
            nodeInfo.put("ip", ip);
            nodeInfo.put("localFiles", localFiles.getOrDefault(nodeHash, Collections.emptyList()));
            nodeInfo.put("replicas", replicas.getOrDefault(nodeHash, Collections.emptyList()));

            result.put("NodeHash " + nodeHash, nodeInfo);
        }

        return result;
    }


//    This algorithm is activated in every exception thrown during communication with other nodes. This allows distributed detection of node failure
//    Request the previous node and next node parameters from the nameserver
//    Update the `next node` parameter of the previous node with the information received from the nameserver
//    Update the `previous node` parameter of the next node with the information received from the nameserver
//    Remove the node from the Naming server
//    Test this algorithm manually terminating a node (CTRL – C) and use a ping method as part of each node, that throws an exception when connection fails to a given node


    @PostMapping("/reportFailure")
    public String reportFailure(@RequestBody Node failedNode) {
//        int failedHash = hashNodeName(failedNode.getName());

        Node prevNode = getPrevious(failedNode);
        Node nextNode = getNext(failedNode);

        // Send updates to the neighboring nodes
        sendHttpPost(prevNode.getIpAddress() + "/updateNext", nextNode);
        sendHttpPost(nextNode.getIpAddress() + "/updatePrevious", prevNode);

        // Remove the failed node from map
        removeNode(failedNode);

        return "Failure handled for node: " + failedNode.getName();
    }

    @PostMapping("/updateNext")
    public void updateNext(@RequestBody Node newNext) {
        this.nextNode = newNext;
    }

    @PostMapping("/updatePrevious")
    public void updatePrevious(@RequestBody Node newPrev) {
        this.previousNode = newPrev;
    }


}
