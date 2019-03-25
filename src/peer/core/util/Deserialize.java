package peer.core.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import peer.core.folder.File;
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
    public ArrayList<Peer> peerList(int id) {
        int paire = this.getInt();
        ArrayList<Peer> peers = new ArrayList<>();
        for (int i = 0; i < paire; i++) {
            int port = this.getInt();
            String address = this.getString();
            peers.add(new Peer(port, address));
        }
        byteBuffer.clear();
        return peers;
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
    public void fileFragment(int id) {
        String fileName = getString();
        long sizeFile = getLong();
        long pointer = getLong();
        int fragment = getInt();
        int limit = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position() + fragment);
        String message = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(limit);
        System.out.println(message);
        //this.client.getFolder().ceateFile(fileName, message);
        //todo add file save
        this.byteBuffer.clear();
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
