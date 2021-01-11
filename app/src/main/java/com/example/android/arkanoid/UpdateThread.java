package com.example.android.arkanoid;

import android.os.Handler;

public class UpdateThread extends Thread {
    Handler updatovaciHandler; //che cazz vuol dire bo updatedHandler forse

    public UpdateThread(Handler uh) {
        super();
        updatovaciHandler = uh;
    }

    public void run() {
        while (true) {
            try {
                sleep(32);
            } catch (Exception ex) {
            }
            updatovaciHandler.sendEmptyMessage(0);
        }
    }
}
