package client.logger;

public interface Logger {

    void connected(int id, String message);

    void listLength(int size);

    void fileLength(int size);

    void listPeer(int id, int port, String adr);

    void listFile(int id, String nameFile, long sizeFile);

    void error(int id);
}
