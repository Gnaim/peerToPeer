import client.Client;
import server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        /*new Thread(
                new Server(3330)
        ).start();*/
        new Thread(
                new Client("prog-reseau-m1.lacl.fr",5486)
        ).start();
    }
}
