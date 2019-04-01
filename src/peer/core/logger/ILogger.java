package peer.core.logger;

import peer.core.util.folder.File;
import peer.core.util.peer.Peer;

import java.util.ArrayList;

public class ILogger implements Logger {


    @Override
    public void message(int id, String message) {
        System.out.println("[" + id + ", '" + message + "']");
    }

    @Override
    public void declarePort(int id, int port) {
        System.out.println("[" + id + ", '" + port + "']");
    }

    @Override
    public void listLength(int size) {
        this.separator();
        System.out.println("Peer : [ " + size + " ]");
    }

    @Override
    public void fileLength(int size) {
        this.separator();
        System.out.println("File : [ " + size + " ]");
    }

    @Override
    public void listPeer(int id, ArrayList<Peer> peers) {
        System.out.print("[" + id + ", " + peers.size() + ", [ ");
        for (Peer p : peers) {
            System.out.print("[ " + p.getPort() + "," + p.getAddress() + " ]");
        }
        System.out.println(" ] ]");
    }

    @Override
    public void listFile(int id, ArrayList<File> files) {
        System.out.print("[" + id + ", " + files.size() + ", [ ");
        for (File f : files) {
            System.out.print("[ " + f.getName() + "," + f.getSize() + " ]");
        }
        System.out.println(" ] ]");
    }

    @Override
    public void file(int id, String fileName, long sizeFile, long pointer, int fragment) {
        System.out.println("ID " + id + " = [ " + fileName + ":" + sizeFile + " pointer :" + pointer + ", fragment: " + fragment + "]");
    }

    @Override
    public void error(int id) {
        this.separator();
        System.out.println("ERROR serveur send : " + id);
    }

    @Override
    public void command(int id) {
        this.separator();
        System.out.println("[ " + id + " ]");
    }

    @Override
    public void separator() {
        System.out.println("-------------------------------");
    }
}
