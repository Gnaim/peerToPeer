package client.util;

import client.Client;
import client.logger.IClientLogger;
import client.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Deserialisation {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;
    private IClientLogger iClientLogger;
    private Client client;

    public Deserialisation(Client client) {
        this.byteBuffer = client.getByteBuffer();
        this.iClientLogger = client.getiClientLogger();
        this.client = client;
    }

    public void start() throws IOException {
        this.byteBuffer.flip();
        int id = this.byteBuffer.get();
        switch (id) {
            case 1:
                this.message(id);
                break;
            case 3:
                this.iClientLogger.command(id);
                this.client.commandSendList();
                this.client.commandGetList();
                break;
            case 4:
                this.listPairs(id);
                break;
            case 5:
                this.iClientLogger.command(id);

                this.client.commandGetFileList();
                break;
            case 6:
                this.listFile(id);
                break;
            default:
                this.iClientLogger.error(id);
        }
        this.byteBuffer.clear();

    }

    private void message(int id) {
        this.iClientLogger.connected(id, this.getString());
    }

    private void listPairs(int id) {
        int paire = this.getInt();
        this.iClientLogger.listLength(paire);
        for (int i = 0; i < paire; i++) {
            var port = this.getInt();
            String address = this.getString();
            this.client.getPeers().add(new Peer(port,address));
            this.iClientLogger.listPeer(id,port,address);
        }
        byteBuffer.clear();
    }

    private void listFile(int id) {
        int nbFile = this.getInt();
        this.iClientLogger.fileLength(nbFile);
        for (int i = 0; i < nbFile; i++) {
            this.iClientLogger.listFile(id, this.getString(), this.getLong());
        }
    }

    private int getInt() {
        return this.byteBuffer.getInt();
    }

    private String getString() {
        int stringSize = this.getInt();
        int limit = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position() + stringSize);
        String message = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(limit);
        return message;
    }

    private long getLong() {
        return this.byteBuffer.getLong();
    }
}
