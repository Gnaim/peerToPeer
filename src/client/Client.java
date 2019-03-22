package client;

import client.folder.Folder;
import client.logger.IClientLogger;
import client.peer.Peer;
import client.util.Deserialisation;
import client.util.Serializable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class Client implements Runnable {
    private int serverPort;
    private String serverAddress;
    private SocketChannel socketChannel;
    private SocketAddress socketAddress;
    private ByteBuffer byteBuffer;
    private Deserialisation deserialisation;
    private IClientLogger iClientLogger;
    private Serializable serializable;
    private ArrayList<Peer> peers;
    private Folder folder;


    public Client(String serverAddress, int serverPort) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.byteBuffer = ByteBuffer.allocate(9000);
        this.iClientLogger = new IClientLogger();
        this.deserialisation = new Deserialisation(this);
        this.serializable = new Serializable(this);
        this.peers = new ArrayList<>();
        this.folder = new Folder();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public IClientLogger getiClientLogger() {
        return iClientLogger;
    }

    public ArrayList<Peer> getPeers() {
        return peers;
    }

    public void run() {
        try {
            this.socketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
            this.socketChannel = this.socketChannel.open();
            this.socketChannel.connect(this.socketAddress);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() throws IOException {
        while (this.socketChannel.isConnected()) {
            this.socketChannel.read(this.byteBuffer);
            this.deserialisation.start();
        }
    }

    public void commandSendListPeer(){
        this.serializable.commandList(this.peers);
    }

    public void commandSendFileList(){
        this.serializable.commandFile(this.folder.listFilesForFolder());
    }

    public void commandGetList()throws IOException{
        this.serializable.commandID(3);
        this.socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }

    public void commandGetFileList() throws IOException {
        this.serializable.commandID(5);
        this.socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }
}
