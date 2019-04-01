package peer.core.util;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RepeatKeyboard implements Runnable{
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;


    public RepeatKeyboard(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.byteBuffer = ByteBuffer.allocate(512);
    }


    @Override
    public void run() {
        while (socketChannel.isConnected()){
                // TODO implm

        }
    }
}
