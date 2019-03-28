package peer.core.protocol;


import peer.core.folder.File;
import peer.core.folder.Fragment;
import peer.core.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;

public interface InputProtocol {

    /**
     * @param id : ID = 1
     * @return
     */
    String message(int id) throws IOException;

    /**
     * @param id : ID = 2
     * @return
     */
    int declarePort(int id) throws IOException;

    /**
     * @param id : ID = 3
     * @return
     */
    void askListPeer(int id);

    /**
     * To request the list of all the peers
     * example : [ 4, INT, [ INT, STRING ]* ]
     *
     * @param id :  ID = 4
     */
    ArrayList<Peer> peerList(int id);

    /**
     * @param id : ID = 5
     */
    void askListFile(int id);

    /**
     * To request the list of all the files
     * example :[ 6, INT, [ STRING, LONG ]* ]
     *
     * @param id :  ID = 6
     */
    ArrayList<File> fileList(int id);

    /**
     * @param id
     * @return
     */
    Fragment fileItem(int id);

    /**
     * example : [ 8, STRING, LONG, LONG, INT, BLOB ] R
     *
     * @param id : ID = 8
     */
    void fileFragment(int id) throws IOException;

}
