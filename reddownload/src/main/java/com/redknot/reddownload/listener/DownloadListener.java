package com.redknot.reddownload.listener;


import android.os.Handler;
import android.os.Message;

import java.io.File;

/**
 * Created by miaoyuqiao on 16/6/7.
 */
public abstract class DownloadListener extends Handler {

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == VALUE.READY) {
            File file = (File) msg.obj;
            onReady(file);
        } else if (msg.what == VALUE.UPDATE) {
            int process = (int) msg.obj;
            onUpdate(process);
        } else if (msg.what == VALUE.COMPLETE) {
            onComplete();
        } else {

        }
    }

    public abstract void onReady(File file);

    public abstract void onUpdate(int process);

    public abstract void onComplete();
}
