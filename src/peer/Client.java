package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import peer.core.handler.Handler;
import peer.core.logger.ILogger;
import peer.core.util.RepeatKeyboard;


public class Client implements Runnable {

    private int serverPort;
    private Handler handler;
    private String serverAddress;
    private ByteBuffer byteBuffer;
    private SocketChannel socketChannel;

    public Client(String serverAddress, int serverPort) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.byteBuffer = ByteBuffer.allocate(70536);
        this.handler= new Handler(this);
    }

    public SocketChannel getSocketChannel() { return socketChannel; }

    public ByteBuffer getByteBuffer() { return byteBuffer; }

    public void run() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
            this.socketChannel = SocketChannel.open();
            this.socketChannel.connect(socketAddress);
            new Thread(new RepeatKeyboard(this.socketChannel)).start();
            while (this.socketChannel.isConnected()) {
                if(this.socketChannel.read(this.byteBuffer) > 0){
                    this.handler.start();
                //new RepeatKeyboard(this.socketChannel).run();
                }else
                    this.socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
