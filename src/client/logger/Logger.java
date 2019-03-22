package client.logger;

public interface Logger {

    void connected(int id, String message);

    void liste(int id, int port, String adr);

    void error(int id);
    }
