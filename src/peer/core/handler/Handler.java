package peer.core.handler;

import peer.Client;
import peer.core.folder.File;
import peer.core.folder.Folder;
import peer.core.folder.Fragment;
import peer.core.logger.ILogger;
import peer.core.peer.Peer;
import peer.core.protocol.InputProtocol;
import peer.core.protocol.OutputProtocol;
import peer.core.util.Deserialize;
import peer.core.util.Serialize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static peer.core.util.Serialize.CHARSET;

public class Handler implements InputProtocol, OutputProtocol {

    public static boolean getFollowingData = true;

    public static final int COMMANDE_MESSAGE = 1;

    public static final int COMMANDE_DECLARE_PORT = 2;

    public static final int COMMANDE_PEER_LIST = 3;

    public static final int COMMANDE_SEND_PEER_LIST = 4;

    public static final int COMMANDE_FILE_LIST = 5;

    public static final int COMMANDE_SEND_FILE_LIST = 6;

    public static final int COMMANDE_SEND_FILE_FRAGMENT = 7;

    public static final int COMMANDE_FILE_FRAGMENT = 8;

    private Client client;

    private Serialize serialize;

    private ByteBuffer byteBuffer;

    private Deserialize deserialize;

    private ILogger iClientLogger;

    private ArrayList<Peer> peers;


    private Folder folder;

    public Handler(Client client) {
        this.byteBuffer = client.getByteBuffer();
        this.iClientLogger = new ILogger();
        this.serialize = new Serialize(this.byteBuffer);
        this.deserialize = new Deserialize(this.byteBuffer);
        this.client = client;
        this.folder = new Folder();
        this.peers = new ArrayList<>();
    }

    public void start() throws IOException {
        this.byteBuffer.flip();
        if (getFollowingData){

            int id = this.byteBuffer.get();
            switch (id) {
                case COMMANDE_MESSAGE: // ID : 1

                    this.iClientLogger.message(id, this.message(id));
                    commandeFileList(5);
                    commandePeerList(3);
                    //this.commandeFileFragment(7, new Fragment("xso.txt", 46125, 0, 46125));

                    break;
                case COMMANDE_DECLARE_PORT: // ID : 2
                    this.iClientLogger.declarePort(id, declarePort(id)); //todo add method
                    break;
                case COMMANDE_PEER_LIST: // ID : 3

                    this.iClientLogger.command(id);

                    break;
                case COMMANDE_SEND_PEER_LIST: // ID : 4

                    this.iClientLogger.listPeer(id, this.peerList(id));

                    break;
                case COMMANDE_FILE_LIST: // ID : 5
                    this.iClientLogger.command(id);
                    this.commandeSendFileList(6, folder.listFilesForFolder());

                    break;
                case COMMANDE_SEND_FILE_LIST:// ID : 6
                    this.iClientLogger.listFile(id, this.fileList(id));
                    break;
                case COMMANDE_SEND_FILE_FRAGMENT:// ID : 7
                    commandeSendFileFragment(8,this.fileItem(id));
                    //this.commandeFileFragment(7, new Fragment("1test.txt", 59075, 0, 59075));
                    this.iClientLogger.command(id);

                    break;
                case COMMANDE_FILE_FRAGMENT: // ID : 8
                    fileFragment(id);
                    this.iClientLogger.command(id);

                    break;

                default: // ID : 1

                    this.iClientLogger.error(id);
            }
        }else{
            this.followingData();
        }
        this.byteBuffer.clear();

    }

    @Override
    public String message(int id) throws IOException {
        var message = this.deserialize.message(id);
        return message;
    }

    @Override
    public int declarePort(int id) throws IOException {

        String address = this.client.getSocketChannel().getRemoteAddress().toString().split("/")[0];
        int port = this.deserialize.declarePort(id);
        Peer peer = new Peer(port, address);

        if (!this.peers.contains(peer))
            this.peers.add(peer);

        return port;
    }

    @Override
    public void askListPeer(int id) {
        this.deserialize.askListPeer(id);
    }

    @Override
    public ArrayList<Peer> peerList(int id) {
        ArrayList<Peer> peers = this.deserialize.peerList(id);
        for (Peer p : peers) {
            if (!this.peers.contains(p)) {
                this.peers.add(p);
            }
        }
        return peers;
    }

    @Override
    public void askListFile(int id) {
        this.deserialize.askListFile(id);
    }

    @Override
    public ArrayList<File> fileList(int id) {
        return this.deserialize.fileList(id);
    }

    @Override
    public Fragment fileItem(int id) {
        return this.deserialize.fileItem(id);
    }

    @Override
    public void fileFragment(int id) throws IOException {
        this.deserialize.fileFragment(id);
    }

    @Override
    public void commandeMessage(int id, String message) throws IOException {
        this.serialize.commandeMessage(id, message);
        writeOnSocketChannel();
    }

    @Override
    public void commandeDeclarePort(int id, int port) throws IOException {
        this.serialize.commandeDeclarePort(id, port);
        writeOnSocketChannel();
    }

    @Override
    public void commandePeerList(int id) throws IOException {
        this.serialize.commandePeerList(id);
        writeOnSocketChannel();
    }

    @Override
    public void commandeFileList(int id) throws IOException {
        this.serialize.commandeFileList(id);
        writeOnSocketChannel();
    }

    @Override
    public void commandeFileFragment(int id, Fragment fragment) throws IOException {
        this.serialize.commandeFileFragment(id, fragment);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendFileFragment(int id, Fragment fragment) throws IOException {
        this.serialize.commandeSendFileFragment(id, fragment);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendFileList(int id, ArrayList<File> files) throws IOException {
        this.serialize.commandeSendFileList(id, files);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendPeerList(int id, ArrayList<Peer> peers) throws IOException {
        this.serialize.commandeSendPeerList(id, peers);
        writeOnSocketChannel();
    }
    void followingData() throws IOException {
        String contents = CHARSET.decode(this.byteBuffer).toString();
        Folder.ceateFile("xso.txt", contents);
        this.byteBuffer.clear();
    }
    private void writeOnSocketChannel() throws IOException {
        this.client.getSocketChannel().write(this.byteBuffer);
        this.byteBuffer.clear();
    }

}
