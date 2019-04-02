

import peer.Server;

import java.net.InetSocketAddress;

public class Main {


    public static void main(String[] args) throws Throwable {
        try {
            new Server(new InetSocketAddress("localhost", 1337));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

       /* new Thread(
                //   new Client("176-132-200-49.abo.bbox.fr",8080)
                new Client("prog-reseau-m1.lacl.fr", 5486)
        ).start();
*/


    }
}
