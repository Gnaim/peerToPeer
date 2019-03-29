package peer.core.util;

import peer.core.folder.File;
import peer.core.folder.Folder;
import peer.core.folder.Fragment;
import peer.core.handler.Handler;
import peer.core.peer.Peer;
import peer.core.protocol.OutputProtocol;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Serialize implements OutputProtocol {
    public static final Charset CHARSET = Charset.forName("UTF-8");

    private ByteBuffer byteBuffer;

    public Serialize(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public void commandID(int ID) {
        this.byteBuffer
                .clear()
                .put((byte) ID)
                .flip();
    }

    public void commandPeerList(ArrayList<Peer> peer) {
        this.byteBuffer.clear()
                .put((byte) Handler.COMMANDE_SEND_PEER_LIST)
                .putInt(peer.size());
        for (Peer p : peer) {
            this.byteBuffer.putInt(p.getPort());
            ByteBuffer buffer1 = CHARSET.encode(p.getAddress());
            this.byteBuffer
                    .putInt(buffer1.remaining())
                    .put(buffer1);
        }
        this.byteBuffer.flip();
    }

    public void commandFileList(ArrayList<File> files) {
        this.byteBuffer
                .clear()
                .put((byte) Handler.COMMANDE_SEND_FILE_LIST)
                .putInt(files.size());
        for (File f : files) {
            ByteBuffer buffer1 = CHARSET.encode(f.getName());
            this.byteBuffer
                    .putInt(buffer1.remaining())
                    .put(buffer1)
                    .putLong(f.getSize());
        }
        this.byteBuffer.flip();
    }

    public void sendIp(String serverName, String ip) {
        this.byteBuffer
                .clear()
                .put((byte) 1);
        ByteBuffer buffer = CHARSET.encode("You are message from: " + serverName + ip);
        this.byteBuffer
                .putInt(buffer.remaining())
                .put(buffer)
                .flip();
    }

    @Override
    public void commandeMessage(int id, String message) {
        this.byteBuffer
                .clear()
                .put((byte) id);
        ByteBuffer buffer = CHARSET.encode(message);
        this.byteBuffer
                .putInt(buffer.remaining())
                .put(buffer)
                .flip();
    }

    @Override
    public void commandeDeclarePort(int id, int port) {
        this.byteBuffer
                .clear()
                .putInt(id)
                .put((byte) port)
                .putInt(port).flip();
    }

    @Override
    public void commandePeerList(int id) {
        this.byteBuffer
                .clear()
                .put((byte) id)
                .flip();
    }

    @Override
    public void commandeSendPeerList(int id, ArrayList<Peer> peers) {
        this.byteBuffer.clear()
                .put((byte) 4)
                .putInt(peers.size());
        for (Peer p : peers) {
            this.byteBuffer.putInt(p.getPort());
            ByteBuffer buffer1 = CHARSET.encode(p.getAddress());
            this.byteBuffer
                    .putInt(buffer1.remaining())
                    .put(buffer1);
        }
        this.byteBuffer.flip();
    }

    @Override
    public void commandeFileList(int id) {
        this.byteBuffer
                .clear()
                .put((byte) id)
                .flip();
    }

    @Override
    public void commandeSendFileList(int id, ArrayList<File> files) {
        this.byteBuffer
                .clear()
                .put((byte) 6)
                .putInt(files.size());
        for (File f : files) {
            ByteBuffer buffer1 = CHARSET.encode(f.getName());
            this.byteBuffer
                    .putInt(buffer1.remaining())
                    .put(buffer1)
                    .putLong(f.getSize());
        }
        this.byteBuffer.flip();
    }


    @Override
    public void commandeFileFragment(int id, Fragment fragment) throws IOException {
        this.byteBuffer.clear();
        this.byteBuffer.put((byte) id);
        ByteBuffer buffer1 = CHARSET.encode(fragment.getFileName());
        this.byteBuffer.putInt(buffer1.remaining());
        this.byteBuffer.put(buffer1);
        this.byteBuffer.putLong(fragment.getSizeFile());
        this.byteBuffer.putLong(fragment.getPointer());
        this.byteBuffer.putInt(fragment.getFragment());
        this.byteBuffer.flip();

    }

    @Override
    public void commandeSendFileFragment(int id, Fragment fragment) throws IOException {
        System.out.println(fragment);
        FileInputStream inputStream = new FileInputStream(Folder.PATH + "/" + fragment.getFileName());
        int sizeFragment = (int) (fragment.getSizeFile() - fragment.getPointer());
        byte[] blobFile = new byte[sizeFragment];
        ByteBuffer buffer = CHARSET.encode(fragment.getFileName());
        inputStream.read(blobFile, (int) fragment.getPointer(), fragment.getFragment()-1);

        this.byteBuffer
                .clear()
                .put((byte) id)
                .putInt(buffer.remaining())
                .put(buffer)
                .putLong(fragment.getSizeFile())
                .putLong(fragment.getPointer())
                .putInt(fragment.getFragment());

        for (byte b : blobFile){
            this.byteBuffer.put(b);
        }

        this.byteBuffer.flip();
    }


}
