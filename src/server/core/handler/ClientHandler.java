package server.core.handler;

import server.core.handler.io.ServerInput;
import server.core.handler.io.ServerOutput;
import server.core.logger.IServerLogger;

import java.nio.channels.SocketChannel;

public class ClientHandler implements Runnable{

    /**
     * To be able to handle the client's input and output
     */
    private final SocketChannel socket;
    private ServerInput clientInput;
    private ServerOutput clientOutput;

    /**
     * The server logger to writing all whats going on the server
     */
    private IServerLogger serverLogger;

    /**
     * all the informations about the player
     *
     * @see Peer
     */
    //private Peer peer ;

    /**
     *  @param socket
     * @param serverLogger
     */
    public ClientHandler(SocketChannel socket, IServerLogger serverLogger) {
        this.serverLogger = serverLogger;
        this.socket = socket;
      //  this.peer = null;
    }

    @Override
    public void run() {

    }
}
