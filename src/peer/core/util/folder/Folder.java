package peer.core.util.folder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.ArrayList;

public class Folder {

    public static final String PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/peer/core/src";
    private File folder;

    public Folder() {
        this.folder = new File(PATH);
    }

    public static void ceateFile(String nameFile, String c) throws IOException {
        FileOutputStream fos = new FileOutputStream(PATH + "/" + nameFile,true);
        fos.write(c.getBytes());
        fos.flush();
        fos.close();
    }

    public ArrayList<peer.core.util.folder.File> listFilesForFolder() {
        ArrayList<peer.core.util.folder.File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            files.add(new peer.core.util.folder.File(fileEntry.getName(), fileEntry.length()));
        }
        return files;
    }

    public Blob getFile(String file) {
        File files = new File(PATH + file);
        return null;
    }


}
