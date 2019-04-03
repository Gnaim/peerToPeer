package peer.core.util.client.session;

import peer.Server;
import peer.core.handler.Handler;
import peer.core.util.client.peer.ClientPeer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientSession implements ClientPeer, Runnable {
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private Handler handler;

    public ClientSession(SelectionKey selectionKey, SocketChannel socketChannel) throws Throwable {
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) socketChannel.configureBlocking(false);
        this.byteBuffer = ByteBuffer.allocateDirect(64);
        this.handler = new Handler(this);
        this.welcomeMessage();
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
        } catch (Throwable t) { /** quietly ignore  */}
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
    public  void welcomeMessage() throws IOException {
       String remoteAdress = this.getSocketChannel().getRemoteAddress().toString();
       this.handler.commandeMessage(1,"▶Reference Implementation🚀 ");
       this.handler.commandeMessage(1,"you are connected from :"+remoteAdress);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                this.handler.commandePeerList(3);
            } catch (IOException e) {
            }
        }, 0, 20000, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                this.handler.commandeFileList(5);
            } catch (IOException e) {

            }
        }, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    @Override
    public void run() {
        try {
            int status = -1;

            try {
                status = socketChannel.read(byteBuffer.clear());
            } catch (Throwable t) {
            }

            if (status == -1)
                disconnect();

            if (status < 1)
                return; // if zero

            handler.start();


        } catch (Throwable t) {
            disconnect();
            t.printStackTrace();
        }
    }
}

