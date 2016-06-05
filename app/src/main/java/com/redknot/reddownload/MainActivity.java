package com.redknot.reddownload;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText et_url_input;
    private Button btn_start;
    private int totla = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        et_url_input.setText("http://192.168.56.1/down.zip");
        btn_start.setOnClickListener(new MyOnClickListener());
    }

    private void init() {
        et_url_input = (EditText) findViewById(R.id.et_url_input);
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btn_start) {
                new Thread(new GetLengthThread(et_url_input.getText().toString())).start();
            }
        }
    }

    synchronized void update(int add){
        totla = totla + add;
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
                byte[] buff = new byte[1024];

                RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw");
                randomAccessFile.seek(this.begin);

                int len = 0;
                while ((len = is.read(buff)) != -1) {
                    randomAccessFile.write(buff, 0, len);
                    update(len);
                    Log.e("update",totla + "");
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

                if (length < 0) {
                    return;
                }

                //创建文件
                File file = new File(Environment.getExternalStorageDirectory(), "miao");

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(length);

                randomAccessFile.close();

                int blockSize = length / 3;
                for (int i = 0; i < 3; i++) {
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
