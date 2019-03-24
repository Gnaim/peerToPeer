
import java.io.IOException;

import peer.Server;


public class LocalServer {

    public static void main(String[] args) throws IOException {
    	new Thread(
               new Server("MyLocalServer",2222)
        ).start();

    }
}
