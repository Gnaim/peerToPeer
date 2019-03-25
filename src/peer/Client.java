package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import peer.handler.ClientHandler;
import peer.logger.IClientLogger;


public class Client implements Runnable {
    private int serverPort;
    private String serverAddress;
    private SocketChannel socketChannel;
    private SocketAddress socketAddress;
    private ByteBuffer byteBuffer;
    private IClientLogger iClientLogger;
    private ClientHandler handler;


    public Client(String serverAddress, int serverPort) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.byteBuffer = ByteBuffer.allocate(9000);
        this.iClientLogger = new IClientLogger();
        this.handler= new ClientHandler(this);
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
            this.handler.start();
            this.byteBuffer.clear();
        }
    }

    
}
