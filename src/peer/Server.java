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
import peer.core.folder.Folder;
import peer.core.logger.IServerLogger;
import peer.core.peer.Peer;
import peer.core.util.Serialize;

public class Server implements Runnable {

    private String adressIp;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer byteBuffer;
    private IServerLogger iServerLogger;
    private int port;
    private boolean stop;
    private Serialize serverSerializer;
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
        this.serverSerializer = new Serialize(this.byteBuffer);
        this.iServerLogger = new IServerLogger();  
        this.peers = new ArrayList<>();
        this.peers.add(new Peer(23,"Test Adress")); // Test Peer
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
        this.SendIp(socketChannel, this.adressIp); 
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
                    { 
	                        this.accept();}
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
		            	sendPeersList(clientSocket);
		                break;
		            case 5:
		            	sendFilesList(clientSocket);
		                break;
		            case 7:
		            	sendFragmentFile(clientSocket,byteBuffer);
		                break; 
		            default:
		                this.error(id);
				  }
			}
		}
		
    }
    	
    private void commandGetPort(SocketChannel clientSocket,ByteBuffer byteBuffer) throws IOException {
    	int port =byteBuffer.getInt();
    	String adressIp = clientSocket.getRemoteAddress().toString();
    	this.peers.add(new Peer(port,adressIp));
		this.iServerLogger.peerAdded(adressIp,Integer.toString(port));
    	this.byteBuffer.clear();
    }
    private void sendPeersList(SocketChannel clientSocket) throws IOException {
    	this.serverSerializer.commandPeerList(this.peers);
        this.iServerLogger.sendPeersList(clientSocket.getRemoteAddress().toString());
        this.writeOnSocketChannel(clientSocket); 
    }
  
    private void sendFilesList(SocketChannel clientSocket) throws IOException {
    	this.serverSerializer.commandFileList(this.folder.listFilesForFolder());
        this.writeOnSocketChannel(clientSocket); 
        this.iServerLogger.sendFilesList(clientSocket.getRemoteAddress().toString());
    }
    private void sendFragmentFile(SocketChannel clientSocket,ByteBuffer byteBuffer) {
    	//send Fragement
    	this.byteBuffer.clear();
    }
    private void error (int id) {
    	this.iServerLogger.error(id);
    	this.byteBuffer.clear();
    }
    
    private boolean receiveMessage (SocketChannel clientSocket) throws IOException {
    	return clientSocket.read( this.byteBuffer) > 0;
    }
    
    private void writeOnSocketChannel(SocketChannel socketChannel) throws IOException {
    	socketChannel.write(this.byteBuffer);
        this.byteBuffer.clear();
    }

    public void commandMessage(SocketChannel socketChannel, String message) throws IOException {
        //this.serverSerializer.sendMessage(message);
        this.writeOnSocketChannel(socketChannel); 
    }

    public void commandId(SocketChannel socketChannel, int id) throws IOException {
        this.serverSerializer.commandID(id);
        this.writeOnSocketChannel(socketChannel);
    }
    public void SendIp(SocketChannel socketChannel, String serverName) throws IOException {
    	String ip=socketChannel.getRemoteAddress().toString();
        this.serverSerializer.sendIp(serverName,ip);
        this.writeOnSocketChannel(socketChannel);
    } 
}
