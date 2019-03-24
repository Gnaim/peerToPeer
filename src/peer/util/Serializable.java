package peer.util;

import peer.Client;
import peer.folder.File;
import peer.logger.IClientLogger;
import peer.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.util.ArrayList;

public class Serializable {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    
    private ByteBuffer byteBuffer;
    
    /**
     * 
     * @param client
     */
    public Serializable(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }
    
    /**
     * 
     * @param ID
     * @throws IOException
     */
    public void commandID(int ID) throws IOException {
        this.byteBuffer
        	.clear()
        	.put((byte) ID)
        	.flip();
    }
    
    /**
     * 
     * @param prot
     */
    public void sendPort(int port) {
    	this.byteBuffer
    		.clear()
    		.put((byte)2)
    		.putInt(port).flip();
    }
    
    /**
     * 
     * @param peer
     */
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
    
    /**
     * 
     * @param files
     */
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
    
    /**
     * 
     * @param fileName
     * @param sizeFile
     * @param pointer
     * @param fragment
     */
    public void getFile(String fileName, long sizeFile, long pointer, int fragment) {
    	this.byteBuffer
    		.clear()
    		.put((byte) 7)
    		.flip();
    }
    
    /**
     * 
     * @param fileName
     * @param sizeFile
     * @param pointer
     * @param fragment
     * @param blob
     */
    public void file(String fileName, long sizeFile, long pointer, int fragment, Blob blob) {
        this.sertlizeFileName(fileName,sizeFile,pointer,fragment);
        //TODO : blob serlize
    }
    
    /**
     * 
     * @param fileName
     * @param sizeFile
     * @param pointer
     * @param fragment
     */
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

    /**
     * 
     * @param fileName
     * @param sizeFile
     * @param pointer
     * @param fragment
     */
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
