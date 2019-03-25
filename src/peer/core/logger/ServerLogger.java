package peer.core.logger;

public interface ServerLogger {
    void serverStarting(int port);

    void serverClosing();

    void systemMessage(String message);

    void clientSentCommand(String ip, String command);

    void clientConnected(String ip);

	void peerAdded(String adressIp, String port);

	void error(int id);

	void sendPeersList(String ip);

	void sendFilesList(String ip);

	void separator();



}
