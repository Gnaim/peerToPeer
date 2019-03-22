package client.util;

import client.Client;
import client.File;
import client.logger.IClientLogger;
import client.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class Serializable {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;
    private IClientLogger iClientLogger;
    private SocketChannel socketChannel ;

    public Serializable(Client client) {
        this.byteBuffer = client.getByteBuffer();
        this.iClientLogger = client.getiClientLogger();
        this.socketChannel = client.getSocketChannel();

    }

    public void commandID(int ID) throws IOException {
        this.byteBuffer.clear();
        this.byteBuffer.put((byte) ID);
        this.byteBuffer.flip();
    }

    public void commandList(ArrayList<Peer> peer){
        this.byteBuffer.clear();
        this.byteBuffer.put((byte) peer.size());
        for(Peer p : peer){
            this.byteBuffer.putInt((byte)p.getPort());
            ByteBuffer buffer1 = CHARSET.encode(p.getAddress());
            this.byteBuffer.putInt(buffer1.remaining());
            this.byteBuffer.put(buffer1);
        }
        this.byteBuffer.flip();
    }

    public void commandFile(ArrayList<File> files){
        this.byteBuffer.clear();
        this.byteBuffer.put((byte) files.size());
        for(File f : files){
            ByteBuffer buffer1 = CHARSET.encode(f.getName());
            this.byteBuffer.putInt(buffer1.remaining());
            this.byteBuffer.put(buffer1);
            this.byteBuffer.putLong((byte)f.getSize());
        }
        this.byteBuffer.flip();
    }


}
