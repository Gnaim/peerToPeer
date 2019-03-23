import client.Client;
import client.folder.Folder;
import server.Server;

import java.io.IOException;

public class LocalTest {

    public static void main(String[] args) throws IOException {

         new Thread(
            //   new Client("176-132-200-49.abo.bbox.fr",8080)
            //  new Client("prog-reseau-m1.lacl.fr", 5486)
                new Client("localhost", 2222)
        ).start();
    }
}
