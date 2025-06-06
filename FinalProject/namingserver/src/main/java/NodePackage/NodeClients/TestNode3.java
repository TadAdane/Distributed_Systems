package NodePackage.NodeClients;


import NodePackage.Node;
import NodePackage.Node;
import NodePackage.NodeApp;

import java.io.File;
import java.net.URL;

public class TestNode3 {
    public static void main(String[] args) throws InterruptedException{
        NodeApp app = new NodeApp();
        //  app.createAndAnnounceNewNode("TestNode2", 2050);
//        URL resource = TestNode1.class.getClassLoader().getResource("files2");
//        if (resource == null) {
//            System.err.println("Directory not found in resources");
//            return;
//        }
//        File dir = new File(resource.getFile());
        String dirPathLocal = "/home/tadiwos/6th_Semester/Distributed_Systems/Labs/Lab_7/GUI_Lab_7/namingserver/src/main/resources/files3/local";
        String dirPathReplica = "/home/tadiwos/6th_Semester/Distributed_Systems/Labs/Lab_7/GUI_Lab_7/namingserver/src/main/resources/files3/replica";
        Node node = app.createAndAnnounceNewNode("Tadiwos", 2570, dirPathLocal, dirPathReplica);
     //   Node node = app.createAndAnnounceNewNode("Tadiwos", 4060, "C:\\Users\\edwar\\Universiteit\\6-DIST\\Practicum\\Lab5\\Replication\\namingserver\\src\\main\\resources\\files3\\local", "C:\\Users\\edwar\\Universiteit\\6-DIST\\Practicum\\Lab5\\Replication\\namingserver\\src\\main\\resources\\files3\\replica");
     //   Node node = app.createAndAnnounceNewNode("Edward", 2570, "C:\\Users\\chris\\IdeaProjects\\NServer\\namingserver\\src\\main\\resources\\files3\\local", "C:\\Users\\chris\\IdeaProjects\\NServer\\namingserver\\src\\main\\resources\\files3\\replicas");
        //Node node = app.createAndAnnounceNewNode("Tadiwos", 4060, "C:\\Users\\edwar\\Universiteit\\6-DIST\\Practicum\\Lab5\\Replication\\namingserver\\src\\main\\resources\\files3\\local", "C:\\Users\\edwar\\Universiteit\\6-DIST\\Practicum\\Lab5\\Replication\\namingserver\\src\\main\\resources\\files3\\replica");
     //   Node node = app.createAndAnnounceNewNode("Edward", 4060, "C:\\3de_jaar\\3_Distributed_Systeem\\Lab5\\namingserver\\src\\main\\resources\\files3\\local", "C:\\3de_jaar\\3_Distributed_Systeem\\Lab5\\namingserver\\src\\main\\resources\\files3\\replicas");

        System.out.println(node.getLocalFileNames());
//        Node node = app.createAndAnnounceNewNode("TestNode2", 2000);

        Thread.sleep(45000);

        NodeApp.shutdownGracefully(node);
    }
}
