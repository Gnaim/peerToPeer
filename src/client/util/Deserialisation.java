package client.util;

import client.logger.IClientLogger;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Deserialisation {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;
    private IClientLogger iClientLogger;

    public Deserialisation() {
        this.byteBuffer = null;
        this.iClientLogger = new IClientLogger();
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }


    public void start(){
        int id = this.byteBuffer.get();
        switch (id){
            case 1:
                  init(id);
                break;
            case 4:
                listPairs(id);
                break;
            default:
                this.iClientLogger.error(id);
        }
    }

    private void init(int id ) {
        this.iClientLogger.connected(id,this.getString());

    }

    private void listPairs(int id){
        int paire = this.getInt();
        this.iClientLogger.listSize(paire);
        for (int i=0; i< paire;i++){
            this.iClientLogger.list(id,this.getInt(),this.getString());
        }

        byteBuffer.clear();
    }

    private int getInt(){
        return this.byteBuffer.getInt();
    }

    private String getString(){
        int stringSize = this.getInt();
        int limit = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position() + stringSize);
        String message = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(limit);
        return message;
    }
}
