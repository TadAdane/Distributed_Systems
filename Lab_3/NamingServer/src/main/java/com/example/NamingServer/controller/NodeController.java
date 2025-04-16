package com.example.NamingServer.controller;

import com.example.NamingServer.model.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
public class NodeController {

    private static final int MAX = Integer.MAX_VALUE;
    private static final int MIN = -Integer.MAX_VALUE;


    // Node hash → IP
    private TreeMap<Integer, String> nodeMap = new TreeMap<>();
    private final Map<Integer, NodeMeta> nodeMetadataMap = new HashMap<>();

    // filename → nodeHash (owner)
    private Map<String, Integer> fileToNodeMap = new HashMap<>();



    // nodeHash → list of owned files
    private Map<Integer, List<String>> localFiles = new HashMap<>();

    // nodeHash → list of replicated files
    private Map<Integer, List<String>> replicas = new HashMap<>();

    // Hashing function to map input to 0–32768
    private int hashNodeName(String nodeName) {
        int hash = 0;
        for (int i = 0; i < nodeName.length(); i++) {
            hash = 31 * hash + nodeName.charAt(i); // A better approach for string hashing
        }
        return (Math.abs(hash) % 32768); // Ensure the result is within the 0-32768 range
    }

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


    @PostMapping("/getPrevious")
    public String getPrevious(@RequestBody Node node) {
        int hash = hashNodeName(node.getName());
        Integer prevHash = nodeMap.lowerKey(hash);

        // Als er geen lager getal is (begin van de ring), pak laatste node
        if (prevHash == null) {
            prevHash = nodeMap.lastKey();
        }

        return nodeMap.get(prevHash);
    }

    @PostMapping("/getNext")
    public String getNext(@RequestBody Node node) {
        int hash = hashNodeName(node.getName());
        Integer nextHash = nodeMap.higherKey(hash);

        // Als er geen hoger getal is (einde van de ring), pak eerste node
        if (nextHash == null) {
            nextHash = nodeMap.firstKey();
        }

        return nodeMap.get(nextHash);
    }


    // Add a node
    @PostMapping("/addNode")
    public String addNode(@RequestBody Node node) {
        int hash = hashNodeName(node.getName());

        if (nodeMap.containsKey(hash)) {
            return "Node with name already exists (hash collision): " + hash;
        }

        // Add the node to the map
        nodeMap.put(hash, node.getIpAddress());

        // Update neighbors after adding the node
        updateNodeNeighbors();

        // Persist the updated map to disk
        saveNodeMapToDisk();

        return "Node added: " + node.getName() + " (hash: " + hash + ")";

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

        // Add getters/setters if needed (or use Lombok)
    }



    // Remove a node
    @PostMapping("/removeNode")
    public String removeNode(@RequestBody Node node) {
        int hash = hashNodeName(node.getName());

        if (!nodeMap.containsKey(hash)) {
            return "Node not found for removal: " + node.getName();
        }

        nodeMap.remove(hash);
        localFiles.remove(hash);
        replicas.remove(hash);


        // Optional: remove files from fileToNodeMap that belonged to this node
        fileToNodeMap.values().removeIf(value -> value == hash);

        // Persist the updated map to disk
        saveNodeMapToDisk();

        return "Node removed: " + node.getName();
    }

    // Register a file to a node (owner) + replica to node based on file hash
    @PostMapping("/registerFile")
    public String registerFile(@RequestParam String filename, @RequestParam String nodeName) {
        int nodeHash = hashNodeName(nodeName);

        if (!nodeMap.containsKey(nodeHash)) {
            return "Node not registered: " + nodeName;
        }

        fileToNodeMap.put(filename, nodeHash);
        localFiles.computeIfAbsent(nodeHash, k -> new ArrayList<>()).add(filename);

        int fileHash = hashNodeName(filename);
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
            return "Node with hash " + nodeHash + " not found.";
        }

        return "File location for '" + filename + "' → Node hash: " + nodeHash + " → IP: " + ip;
    }

    // Optional: file fallback using hash-based logic (like in slide spec)
    @GetMapping("/getFileLocationHashed")
    public String getFileLocationHashed(@RequestParam String filename) {
        int fileHash = hashNodeName(filename);
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
        int hash = hashNodeName(nodeName);
        return localFiles.getOrDefault(hash, Collections.emptyList());
    }

    // Return replicated files of a node
    @GetMapping("/getReplicas")
    public List<String> getReplicas(@RequestParam String nodeName) {
        int hash = hashNodeName(nodeName);
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

}
