

import peer.Client;
import peer.Server;


public class Main {


    public static void main(String[] args) throws Throwable {
        new Thread(
                new Server()
        ).start();


    }
}
