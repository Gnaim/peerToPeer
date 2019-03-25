package peer.core.logger;



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
	public void separator() {
        System.out.println("-------------------------------");
		
	}



}
