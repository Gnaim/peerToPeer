package peer.core.peer;

public class Peer {
    private int port;
    private String address;

    public Peer(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address+port;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.toString().equals(address+port) ;
    }
}
