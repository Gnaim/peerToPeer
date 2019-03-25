package peer.protocol;

import peer.folder.File;
import peer.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;

public interface OutputProtocol {

    void commandeMessage(int id, String message) throws IOException;
    /**
     * send port
     * example :[2,3333]
     * @param id :  ID = 2
     * @param port
     */
    void commandeDeclarePort(int id, int port) throws IOException;

    /**
     * To request the list of all the peers
     * example :[3]
     * @param id :  ID = 3
     */
    void commandePeerList(int id) throws IOException;

    /**
     * example : [ 4, INT, [ INT, STRING ]* ]
     * @param id :  ID = 4
     */
    void commandeSendPeerList(int id, ArrayList<Peer> peers) throws IOException;

    /**
     * To request the list of all the files
     * example :[5]
     * @param id :  ID = 5
     */
    void commandeFileList(int id) throws IOException;

    /**
     * To request the list of all the files
     * example :[ 6, INT, [ STRING, LONG ]* ]
     * @param id :  ID = 6
     */
    void commandeSendFileList(int id, ArrayList<File> files) throws IOException;

    /**
     * To request a fragment of file
     * example : [ 7, file.txt, 100, 0, 100 ]
     * @param id : ID = 7
     * @param fileName
     * @param sizeFile
     * @param pointer
     * @param fragment
     */
    void commandeFileFragment(int id, String fileName, long sizeFile, long pointer, int fragment) throws IOException;


}

