package peer.core.util;

import peer.core.handler.Handler;
import peer.core.util.folder.Fragment;

import java.io.IOException;

public class MyRunnable implements Runnable {
    private Handler handler;
    private Fragment fragment ;

    public MyRunnable(Handler handler, Fragment fragment) {
        this.handler = handler;
        this.fragment = fragment;
    }

    public void run() {
        try {
            this.handler.commandeFileFragment(7,fragment);
        } catch (IOException e1) {

            e1.printStackTrace();
        }
    }
}
