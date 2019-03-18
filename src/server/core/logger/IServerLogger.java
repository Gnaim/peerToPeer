package server.core.logger;

public class IServerLogger implements Logger {

    @Override
    public void serverStarting(int port) {
        System.out.println("Server on port " + port + " is strating ...");
    }

    @Override
    public void serverClosing() {
        System.out.println("Server Closing ...");
    }

    @Override
    public void clientSentCommand(String ip, String name, String command) {
        System.out.println("client: [" + ip + "] " + name + ", sent : {" + command + "}");
    }

    @Override
    public void clientConnected(String ip) {
        System.out.println("client: [" + ip.split("/")[1] + "] connected");
    }
}
