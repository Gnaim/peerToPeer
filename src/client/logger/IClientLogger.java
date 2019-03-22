package client.logger;

public class IClientLogger implements Logger {
    @Override
    public void connected(int id, String message) {
        System.out.println("ID" + id +"=" +message);
    }

    @Override
    public void liste(int id, int port, String adr) {
        System.out.println("ID "+ id +" = [ " + port + " " +adr+ " ]");
    }

    @Override
    public void error(int id) {
        System.out.println("ERROR serveur send : " +id);
    }
}
