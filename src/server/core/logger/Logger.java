package server.core.logger;

public interface Logger {
    void serverStarting(int port);

    void serverClosing();

    void systemMessage(String message);

    void clientSentCommand(String ip, String command);

    void clientConnected(String ip);


}
