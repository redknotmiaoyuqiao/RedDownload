package com.redknot.reddownload;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.redknot.reddownload.listener.DownloadListener;
import com.redknot.reddownload.tool.DownloadSession;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText et_url_input;
    private Button btn_start;
    private SeekBar seekBar;

    private int totla = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        et_url_input.setText("http://192.168.56.1/fmscms.zip");
        btn_start.setOnClickListener(new MyOnClickListener());
    }

    private void init() {
        et_url_input = (EditText) findViewById(R.id.et_url_input);
        btn_start = (Button) findViewById(R.id.btn_start);
        seekBar = (SeekBar) findViewById(R.id.seek);
        seekBar.setMax(100);
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btn_start) {
                String url = et_url_input.getText().toString();
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaaaaa";

                MyDownloadListener myDownloadListener = new MyDownloadListener();

                DownloadSession session = new DownloadSession(myDownloadListener);
                session.start(url,savePath,5);
            }
        }
    }

    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onReady(File file) {
            Log.e("DownLoad", file.length() + " " + file.getAbsolutePath());
        }

        @Override
        public void onUpdate(int process) {
            Log.e("DownLoad", "update:" + process);
            seekBar.setProgress(process);
        }

        @Override
        public void onComplete() {
            Log.e("DownLoad", "complete");
        }
    }


}
