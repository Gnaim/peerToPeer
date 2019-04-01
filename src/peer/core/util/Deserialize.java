package peer.core.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import peer.core.folder.File;
import peer.core.folder.Folder;
import peer.core.folder.Fragment;
import peer.core.peer.Peer;
import peer.core.protocol.InputProtocol;

public class Deserialize implements InputProtocol {
    static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;

    public Deserialize(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;

    }

    public String message(int id) {
        return this.getString();
    }

    @Override
    public int declarePort(int id) {
        return this.getInt();
    }

    @Override
    public void askListPeer(int id) {
        // todo
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
        peers.add(new Peer(5486,"prog-reseau-m1.lacl.fr"));
        byteBuffer.clear();
        return peers;
    }

    @Override
    public void askListFile(int id) {
        //todo
    }

    @Override
    public ArrayList<File> fileList(int id) {
        int nbFile = this.getInt();
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < nbFile; i++) {
            String fileName = this.getString();
            long fileSize = this.getLong();
            files.add(new File(fileName, fileSize));
        }
        return files;
    }

    @Override
    public void fileFragment(int id) throws IOException {
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
    }

    @Override
    public Fragment fileItem(int id) {
        String fileName = getString();
        long sizeFile = getLong();
        long pointer = getLong();
        int fragment = getInt();

        return new Fragment(fileName,sizeFile,pointer,fragment);
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
