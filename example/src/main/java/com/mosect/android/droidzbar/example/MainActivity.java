package com.mosect.android.droidzbar.example;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mosect.android.droidzbar.ScanCallback;
import com.mosect.android.droidzbar.ScanHandler;
import com.mosect.android.droidzbar.Scanner;

public class MainActivity extends AppCompatActivity implements ScanCallback {

    private Handler handler;

    private SurfaceView svContent;
    private TextView tvInfo;
    private Button btnScan;
    private String scanResult;
    private ScanHandler scanHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tv_info);
        svContent = findViewById(R.id.sv_content);
        btnScan = findViewById(R.id.btn_scan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != scanHandler.getScanner()) {
                    if (scanHandler.getScanner().isScanning()) {
                        scanHandler.getScanner().stop();
                    } else {
                        scanHandler.getScanner().start();
                    }
                }
            }
        });

        scanHandler = new ScanHandler() {
            @Override
            protected Camera onOpenCamera() {
                return Camera.open();
            }

            @Override
            protected Scanner onCreateScanner(int width, int height) {
                return new Scanner(width, height, "Y800");
            }

            @Override
            protected void onInitCamera() {
                super.onInitCamera();
                getCamera().setDisplayOrientation(90);
            }

            @Override
            protected void onInitScanner() {
                super.onInitScanner();
                getScanner().setScanCallback(MainActivity.this);
            }

            @Override
            protected void onStart() {
                super.onStart();
                getCamera().startPreview();
                autoFocus();
            }
        };
        svContent.getHolder().addCallback(scanHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != scanHandler) {
            scanHandler.close();
            scanHandler = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != scanHandler.getScanner() && scanHandler.getScanner().isScanning()) {
            scanHandler.getScanner().stop();
        }
    }

    @Override
    public void onScanStart() {
        scanResult = null;
        tvInfo.setText("扫描中，请稍后……");
        btnScan.setText("扫码中……");
    }

    @Override
    public void onScanStop() {
        if (null == scanResult) {
            tvInfo.setText("");
        }
        btnScan.setText("开始扫码");
    }

    @Override
    public void onScanResult(Object result) {
        if (null != result) {
            if (result instanceof byte[]) {
                scanResult = new String((byte[]) result);
                tvInfo.setText("扫码结果：\n" + scanResult);
            }
        }
        if (null != scanHandler.getScanner()) {
            scanHandler.getScanner().stop();
        }
    }

    private void autoFocus() {
        if (null != scanHandler.getCamera()) {
            scanHandler.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    autoFocusDelayed();
                }
            });
        }
    }

    private void autoFocusDelayed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoFocus();
            }
        }, 2000);
    }
}
