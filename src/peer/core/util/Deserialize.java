package peer.core.util;

import peer.core.util.folder.File;
import peer.core.util.folder.Folder;
import peer.core.util.folder.Fragment;
import peer.core.handler.Handler;
import peer.core.util.peer.Peer;
import peer.core.protocol.InputProtocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Deserialize implements InputProtocol {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;

    public Deserialize(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;

    }

    public String message(int id) {
        String message = this.getString();
        this.byteBuffer.clear();

        return message;

    }

    @Override
    public int declarePort(int id) {
        int port = this.getInt();
        this.byteBuffer.clear();
        return port;
    }

    @Override
    public void askListPeer(int id) {
        this.byteBuffer.clear();
    }

    @Override
    public ArrayList<Peer> peerList(int id) {
        int paire = this.getInt();
        ArrayList<Peer> peers = new ArrayList<>();
        for (int i = 0; i < paire; i++) {
            int port = this.getInt();
            String address = this.getString();
            peers.add(new Peer(port, address));
        }
        peers.add(new Peer(5486, "prog-reseau-m1.lacl.fr"));
        byteBuffer.clear();
        return peers;
    }

    @Override
    public void askListFile(int id) {
        this.byteBuffer.clear();
    }

    @Override
    public ArrayList<File> fileList(int id) {
        int nbFile = this.getInt();
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < nbFile; i++) {
            String fileName = this.getString();
            long fileSize = this.byteBuffer.getLong();
            files.add(new File(fileName, fileSize));
        }
        this.byteBuffer.clear().flip();
        return files;
    }

    @Override
    public void fileFragment(int id) throws IOException {
        System.out.println("test");
        String fileName = getString();
        long sizeFile = getLong();
        long pointer = getLong();
        int fragment = getInt();
        int limit = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position() + fragment);
        String contents = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(limit);
        Folder.ceateFile(fileName, contents);
        this.byteBuffer.clear();
        //just fot test
    }

    @Override
    public Fragment fileItem(int id) {
        String fileName = getString();
        long sizeFile = getLong();
        long pointer = getLong();
        int fragment = getInt();
        this.byteBuffer.clear();
        return new Fragment(fileName, sizeFile, pointer, fragment);
    }

    private int getInt() {
        return this.byteBuffer.getInt();
    }

    private String getString() {
        int stringSize = this.getInt();
        int limit = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position() + stringSize);
        String message = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(limit);
        return message;
    }

    private long getLong() {
        return this.byteBuffer.getLong();
    }

}
