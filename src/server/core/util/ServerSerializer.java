package server.core.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import server.Server;

public class ServerSerializer {

    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;

	public ServerSerializer(Server server) {
		this.byteBuffer = server.getByteBuffer();	
	}
		
	public void sendMessage(String message) {
		this.byteBuffer
			.clear()
			.put((byte)1);
		ByteBuffer buffer = CHARSET.encode(message);
		this.byteBuffer
			.putInt(buffer.remaining())
			.put(buffer)
			.flip();	
	}

	  public void commandID(int ID) throws IOException {
        this.byteBuffer
        	.clear()
        	.put((byte) ID)
        	.flip();
    }

	public void sendIp(String ip) throws IOException {
    	this.byteBuffer
    		.clear()
    		.put((byte)1);
        ByteBuffer buffer = CHARSET.encode(ip);
        this.byteBuffer
        	.putInt(buffer.remaining())
        	.put(buffer)
        	.flip();
    }
	
	
}
