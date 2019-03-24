package peer.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import peer.Server;

public class ServerSerializer {

    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;

	public ServerSerializer(Server server) {
		this.byteBuffer = server.getByteBuffer();	
	}
	
}
