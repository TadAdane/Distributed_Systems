import NodePackage.Node;

public class NodeApp {



    public Node createExternalNode(String name , String ipAddress) {
        Node newNode = new Node(ipAddress, name);
        return newNode;
    }


    public void sendMulticast(Node multicastNode) {

    }

}
