package peer.handler;

import peer.Client;
import peer.folder.File;
import peer.logger.IClientLogger;
import peer.peer.Peer;
import peer.protocol.InputProtocol;
import peer.protocol.OutputProtocol;
import peer.util.Deserialisation;
import peer.util.Serializable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientHandler implements InputProtocol, OutputProtocol {

        private ByteBuffer byteBuffer;
        private IClientLogger iClientLogger;
        private Client client;
        private Serializable serializable;
        private Deserialisation deserialisation;

        public ClientHandler(Client client) {
            this.byteBuffer = client.getByteBuffer();
            this.iClientLogger = client.getiClientLogger();
            this.serializable = new Serializable(this.byteBuffer);
            this.deserialisation = new Deserialisation(this.byteBuffer);
            this.client = client;
        }

        public void start() throws IOException {
            this.byteBuffer.flip();
            int id = this.byteBuffer.get();
            switch (id) {
                case 1:
                    this.iClientLogger.command(id);
                    commandeFileList(3);
                    break;
                case 3:
                    this.iClientLogger.command(id);
                    break;
                case 4:
                    this.iClientLogger.command(id);
                    break;
                case 5:
                    this.iClientLogger.command(id);
                    break;
                case 6:
                    break;
                case 7:
                    this.iClientLogger.command(id);
                    break;
                case 8:
                    break;
                default:
                    this.iClientLogger.error(id);
            }
            this.byteBuffer.clear();

        }


    @Override
    public String message(int id) {
        return this.deserialisation.message(id);
    }

    @Override
    public ArrayList<Peer> peerList(int id) {
        return this.deserialisation.peerList(id);
    }

    @Override
    public ArrayList<File> fileList(int id) {
        return this.deserialisation.fileList(id);
    }

    @Override
    public void fileFragment(int id) throws IOException {
        this.deserialisation.fileFragment(id);
    }

    @Override
    public void commandeMessage(int id, String message) throws IOException {
        this.serializable.commandeMessage(id,message);
        writeOnSocketChannel();
    }

    @Override
    public void commandeDeclarePort(int id, int port) throws IOException {
        this.serializable.commandeDeclarePort(id,port);
        writeOnSocketChannel();
    }

    @Override
    public void commandePeerList(int id) throws IOException {
        this.serializable.commandePeerList(id);
        writeOnSocketChannel();
    }

    @Override
    public void commandeFileList(int id) throws IOException {
        this.serializable.commandeFileList(id);
        writeOnSocketChannel();
    }

    @Override
    public void commandeFileFragment(int id, String fileName, long sizeFile, long pointer, int fragment) throws IOException {
        this.serializable.commandeFileFragment(id,fileName,sizeFile,pointer,fragment);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendFileList(int id, ArrayList<File> files) throws IOException {
        this.commandeSendFileList(id,files);
        writeOnSocketChannel();
    }

    @Override
    public void commandeSendPeerList(int id, ArrayList<Peer> peers) throws IOException {
        this.serializable.commandeSendPeerList(id,peers);
        writeOnSocketChannel();
    }
    private void writeOnSocketChannel() throws IOException {
        this.client.getSocketChannel().write(this.byteBuffer);
        this.byteBuffer.clear();
    }

}
