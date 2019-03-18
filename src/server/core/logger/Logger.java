package server.core.logger;

public interface Logger {
    void serverStarting(int port);

    void serverClosing();

    void clientSentCommand(String ip, String name, String command);

    void clientConnected(String ip);


}
