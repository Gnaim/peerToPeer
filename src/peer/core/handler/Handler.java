package peer.core.handler;

import peer.Client;
import peer.Server;
import peer.core.util.client.session.ClientSession;
import peer.core.gui.clientGui.ClientGui;
import peer.core.util.client.peer.ClientPeer;
import peer.core.util.folder.File;
import peer.core.util.folder.Folder;
import peer.core.util.folder.Fragment;
import peer.core.logger.ILogger;
import peer.core.util.peer.Peer;
import peer.core.protocol.InputProtocol;
import peer.core.protocol.OutputProtocol;
import peer.core.util.Deserialize;
import peer.core.util.Serialize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Handler implements InputProtocol, OutputProtocol {

    public static final int COMMANDE_MESSAGE = 1;

    public static final int COMMANDE_DECLARE_PORT = 2;

    public static final int COMMANDE_PEER_LIST = 3;

    public static final int COMMANDE_SEND_PEER_LIST = 4;

    public static final int COMMANDE_FILE_LIST = 5;

    public static final int COMMANDE_SEND_FILE_LIST = 6;

    public static final int COMMANDE_SEND_FILE_FRAGMENT = 7;

    public static final int COMMANDE_FILE_FRAGMENT = 8;

    private ClientPeer client;

    private Serialize serialize;

    private ByteBuffer byteBuffer;

    private Deserialize deserialize;

    private ILogger iLogger;

    private ClientGui clientGui;

    private Folder folder;

    public Handler(Client client) {
        this.byteBuffer = client.getByteBuffer();
        this.iLogger = new ILogger();
        this.serialize = new Serialize(this.byteBuffer);
        this.deserialize = new Deserialize(this.byteBuffer);
        this.client = client;
        this.folder = new Folder();
        this.clientGui = new ClientGui(this);
    }

    public Handler(ClientSession client) {
        this.byteBuffer = client.getByteBuffer();
        this.iLogger = new ILogger();
        this.serialize = new Serialize(this.byteBuffer);
        this.deserialize = new Deserialize(this.byteBuffer);
        this.folder = new Folder();
        this.client = client;
    }


    public void start() throws IOException {
        this.byteBuffer.flip();
            int id = this.byteBuffer.get();
            switch (id) {
                case COMMANDE_MESSAGE: // ID : 1
                    String message = this.message(id);
                    if(clientGui != null)
                        this.clientGui.log("[" + id + ", '" + message + "']");
                    this.iLogger.command(id);
                    this.iLogger.message(id, message);
                    break;
                case COMMANDE_DECLARE_PORT: // ID : 2
                    this.iLogger.command(id);
                    int declarePort = declarePort(id);
                    if(clientGui != null)
                        this.clientGui.declarePort(id,declarePort);
                    this.iLogger.declarePort(id, declarePort);
                    break;
                case COMMANDE_PEER_LIST: // ID : 3
                    this.iLogger.command(id);
                    if(clientGui != null)
                        this.clientGui.command(id);
                    this.commandeSendPeerList(4, Server.peers);
                    break;
                case COMMANDE_SEND_PEER_LIST: // ID : 4
                    this.iLogger.command(id);
                    var peersList = this.peerList(id);
                    this.iLogger.listPeer(id, peersList);
                    if(clientGui != null)
                        this.clientGui.listPeer(id,peersList);
                        break;
                case COMMANDE_FILE_LIST: // ID : 5
                    this.iLogger.command(id);
                    if(clientGui != null)
                        this.clientGui.command(id);
                    this.commandeSendFileList(6, folder.listFilesForFolder());
                    break;
                case COMMANDE_SEND_FILE_LIST:// ID : 6
                    this.iLogger.command(id);
                    var filelist = this.fileList(id);
                    if(clientGui != null)
                        this.clientGui.listFile(id,filelist);
                    this.iLogger.listFile(id, filelist);
                    break;
                case COMMANDE_SEND_FILE_FRAGMENT:// ID : 7
                    this.iLogger.command(id);
                    if(clientGui != null)
                        this.clientGui.command(id);
                    this.commandeSendFileFragment(8,this.fileItem(id));
                    break;
                case COMMANDE_FILE_FRAGMENT: // ID : 8
                    this.fileFragment(id);
                    this.iLogger.command(id);
                    if(clientGui != null)
                        this.clientGui.command(id);
                    break;
                default: // ID : Error
                    this.commandeMessage(1,"Error : unknown id = [ " + id + " ]");
                    this.iLogger.error(id);
                    if(clientGui != null)
                        this.clientGui.error(id);

            }
        this.byteBuffer.clear();
    }

    @Override
    public String message(int id) {
        String message =this.deserialize.message(id);
        this.byteBuffer.clear();
        return message;
    }

    @Override
    public int declarePort(int id) throws IOException {
        String address = (this.client.getSocketChannel().getRemoteAddress().toString().split("/")[1]);
        address= address.split(":")[0];
        int port = this.deserialize.declarePort(id);
        Peer peer = new Peer(port, address);
        if (!Server.peers.contains(peer))
            Server.peers.add(peer);
        this.byteBuffer.clear();
        return port;
    }

    @Override
    public void askListPeer(int id) {
        this.deserialize.askListPeer(id);
        this.byteBuffer.clear();
    }

    @Override
    public ArrayList<Peer> peerList(int id) {
        ArrayList<Peer> peers = this.deserialize.peerList(id);
        for (Peer p : peers) {
            if (!Server.peers.contains(p)) {
                Server.peers.add(p);
            }
        }
        this.byteBuffer.clear();
        return peers;
    }

    @Override
    public void askListFile(int id) {
        this.deserialize.askListFile(id);
        this.byteBuffer.clear();
    }

    @Override
    public ArrayList<File> fileList(int id) {
        ArrayList<File> files = this.deserialize.fileList(id);
        if(this.clientGui == null){
            for (File f : files){
                if(!folder.listFilesForFolder().contains(f)){
                    try {
                        this.commandeFileFragment(7, new Fragment(f.getName(),f.getSize(),0,(int)f.getSize()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.byteBuffer.clear();
        return files;
    }

    @Override
    public Fragment fileItem(int id) {
        Fragment fragment = this.deserialize.fileItem(id);
        this.byteBuffer.clear();
        return fragment;

    }

    @Override
    public void fileFragment(int id) throws IOException {
        this.deserialize.fileFragment(id);
        this.byteBuffer.clear();
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

        if(fragment.getSizeFile() <= Client.BYTEBYFFER_SIZE){
            Fragment f = new Fragment(fragment.getFileName(),fragment.getSizeFile(),0,(int)fragment.getSizeFile());
            this.serialize.commandeFileFragment(id, f);
            writeOnSocketChannel();
        }else{
            int round = (int)fragment.getSizeFile()/Client.BYTEBYFFER_SIZE;
            int mod = (int)fragment.getSizeFile() % Client.BYTEBYFFER_SIZE;
            int i ;
            for ( i = 0; i <= round-1; i++){
                Fragment f = new Fragment(fragment.getFileName(),fragment.getSizeFile(),i*Client.BYTEBYFFER_SIZE,Client.BYTEBYFFER_SIZE);
                this.serialize.commandeFileFragment(id, f);
                writeOnSocketChannel();
            }
            if (mod != 0){
                Fragment f = new Fragment(fragment.getFileName(),fragment.getSizeFile(),i*Client.BYTEBYFFER_SIZE,mod);
                this.serialize.commandeFileFragment(id, f);
                writeOnSocketChannel();
            }
        }

    }

    @Override
    public void commandeSendFileFragment(int id, Fragment fragment) throws IOException {
        System.out.println(fragment);
        if(fragment.getFileName().equals(".DS_Store")){
            this.byteBuffer.clear();
            return;
        }
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


    private void writeOnSocketChannel() throws IOException {
        this.client.getSocketChannel().write(this.byteBuffer);
        this.byteBuffer.clear();
    }

}
