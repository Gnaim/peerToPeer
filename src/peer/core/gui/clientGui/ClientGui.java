package peer.core.gui.clientGui;

import peer.Client;
import peer.Server;
import peer.core.handler.Handler;
import peer.core.logger.Logger;
import peer.core.util.ComboItem;
import peer.core.util.folder.File;
import peer.core.util.folder.Fragment;
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
    private JButton connectButton;
    private JComboBox peersList;
    private Handler handler;
    private ArrayList<File> fileList;

    public ClientGui(Handler handler) {
        add(mainPanel);
        setSize(900, 800);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.textField1.setEditable(false);
        this.logger.setEnabled(false);
        setLocation(200,200);
        this.textField1.setText(Server.SERVER_PORT+"");
        this.fileList = new ArrayList<>();
        this.handler = handler;
        peerButton.addActionListener(e -> {

        });

        this.messagButton.addActionListener(e -> {
            String message = messageField.getText();
            try {
                handler.commandeMessage(Handler.COMMANDE_MESSAGE, message);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            messageField.setText("");
        });

        this.sendButton.addActionListener(e -> {
            try {
                handler.commandeDeclarePort(Handler.COMMANDE_DECLARE_PORT, Server.SERVER_PORT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.fileButton.addActionListener(e -> {
            try {
                handler.commandeFileList(Handler.COMMANDE_FILE_LIST);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.peerButton.addActionListener(e -> {
            try {
                handler.commandePeerList(Handler.COMMANDE_PEER_LIST);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.getFileButton.addActionListener(e -> {
            if (listFile.getItemCount() != 0){
                var tmp = listFile.getSelectedItem().toString().split(" : ");
                var fileName = tmp[0];
                var fileSize = Integer.valueOf(tmp[1]);
                Fragment fragment = new Fragment(fileName,fileSize,0,fileSize);
                try {
                    this.handler.commandeFileFragment(7,fragment);
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
        });
        this.connectButton.addActionListener(e -> {
            if (peersList.getItemCount() != 0){
                var tmp = peersList.getSelectedItem().toString().split(" : ");
                var serverAddress = tmp[0];
                var ServerPort = Integer.valueOf(tmp[1]);
                new Thread(new Client(serverAddress,ServerPort)).start();
            }
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
        peersList.removeAllItems();
        log("[" + id + ", " + peers.size() + ", [ ");
        for (Peer p : peers) {
            peersList.addItem(new ComboItem(p.getAddress()+ " : " + p.getPort(), p.getPort()+ ""));
            log("[ " + p.getPort() + "," + p.getAddress() + " ]");
        }
        log(" ] ]");
    }

    @Override
    public void listFile(int id, ArrayList<File> files) {
        fileList.addAll(files);
        listFile.removeAllItems();
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

