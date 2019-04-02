package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import peer.core.folder.Folder;
import peer.core.folder.Fragment;
import peer.core.logger.IServerLogger;
import peer.core.peer.Peer;
import peer.core.util.Deserialize;
import peer.core.util.Serialize;

public class Server implements Runnable {

    private final Deserialize deserialize;
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
        this.deserialize = new Deserialize(this.byteBuffer);


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
        byteBuffer.clear();
        this.commandMessage(socketChannel, "Reference Implementation 🚀 ");
        this.SendIp(socketChannel, this.adressIp);
     //   this.initialCommand(socketChannel);
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

        for(SelectionKey s: selector.keys()) {

                if (this.receiveMessage(clientSocket)) {

                    byteBuffer.flip();
                    String adressIp = clientSocket.getRemoteAddress().toString();
                    int id = byteBuffer.get();
                    this.iServerLogger.clientSentCommand(adressIp, Integer.toString(id));
                    commandExecuteID(id,clientSocket);

                }
            }

    }

    private void commandExecuteID (int id, SocketChannel clientSocket) throws IOException {

        switch (id) {
            case 2:
                commandGetPort(clientSocket, byteBuffer);
                break;
            case 3:
          //      manyIdCheck(clientSocket);
                sendPeersList(clientSocket);
                break;
            case 4:
                this.iServerLogger.listPeer(id, this.peerList(id));
                break;
            case 5:
           //     manyIdCheck(clientSocket);
                sendFilesList(clientSocket);
                break;
            case 7:
                commandeSendFileFragment(this.fileItem(id));
                break;
            default:
                this.error(id);
        }
    }
    private void manyIdCheck (SocketChannel clientSocket) throws IOException {
        if (this.byteBuffer.capacity()>0){
            this.commandExecuteID(this.byteBuffer.getInt(),clientSocket);
        }
    }
    private void initialCommand(SocketChannel clientSocket) throws IOException {
        this.askForPeerList(clientSocket);
        this.askForFileList(clientSocket);
    }
    private void commandGetPort(SocketChannel clientSocket,ByteBuffer byteBuffer) throws IOException {
    	int port =byteBuffer.getInt();
    	String adressIp = clientSocket.getRemoteAddress().toString();
    	this.peers.add(new Peer(port,adressIp));
		this.iServerLogger.peerAdded(adressIp,Integer.toString(port));
    	this.byteBuffer.clear();
    }
    public ArrayList<Peer> peerList(int id) {
        ArrayList <Peer> peers = this.deserialize.peerList(id);
        for (Peer p : peers ) {
            if(!this.peers.contains(p)){
                this.peers.add(p);
            }
        }
        return peers;
    }
    private void sendPeersList(SocketChannel clientSocket) throws IOException {
        this.serverSerializer.commandeSendPeerList(4,this.peers);
        this.iServerLogger.sendPeersList(clientSocket.getRemoteAddress().toString());
        this.writeOnSocketChannel(clientSocket);
    }
  
    private void sendFilesList(SocketChannel clientSocket) throws IOException {
    	this.serverSerializer.commandFileList(this.folder.listFilesForFolder());
        this.writeOnSocketChannel(clientSocket); 
        this.iServerLogger.sendFilesList(clientSocket.getRemoteAddress().toString());
    }
    private void commandeSendFileFragment(Fragment fragment) throws IOException {
        this.serverSerializer.commandeSendFileFragment(8,fragment);
    	this.byteBuffer.clear();
    }
    private void askForPeerList(SocketChannel clientSocket) throws IOException {
        this.byteBuffer.clear();
        this.byteBuffer.put((byte)3);
        this.byteBuffer.flip();
        clientSocket.write(this.byteBuffer);
    }
    private void askForFileList(SocketChannel clientSocket) throws IOException {
        this.byteBuffer.clear();
        this.byteBuffer.put((byte)5);
        this.byteBuffer.flip();
        clientSocket.write(this.byteBuffer);
    }
    private Fragment fileItem(int id) throws IOException {
        return this.deserialize.fileItem(id);
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
        this.serverSerializer.commandeMessage(1,message);
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
