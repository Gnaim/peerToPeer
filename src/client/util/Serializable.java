package client.util;

import client.Client;
import client.folder.File;
import client.logger.IClientLogger;
import client.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.util.ArrayList;

public class Serializable {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    private ByteBuffer byteBuffer;
    private Client client;
    private IClientLogger iClientLogger;
    private SocketChannel socketChannel;

    public Serializable(Client client) {
        this.client = client;
        this.byteBuffer = client.getByteBuffer();
        this.iClientLogger = client.getiClientLogger();
        this.socketChannel = client.getSocketChannel();

    }

    public void commandID(int ID) throws IOException {
        this.byteBuffer
        	.clear()
        	.put((byte) ID)
        	.flip();
    }

    public void commandPeerList(ArrayList<Peer> peer) {
        this.byteBuffer.clear()
        	.put((byte) 4)
        	.putInt(peer.size());
        for (Peer p : peer) {
            this.byteBuffer.putInt(p.getPort());
            ByteBuffer buffer1 = CHARSET.encode(p.getAddress());
            this.byteBuffer
            	.putInt(buffer1.remaining())
            	.put(buffer1);
        }
        this.byteBuffer.flip();
    }

    public void commandFileList(ArrayList<File> files) {
        this.byteBuffer
        	.clear()
        	.put((byte) 6)
        	.putInt(files.size());
        for (File f : files) {
            ByteBuffer buffer1 = CHARSET.encode(f.getName());
            this.byteBuffer
            	.putInt(buffer1.remaining())
            	.put(buffer1)
            	.putLong(f.getSize());
        }
        this.byteBuffer.flip();
    }

    public void getFile(String fileName, long sizeFile, long pointer, int fragment) {
    	this.byteBuffer
    		.clear()
    		.put((byte) 7)
    		.flip();
    }

    public void file(String fileName, long sizeFile, long pointer, int fragment, Blob blob) {
        this.sertlizeFileName(fileName,sizeFile,pointer,fragment);

        //TODO : blob serlize

    }

    private void sertlizeFileName(String fileName, long sizeFile, long pointer, int fragment) {
        this.byteBuffer
        	.clear()
        	.put((byte) 7);
        ByteBuffer buffer1 = Deserialisation.CHARSET.encode(fileName);
        this.byteBuffer
        	.putInt(buffer1.remaining())
        	.put(buffer1)
        	.putLong(sizeFile)
        	.putLong(pointer)
        	.putInt(fragment);
    }

	public void askForFile(String fileName, long sizeFile, long pointer, int fragment) {
		this.byteBuffer
			.clear()
			.put((byte) 7);
        ByteBuffer buffer1 = CHARSET.encode(fileName);
        this.byteBuffer
        	.putInt(buffer1.remaining())
        	.put(buffer1)
        	.putLong(sizeFile)
        	.putLong(pointer)
        	.putInt(fragment)
        	.flip();
        
	}

}
