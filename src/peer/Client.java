package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import peer.folder.Folder;
import peer.logger.IClientLogger;
import peer.util.Deserialisation;
import peer.util.Serializable;
import peer.peer.Peer;


public class Client implements Runnable {
    boolean a = true;
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
        this.serializable = new Serializable(this.byteBuffer);
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

    public Folder getFolder() {
        return folder;
    }

    public void run() {
        try {
            this.socketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
            this.socketChannel = SocketChannel.open();
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
            this.byteBuffer.clear();
        }
    }

    public void commandSendListPeer() throws IOException {
        this.serializable.commandPeerList(this.peers);
       this.writeOnSocketChannel();
    }

    public void commandSendFileList() throws IOException {
        this.serializable.commandFileList(this.folder.listFilesForFolder());
        this.writeOnSocketChannel();
    }

    public void commandGetList() throws IOException {
        this.serializable.commandID(3);
        this.writeOnSocketChannel();
    }

    public void commandGetFileList() throws IOException {
        this.serializable.commandID(5);
        this.writeOnSocketChannel();
    }
    
    public void commandSendPort(int port)throws IOException {
    	this.serializable.sendPort(port);
    	this.writeOnSocketChannel();
    }
    
    public void commandGetFile (String fileName, long sizeFile, long pointer, int fragment) throws IOException {
    	this.iClientLogger.file(7, fileName, sizeFile, pointer, fragment);
    	this.serializable.askForFile(fileName, sizeFile, pointer, fragment);
        this.writeOnSocketChannel();
    }

    private void writeOnSocketChannel() throws IOException {
        this.socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }
    
}
