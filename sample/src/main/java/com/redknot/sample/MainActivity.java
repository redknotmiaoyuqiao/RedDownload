package com.redknot.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.redknot.reddownload.listener.DownloadListener;
import com.redknot.reddownload.tool.DownloadSession;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText et_url;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        et_url = (EditText) findViewById(R.id.et_url);
        btn_start = (Button) findViewById(R.id.btn_start);

        et_url.setText("");

        btn_start.setOnClickListener(new MyOnCliclListener());
    }

    private class MyOnCliclListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String url = et_url.getText().toString();
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mimimiao";

            DownloadSession downloadSession = new DownloadSession(new MyDownloadListener());
            downloadSession.start(url,savePath,5);
        }
    }

    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onReady(File file) {

        }

        @Override
        public void onUpdate(int process) {

        }

        @Override
        public void onComplete() {

        }
    }
}
