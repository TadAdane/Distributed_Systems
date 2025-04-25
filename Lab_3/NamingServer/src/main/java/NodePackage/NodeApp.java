package NodePackage;

import NodePackage.networkNode.MulticastNodeReceiver;
import NodePackage.networkNode.MulticastSender;
import NodePackage.networkNode.PingServer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeApp {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Node createAndAnnounceNewNode(String name, String ipAddress, int prev, int next, String prevIp, String nextIp) {
        Node node = new Node(name, ipAddress);
        node.setPreviousID(prev);
        node.setNextID(next);

        try {
            MulticastSender.sendMulticast(name, ipAddress);
        } catch (Exception e) {
            System.err.println("Fout bij multicast:");
            e.printStackTrace();
        }

        startReceiver(node);
        startPingServer(8081); // here we activate the pinging      (every node receives a unique port)

        startPingCheckLoop(node, prevIp, nextIp, "localhost", 8080); // IP’s van buren + NamingServer

        return node;
    }

    private void startReceiver(Node node) {
        try {
            MulticastNodeReceiver receiver = new MulticastNodeReceiver(node);
            new Thread(() -> {
                try {
                    receiver.listen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Receiver kon niet gestart worden:");
            e.printStackTrace();
        }
    }

    private void startPingServer(int port) {
        PingServer pingServer = new PingServer();
        pingServer.start(port);
    }

    private boolean ping(String ip, int port) {
        try {
            URL url = new URL("http://" + ip + ":" + port + "/ping");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void reportFailureToNamingServer(Node failedNode, String namingServerIp, int namingServerPort) {
        try {
            URL url = new URL("http://" + namingServerIp + ":" + namingServerPort + "/reportFailure");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = String.format(
                    "{\"name\":\"%s\",\"ipAddress\":\"%s\"}",
                    failedNode.getName(), failedNode.getIpAddress()
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            System.out.println("reportFailure response: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPingCheckLoop(Node self, String prevIp, String nextIp, String namingServerIp, int namingServerPort) {
        scheduler.scheduleAtFixedRate(() -> {
            String[] neighbours = {prevIp, nextIp};

            for (String ip : neighbours) {
                if (!ping(ip, 8081)) { // Portnr needs to be changed if a node uses a different nr
                    System.out.println("Buur " + ip + " is NIET bereikbaar!");

                    Node failed = new Node("unknown", ip); //Name isn't needed for report of failure
                    reportFailureToNamingServer(failed, namingServerIp, namingServerPort);
                }
            }
        }, 5, 10, TimeUnit.SECONDS); // every 10 sec, with 5 sec delay
    }
}
