package peer.core.logger;

import peer.core.folder.File;
import peer.core.peer.Peer;

import java.util.ArrayList;

public interface Logger {

    void message(int id, String message);

    void declarePort(int id, int port);

    void listLength(int size);

    void fileLength(int size);

    void listPeer(int id, ArrayList<Peer> peers);

    void listFile(int id, ArrayList<File> files);

    void file(int id, String fileName, long sizeFile, long pointer, int fragment);

    void error(int id);

    void command(int id);

    void separator();
    
}
