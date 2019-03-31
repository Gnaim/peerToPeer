
import java.io.IOException;

import peer.Client;
import peer.core.ClientGui;

public class Main {

    public static void main(String[] args) throws IOException {
    	
    	/* new Thread(
               new Server("LocalServer" ,2222)
        ).start();*/
        new Thread(
                //   new Client("176-132-200-49.abo.bbox.fr",8080)
                new Client("127.0.0.1", 1337)
        ).start();

//        new ClientGui();
    }
}
