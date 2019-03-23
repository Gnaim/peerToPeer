package client.logger;

public interface Logger {

    void connected(int id, String message);

    void listLength(int size);

    void fileLength(int size);

    void listPeer(int id, int port, String adr);

    void listFile(int id, String nameFile, long sizeFile);

    void file(int id, String fileName, long sizeFile, long pointer, int fragment);

    void error(int id);

    void command(int id);

    void separator();
}
