package client.folder;

import java.io.File;
import java.util.ArrayList;

public class Folder {

    private String path = "/home/namrane/IdeaProjects/P2P file/src/client/src";
    private File folder;

    public Folder() {
        this.folder = new File(path);
    }

    public ArrayList<client.folder.File> listFilesForFolder() {
        ArrayList<client.folder.File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            files.add(new client.folder.File(fileEntry.getName(), fileEntry.length()));
        }
        return files;
    }


}
