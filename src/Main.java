

import peer.Client;
import peer.Server;


public class Main {


    public static void main(String[] args) throws Throwable {

        new Thread(
                new Client("prog-reseau-m1.lacl.fr",5486)
        ).start();
        ;
    }
}
