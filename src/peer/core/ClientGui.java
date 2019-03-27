package peer.core;

import javax.swing.*;

public class ClientGui extends JFrame{
    private JPanel panel;
    private JTextArea logger;


    public ClientGui(){
        add(panel);
        setSize(600,800);
        setVisible(true);


    }

    public JTextArea getLogger() {
        return logger;
    }
}
