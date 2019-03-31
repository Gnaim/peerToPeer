package peer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<>();
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private SelectionKey selectionKey;

    public Server(InetSocketAddress listenAddress) throws Throwable {
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.selectionKey = this.serverChannel.register(this.selector = Selector.open(), SelectionKey.OP_ACCEPT);
        this.serverChannel.bind(listenAddress);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                loop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    void loop() throws Throwable {
        selector.selectNow();

        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid())
                    continue;

                if (key == selectionKey) {
                    SocketChannel acceptedChannel = serverChannel.accept();

                    if (acceptedChannel == null)
                        continue;

                    acceptedChannel.configureBlocking(false);
                    SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                    clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));

                    System.out.println("New client ip=" + acceptedChannel.getRemoteAddress() + ", total clients=" + Server.clientMap.size());
                    List keys = new ArrayList(clientMap.keySet());


                        clientMap.get(keys.get(0)).getByteBuffer().clear().putInt(1).putInt(0).flip();
                        clientMap.get(keys.get(0)).getSocketChannel().write(clientMap.get(keys.get(0)).getByteBuffer());
                        clientMap.get(keys.get(0)).getByteBuffer().clear();
                }

                if (key.isReadable()) {
                    ClientSession sesh = clientMap.get(key);

                    if (sesh == null)
                        continue;

                    sesh.read();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        selector.selectedKeys().clear();
    }
}
