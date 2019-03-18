import server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Thread(
                new Server(3330)
        ).start();
    }
}
