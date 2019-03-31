import peer.Client;
import peer.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class test {
    public static void main(String[] args) throws Throwable {
        new Server(new InetSocketAddress("localhost", 1337));
    }
}
