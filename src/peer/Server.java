package peer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import peer.folder.Folder;
import peer.logger.IServerLogger;
import peer.peer.Peer;
import peer.util.Deserialisation;
import peer.util.Serializable;

public class Server implements Runnable {

    private String adressIp;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer byteBuffer;
    private IServerLogger iServerLogger;
    private int port;
    private boolean stop;
    private Serializable serverSerializer;
    private Deserialisation deserialisation;
    private ArrayList<Peer> peers;
    private Folder folder;
 
    /**
     * @param port
     * @throws IOException
     */
    public Server(String adressIp,int port) throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.port = port;
        this.adressIp = adressIp;
        this.stop = false;
        this.byteBuffer = ByteBuffer.allocateDirect(65574);
        this.selector = Selector.open();
        this.serverSerializer = new Serializable(this.byteBuffer);
        this.iServerLogger = new IServerLogger();  
        this.peers = new ArrayList<>();
        this.folder = new Folder();

    }
    
    public ByteBuffer getByteBuffer() {
    	return this.byteBuffer;
    }

    /**
     * @throws IOException
     */
    public void accept() throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        this.commandMessage(socketChannel, "Reference Implementation 🚀");
        this.commandSendIp(socketChannel,socketChannel.getRemoteAddress().toString());
        iServerLogger.clientConnected(socketChannel.getRemoteAddress().toString());
    }

    /**
     * @param key
     * @throws IOException
     */

    @Override
    public void run() {

        try {
            SocketAddress inetSocketAddress = new InetSocketAddress(port);
            this.serverSocketChannel.bind(inetSocketAddress);
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            iServerLogger.serverStarting(this.port);
            while (!stop) {
                selector.select();

                for (SelectionKey k : selector.selectedKeys()) {
                    if (k.isAcceptable())
                        this.accept();
                    else
                        this.transmition(k);
                }

                selector.selectedKeys().clear();

            }
        } catch (IOException e) {
            iServerLogger.systemMessage(e.getMessage());
        }
    }
    

    public synchronized void finish() {
        this.stop = true;
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            iServerLogger.systemMessage(e.getMessage());
        }
        iServerLogger.serverClosing();
    }
    
    private void transmition (SelectionKey key) throws IOException {
    	SocketChannel clientSocket = (SocketChannel) key.channel();
    	clientSocket.read(this.byteBuffer);
    	this.byteBuffer.flip();
    	
    	while(clientSocket.isConnected()) {
    /*		int id = this.byteBuffer.get();
    		 switch (id) {
    		 		//	Manage Protocol or using Deserilization
    		 }
    */
    	}
   
    }
    private void writeOnSocketChannel(SocketChannel socketChannel) throws IOException {
    	socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }

    public void commandMessage(SocketChannel socketChannel, String message) throws IOException {
        this.serverSerializer.sendMessage(message);
        this.writeOnSocketChannel(socketChannel);
    }

    public void commandSendIp(SocketChannel socketChannel, String ip) throws IOException {
        this.serverSerializer.sendIp("You are connected from "+this.adressIp+ip);
        this.writeOnSocketChannel(socketChannel);
    }
    
    public void commandGetList(SocketChannel socketChannel) throws IOException {
        this.serverSerializer.commandID(3);
        this.writeOnSocketChannel(socketChannel);
    }
    public void commandSendFileList(SocketChannel socketChannel) throws IOException {
        this.serverSerializer.commandFileList(this.folder.listFilesForFolder());
        this.writeOnSocketChannel(socketChannel);
    }
    public void commandSendListPeer(SocketChannel socketChannel) throws IOException {
        this.serverSerializer.commandPeerList(this.peers);
       this.writeOnSocketChannel(socketChannel);
    }
}
