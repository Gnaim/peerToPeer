package peer.core.logger;


import peer.core.peer.Peer;

import java.util.ArrayList;

public class IServerLogger implements ServerLogger  {

    @Override
    public void serverStarting(int port) {
        System.out.println("Server on port " + port + " is starting ...");
    }

    @Override
    public void serverClosing() {
        System.out.println("Server Closing ...");
    }

    @Override
    public void systemMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void clientSentCommand(String ip, String command) {
        this.separator();
        System.out.println("client: [" + ip.split("/")[1]  + "]   sent : [" + command + "]");
    }

    @Override
    public void clientConnected(String ip) {
        System.out.println("client: [" + ip.split("/")[1] + "] message");
    }
    
    @Override
    public void peerAdded(String ip,String port){
        System.out.println("Server: Peer [" + ip.split("/")[1] +"] added succesfully");
    }
    
    @Override
    public void sendPeersList(String ip){
        System.out.println("Server: Sending peers list to  [" + ip.split("/")[1] +"] ........");
    }
    
    @Override
    public void sendFilesList(String ip){
        System.out.println("Server: Sending files list to  [" + ip.split("/")[1] +"] ........");
    }
   
    @Override
    public void error(int id) {
        this.separator();
        System.out.println("ERROR serveur send : " + id);
    }
    @Override
    public void listPeer(int id, ArrayList<Peer> peers) {
        System.out.print("[" + id + ", " + peers.size() + ", [ " );
        // clientGui.getLogger().append("[" + id + ", " + peers.size() + ", [ " );
        for (Peer p : peers){
            //   clientGui.getLogger().append("[ " + p.getPort() + "," + p.getAddress() + " ]");

            System.out.print("[ " + p.getPort() + "," + p.getAddress() + " ]");
        }
        //clientGui.getLogger().append("]]"+"\n");


        System.out.println(" ] ]");
    }

	@Override
	public void separator() {
        System.out.println("-------------------------------");
		
	}



}
