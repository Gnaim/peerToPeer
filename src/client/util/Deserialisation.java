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
        int n = this.byteBuffer.getInt();
        int lim = this.byteBuffer.limit();
        this.byteBuffer.limit(this.byteBuffer.position()+n);
        String message = CHARSET.decode(this.byteBuffer).toString();
        this.byteBuffer.limit(lim);
        this.iClientLogger.connected(id,message);

    }

    private void listPairs(int id){
        int nbPairs = this.byteBuffer.getInt();
        int lim = this.byteBuffer.limit();
        System.out.println(nbPairs);
            int p = this.byteBuffer.getInt();
            int n = this.byteBuffer.getInt();
            this.byteBuffer.limit(this.byteBuffer.position()+n);
            String message = CHARSET.decode(this.byteBuffer).toString();
            this.byteBuffer.limit(lim);
            this.iClientLogger.liste(id,p,message);
        byteBuffer.clear();
    }
}
