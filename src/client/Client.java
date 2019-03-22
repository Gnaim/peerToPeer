package client;

import client.util.Deserialisation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Client implements Runnable{
	private int serverPort;
	private String serverAddress;
	private SocketChannel socketChannel;
	private SocketAddress socketAddress;
	private ByteBuffer byteBuffer;
	private Deserialisation deserialisation;


	public Client(String serverAddress, int serverPort) {
		this.serverPort = serverPort;
		this.serverAddress = serverAddress;
		this.byteBuffer =  ByteBuffer.allocate(9000);
		this.deserialisation = new Deserialisation();
	}

	public void run() {
		try {
			this.socketAddress = new InetSocketAddress(this.serverAddress,this.serverPort);
			this.socketChannel = this.socketChannel.open();
			this.socketChannel.connect(this.socketAddress);
			this.start(); //Reference Implementation
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void start() throws IOException {
		while (this.socketChannel.isConnected()){

			socketChannel.read(this.byteBuffer);
			this.byteBuffer.flip();
			this.deserialisation.setByteBuffer(byteBuffer);
			this.deserialisation.start();
			this.byteBuffer.clear();

			this.byteBuffer.put((byte)3);
			this.byteBuffer.flip();
			socketChannel.write(this.byteBuffer);
			this.byteBuffer.clear();
		}

	}
}
