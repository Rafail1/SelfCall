package com.hfad.selfcall.Classes;

import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by user-dis2 on 8/29/2016.
 */
public class ToastShower extends Thread {
    private Toast toast;
    public AtomicBoolean stopToast = new AtomicBoolean(false);
    public ToastShower(Toast toast){
        this.toast = toast;
    }

    @Override
    public void run() {
        int k = 0;
        while (!stopToast.get() && k < 28) {
            k++;
            toast.show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
