package com.redknot.reddownload.tool;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

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
    private String url;
    private String savePath;
    private int length = 0;
    private int total = 0;
    private DownloadListener downloadListener = null;

    public DownloadSession(String url, DownloadListener downloadListener) {
        this.url = url;
        this.downloadListener = downloadListener;
    }

    public void start() {
        GetLengthThread t = new GetLengthThread(this.url);
        new Thread(t).start();
    }

    synchronized void update(int add) {
        this.total += add;

        Message message = new Message();

        if(this.total == this.length){
            message.what = VALUE.COMPLETE;
            downloadListener.sendMessage(message);
            return;
        }

        Log.e("p",this.total + "  " + this.length);

        message.what = VALUE.UPDATE;
        double p = (this.total + 0.0f) / this.length;

        message.obj = (int) (100 * p);
        downloadListener.sendMessage(message);
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
                while ((len = is.read(buff)) != -1) {
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

        public GetLengthThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:43.0) Gecko/20100101 Firefox/43.0");

                int length = conn.getContentLength();//获取文件长度

                Log.e("Location", length + "");
                DownloadSession.this.length = length;

                if (length < 0) {
                    return;
                }

                File file = new File(Environment.getExternalStorageDirectory(), "aaamiao");

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(length);

                randomAccessFile.close();

                Message message = new Message();
                message.what = VALUE.READY;
                downloadListener.sendMessage(message);

                int blockSize = length / 5;
                for (int i = 0; i < 5; i++) {
                    int begin = i * blockSize;
                    int end = (i + 1) * blockSize;
                    if (i == 2) {
                        end = length;
                    }
                    DownLoadThread downLoadThread = new DownLoadThread(i, begin, end, file, url);
                    new Thread(downLoadThread).start();
                }

                Log.e("Location", file.getAbsolutePath());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
