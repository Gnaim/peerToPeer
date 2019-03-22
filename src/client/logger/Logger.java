package client.logger;

public interface Logger {

    void connected(int id, String message);

    void listSize(int size);

    void list(int id, int port, String adr);

    void error(int id);
    }
