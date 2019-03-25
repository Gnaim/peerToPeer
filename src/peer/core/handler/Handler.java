package peer.core.handler;

import peer.Client;
import peer.core.folder.File;
import peer.core.logger.ILogger;
import peer.core.peer.Peer;
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

    private Client client;

    private Serialize serialize;

    private ByteBuffer byteBuffer;

    private Deserialize deserialize;

    private ILogger iClientLogger;

    public Handler(Client client) {
        this.byteBuffer = client.getByteBuffer();
        this.iClientLogger = client.getiClientLogger();
        this.serialize = new Serialize(this.byteBuffer);
        this.deserialize = new Deserialize(this.byteBuffer);
        this.client = client;
    }

    public void start() throws IOException {
        this.byteBuffer.flip();
        int id = this.byteBuffer.get();
        switch (id) {
            case COMMANDE_MESSAGE: // ID : 1

                this.iClientLogger.message(id,this.message(id));
                commandePeerList(3);
                commandeFileList(5);

                break;
            case COMMANDE_DECLARE_PORT: // ID : 2
                this.iClientLogger.declarePort(id,3333); //todo add method
                break;
            case COMMANDE_PEER_LIST: // ID : 3
                this.iClientLogger.command(id);

                break;
            case COMMANDE_SEND_PEER_LIST: // ID : 4

                this.iClientLogger.listPeer(id,this.peerList(id));

                break;
            case COMMANDE_FILE_LIST: // ID : 5
                this.iClientLogger.command(id);

                break;
            case COMMANDE_SEND_FILE_LIST:// ID : 6
                this.iClientLogger.listFile(id,this.fileList(id));
                break;
            case COMMANDE_SEND_FILE_FRAGMENT:// ID : 7

                this.iClientLogger.command(id);

                break;
            case COMMANDE_FILE_FRAGMENT: // ID : 8
                this.iClientLogger.command(id);


                break;

            default: // ID : 1

                this.iClientLogger.error(id);
        }
        this.byteBuffer.clear();

    }


    @Override
    public String message(int id) {
        return this.deserialize.message(id);
    }

    @Override
    public ArrayList<Peer> peerList(int id) {
        return this.deserialize.peerList(id);
    }

    @Override
    public ArrayList<File> fileList(int id) {
        return this.deserialize.fileList(id);
    }

    @Override
    public void fileFragment(int id)  {
        this.deserialize.fileFragment(id);
    }

    @Override
    public void commandeMessage(int id, String message) throws IOException {
        this.serialize.commandeMessage(id,message);
        writeOnSocketChannel();
    }

    @Override
    public void commandeDeclarePort(int id, int port) throws IOException {
        this.serialize.commandeDeclarePort(id,port);
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
    public void commandeFileFragment(int id, String fileName, long sizeFile, long pointer, int fragment) throws IOException {
        this.serialize.commandeFileFragment(id,fileName,sizeFile,pointer,fragment);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendFileList(int id, ArrayList<File> files) throws IOException {
        this.commandeSendFileList(id,files);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendPeerList(int id, ArrayList<Peer> peers) throws IOException {
        this.serialize.commandeSendPeerList(id,peers);
        writeOnSocketChannel();
    }
    private void writeOnSocketChannel() throws IOException {
        this.client.getSocketChannel().write(this.byteBuffer);
        this.byteBuffer.clear();
    }

}
