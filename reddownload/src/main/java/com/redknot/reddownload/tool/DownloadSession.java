package com.redknot.reddownload.tool;

import android.os.Message;

import com.redknot.reddownload.listener.DownloadListener;
import com.redknot.reddownload.listener.VALUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by miaoyuqiao on 16/6/6.
 */
public class DownloadSession {

    private DownloadListener downloadListener = null;

    private int length = 0;

    private int totle = 0;

    private int process_cache = 0;

    private boolean isDownload = true;

    private synchronized void update(int add){
        this.totle = this.totle + add;

        int process = (int)((totle)/(length + 0.0f) * 100);

        if(process != this.process_cache){
            Message msg = new Message();
            this.process_cache = process;
            msg.what = VALUE.UPDATE;
            msg.obj = process;
            downloadListener.sendMessage(msg);
        }

        if(this.totle == this.length){
            Message msg = new Message();
            msg.what = VALUE.COMPLETE;
            downloadListener.sendMessage(msg);
        }
    }

    public DownloadSession(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void start(String url,String savePath,int threadNum) {
        GetLengthThread t = new GetLengthThread(url,savePath,threadNum);
        new Thread(t).start();
    }

    public void pause(){

    }

    public void stop(){

    }

    private class DownLoadThread implements Runnable {
        private int begin;
        private int end;
        private File file;
        private URL url;
        private int id;

        public DownLoadThread(int id, int begin, int end, File file, URL url) {
            this.begin = begin;
            this.end = end;
            this.file = file;
            this.url = url;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:43.0) Gecko/20100101 Firefox/43.0");
                conn.setRequestProperty("Range", "bytes=" + this.begin + "-" + this.end);

                InputStream is = conn.getInputStream();
                byte[] buff = new byte[1024 * 1024 * 2];

                RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw");
                randomAccessFile.seek(this.begin);

                int len = 0;
                while (((len = is.read(buff)) != -1) && isDownload) {
                    randomAccessFile.write(buff, 0, len);
                    update(len);
                }

                is.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetLengthThread implements Runnable {
        private String url;
        private String savePath;
        private int threadNum;

        public GetLengthThread(String url,String savePath ,int threadNum) {
            this.url = url;
            this.savePath = savePath;
            this.threadNum = threadNum;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:43.0) Gecko/20100101 Firefox/43.0");

                length = conn.getContentLength();//获取文件长度

                if (length < 0) {
                    return;
                }

                File file = new File(this.savePath);

                Message msg = new Message();
                msg.what = VALUE.READY;
                msg.obj = file;
                downloadListener.sendMessage(msg);

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(length);
                randomAccessFile.close();

                int blockSize = length / this.threadNum;
                for (int i = 0; i < this.threadNum; i++) {
                    int begin = i * blockSize;
                    int end = (i + 1) * blockSize - 1;
                    if (i == this.threadNum - 1) {
                        end = length - 1;
                    }
                    DownLoadThread downLoadThread = new DownLoadThread(i, begin, end, file, url);
                    new Thread(downLoadThread).start();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
