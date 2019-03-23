import client.Client;
import server.Server;

import java.io.IOException;

public class LocalServer {

    public static void main(String[] args) throws IOException {
    	new Thread(
               new Server(2222,"MyLocalServer")
        ).start();

    }
}
