
import java.net.InetSocketAddress;

import peer.Client;
import peer.Server;

public class Main {

    public static void main(String[] args) {

        try {
            new Server(new InetSocketAddress("localhost", 1337));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }
}
