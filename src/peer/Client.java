package peer;

import peer.core.handler.Handler;
import peer.core.util.client.peer.ClientPeer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Client implements Runnable, ClientPeer {

    private int serverPort;
    private Handler handler;
    private String serverAddress;
    private ByteBuffer byteBuffer;
    private SocketChannel socketChannel;

    public static int BYTEBYFFER_SIZE = 700000;

    public Client(String serverAddress, int serverPort) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.byteBuffer = ByteBuffer.allocate(BYTEBYFFER_SIZE);
        this.handler = new Handler(this);
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void run() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
            this.socketChannel = SocketChannel.open();
            this.socketChannel.connect(socketAddress);
            while (this.socketChannel.isConnected()) {
                if (this.socketChannel.read(this.byteBuffer) > 0) {
                    this.handler.start();
                } else
                    this.socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
