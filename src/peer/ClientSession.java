package peer;

import peer.core.handler.Handler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ClientSession implements ClientPeer {
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private Handler handler;

    ClientSession(SelectionKey selectionKey, SocketChannel socketChannel) throws Throwable {
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) socketChannel.configureBlocking(false);
        this.byteBuffer = ByteBuffer.allocateDirect(64);
        this.handler= new Handler(this);

    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    void disconnect() {
        Server.clientMap.remove(selectionKey);
        try {
            if (selectionKey != null)
                selectionKey.cancel();

            if (socketChannel == null)
                return;

            System.out.println("bye bye " + socketChannel.getRemoteAddress());
            socketChannel.close();
        } catch (Throwable t) { /** quietly ignore  */ }
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    private boolean init = true;
    void read() {
        try {
            int status = -1;

            try { status = socketChannel.read(byteBuffer.clear());
            } catch (Throwable t) { }

            if (status == -1)
                disconnect();

            if (status < 1)
                return; // if zero

            handler.start();
            // turn this bus right around and send it back!

        } catch (Throwable t) {
            disconnect();
            t.printStackTrace();
        }
    }

    @Override
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
