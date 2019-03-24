package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    private Deserialisation serverDeserializer;
    private ArrayList<Peer> peers;
    private Folder folder;
 
    /**
     * @param port, adressIp
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
        SocketAddress inetSocketAddress = new InetSocketAddress(port);
        this.serverSocketChannel.bind(inetSocketAddress);
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    }
    
    public ByteBuffer getByteBuffer() {
    	return this.byteBuffer;
    }

    /**
     * @throws IOException
     */
    public void accept() throws IOException {
    	this.byteBuffer.clear();
		SocketChannel socketChannel = serverSocketChannel.accept();
        this.commandMessage(socketChannel, "Reference Implementation 🚀 ");
        this.commandSendIp(socketChannel, socketChannel.getRemoteAddress().toString()); 
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
        iServerLogger.clientConnected(socketChannel.getRemoteAddress().toString());
    }

    @Override
    public void run() {

        try {
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
    	System.out.println("transmission");
    	SocketChannel clientSocket = (SocketChannel) key.channel();
    	
    	while(clientSocket.isConnected()) {
    		byteBuffer.clear();
			if (this.receiveMessage (clientSocket)) {
				byteBuffer.flip();
				String adressIp = clientSocket.getRemoteAddress().toString();
				int id = byteBuffer.get();
				this.iServerLogger.clientSentCommand(adressIp, Integer.toString(id));
				byteBuffer.clear();
				  switch (id) {
		            case 2: 
		            	commandGetPort(clientSocket,byteBuffer);
		                break;
		            case 3: 
		            	sendPeersList(clientSocket,byteBuffer);
		                break;
		            case 5:
		            	sendFilesList(clientSocket,byteBuffer);
		                break;
		            case 7:
		            	sendFragmentFile(clientSocket,byteBuffer);
		                break;
		                
				  }
			}
		}
		
    }
    	
    private void commandGetPort(SocketChannel clientSocket,ByteBuffer byteBuffer) throws IOException {
    	int port =byteBuffer.getInt();
    	String adressIp = clientSocket.getRemoteAddress().toString();
    	this.peers.add(new Peer(port,adressIp));
    	System.out.println("Peers added succefully");
    	this.byteBuffer.clear();
    }
    private void sendPeersList(SocketChannel clientSocket,ByteBuffer byteBuffer) {
    	
    	this.byteBuffer.clear();
    }
    private void sendFilesList(SocketChannel clientSocket,ByteBuffer byteBuffer) {
    	
    	this.byteBuffer.clear();
    }
    private void sendFragmentFile(SocketChannel clientSocket,ByteBuffer byteBuffer) {
    	
    	this.byteBuffer.clear();
    }
   
    
    private boolean receiveMessage (SocketChannel clientSocket) throws IOException {
    	return clientSocket.read( this.byteBuffer) > 0;
    }
    public void SendPeersList(SocketChannel socketChannel) throws IOException {
 //      this.serializable.commandPeerList(this.peers);
 //      this.writeOnSocketChannel();
    }
    private void writeOnSocketChannel(SocketChannel socketChannel) throws IOException {
    	socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }

    public void commandMessage(SocketChannel socketChannel, String message) throws IOException {
        this.serverSerializer.sendMessage(message);
        this.writeOnSocketChannel(socketChannel); 
    }

    public void commandId(SocketChannel socketChannel, int id) throws IOException {
        this.serverSerializer.commandID(id);
        this.writeOnSocketChannel(socketChannel);
    }
    public void commandSendIp(SocketChannel socketChannel, String ip) throws IOException {
        this.serverSerializer.sendMessage("You are connected from "+this.adressIp+ip);
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
