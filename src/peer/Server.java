package peer;

import peer.core.util.client.session.ClientSession;
import peer.core.util.peer.Peer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {

    public static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<>();
    public static ArrayList<Peer>peers = new ArrayList<>();
    public static int SERVER_PORT = 1337;
    private Selector selector;
    private SelectionKey selectionKey;
    private ServerSocketChannel serverChannel;
    private InetSocketAddress inetSocketAddress;

    public Server() throws Throwable {
        this.inetSocketAddress = new InetSocketAddress("localhost", Server.SERVER_PORT);
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.selectionKey = this.serverChannel.register(this.selector = Selector.open(), SelectionKey.OP_ACCEPT);
        this.serverChannel.bind(this.inetSocketAddress);
    }

    @Override
    public void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                loop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void loop() throws Throwable {
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
                    //Todo add init message

                }

                if (key.isReadable()) {
                    ClientSession clientSession = clientMap.get(key);

                    if (clientSession == null)
                        continue;
                    new Thread(clientSession).start();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        selector.selectedKeys().clear();
    }


}
