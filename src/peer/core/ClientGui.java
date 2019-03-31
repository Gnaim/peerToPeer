package peer.core;

import javax.swing.*;
import java.awt.*;

public class ClientGui extends JFrame{
    private JPanel panel;
    private JButton fileList;
    private JButton peerList;
    private JButton getFile;
    private JList listFile;
    private JList listPeer;


    public ClientGui(){


        add(panel);
        setSize(600,800);
        setVisible(true);
        peerList.addActionListener(e -> {
            listFile.add(new Component() {
                @Override
                public String getName() {
                    return super.getName();
                }
            });
        });
    }

    public JList getListFile() {
        return listFile;
    }

    public JList getListPeer() {
        return listPeer;
    }
}
