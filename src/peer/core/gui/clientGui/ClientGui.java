package peer.core.gui.clientGui;

import peer.core.handler.Handler;
import peer.core.logger.Logger;
import peer.core.util.folder.File;
import peer.core.util.peer.Peer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClientGui extends JFrame implements Logger {
    private JButton peerButton;
    private JButton fileButton;
    private JComboBox listFile;
    private JButton getFileButton;
    private JTextArea logger;
    private JPanel mainPanel;
    private JButton messagButton;
    private JTextField messageField;
    private JButton sendButton;
    private JTextField textField1;
    private Handler handler;
    private ArrayList<File> fileList;

    public ClientGui(Handler handler) {
        add(mainPanel);
        setSize(900, 800);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.fileList = new ArrayList<>();
        this.handler = handler;
        peerButton.addActionListener(e -> {

        });

        this.messagButton.addActionListener(e -> {
            String message = messageField.getText();
            try {
                handler.commandeMessage(1, message);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        this.sendButton.addActionListener(e -> {
            int prot = 1234;
            try {
                handler.commandeDeclarePort(2, prot);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.fileButton.addActionListener(e -> {
            try {
                handler.commandeFileList(5);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.peerButton.addActionListener(e -> {
            try {
                handler.commandePeerList(3);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.getFileButton.addActionListener(e -> {
            var tmp = listFile.getSelectedItem().toString().split(" : ");
            var fileName = tmp[0];
            var fileSize = tmp[1];
            System.out.println(fileName + "- " + fileSize);
        });
    }


    public void log(String s) {
        logger.append(s);
    }

    @Override
    public void message(int id, String message) {
        this.separator();
        log("[" + id + ", " + message + "']");
    }

    @Override
    public void declarePort(int id, int port) {
        this.separator();
        log("[" + id + ", '" + port + "']");
    }

    @Override
    public void listLength(int size) {
        this.separator();
        log("Peer : [ " + size + " ]");

    }

    @Override
    public void fileLength(int size) {
        this.separator();

        log("File : [ " + size + " ]");
    }

    @Override
    public void listPeer(int id, ArrayList<Peer> peers) {
        this.separator();
        log("[" + id + ", " + peers.size() + ", [ ");
        for (Peer p : peers) {
            log("[ " + p.getPort() + "," + p.getAddress() + " ]");
        }
        log(" ] ]");
    }

    @Override
    public void listFile(int id, ArrayList<File> files) {
        fileList.addAll(files);
        this.separator();
        log("[" + id + ", " + files.size() + ", [ ");
        for (File f : files) {
            listFile.addItem(new ComboItem(f.getName() + " : " + f.getSize(), f.getSize() + ""));
            log("[ " + f.getName() + "," + f.getSize() + " ]\n");
        }
        log(" ] ]");
    }

    @Override
    public void file(int id, String fileName, long sizeFile, long pointer, int fragment) {
        this.separator();
        log("ID " + id + " = [ " + fileName + ":" + sizeFile + " pointer :" + pointer + ", fragment: " + fragment + "]");
    }

    @Override
    public void error(int id) {
        this.separator();
        log("ERROR serveur send : " + id);
    }

    @Override
    public void command(int id) {
        this.separator();
        log("[ " + id + " ]");
    }

    @Override
    public void separator() {
        log("-------------------------------\n");
    }

}

class ComboItem extends Component {
    private String key;
    private String value;

    public ComboItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return key + "!_W_!" + value;
    }
}
