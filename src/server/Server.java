package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Server implements Runnable{

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer byteBuffer;
    private int port;

    /**
     *
     * @param port
     * @throws IOException
     */
    public Server(int port) throws IOException {

        this.port = port;
        this.byteBuffer  = ByteBuffer.allocateDirect(512);

    }

    /**
     *
     * @throws IOException
     */
    public void accept() throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("new connection : " + socketChannel);
    }

    /**
     *
     * @param key
     * @throws IOException
     */
    public void repeat(SelectionKey key) throws IOException {
        System.out.println("Nouveau message ");
        SocketChannel sc = (SocketChannel) key.channel();
        sc.read(this.byteBuffer);

        this.byteBuffer.flip();
        Charset charset = Charset.forName("UTF-8");
        CharBuffer charBuffer = charset.decode(this.byteBuffer);
        System.out.println(charBuffer.toString());

        for (SelectionKey k1 : selector.keys()) {
            if (k1.isAcceptable()) {

            } else {
                SocketChannel socketChannel = (SocketChannel) k1.channel();
                this.byteBuffer.rewind();
                socketChannel.write(this.byteBuffer);
            }
        }
        this.byteBuffer.clear();
    }



    @Override
    public void run() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            SocketAddress inetSocketAddress = new InetSocketAddress(port);
            this.serverSocketChannel.bind(inetSocketAddress);
            this.serverSocketChannel.configureBlocking(false);
            this.selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                for (SelectionKey k : selector.selectedKeys()) {
                    if (k.isAcceptable())
                        this.accept();
                    else
                        this.repeat(k);
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
