package com.mosect.android.droidzbar;

import android.os.Handler;

import java.io.Closeable;

/**
 * 扫描器
 */
public class Scanner implements Closeable {

    private boolean scanning;
    private ScanCallback scanCallback;
    private Handler handler;
    private DataHandler dataHandler;


    /**
     * 创建扫描器
     *
     * @param width  宽
     * @param height 高
     * @param format 格式
     */
    public Scanner(int width, int height, String format) {
        this.handler = new Handler();
        this.dataHandler = new DataHandler(width, height, format) {
            @Override
            void onScanResult(Object result) {
                notifyResult(result);
            }
        };
    }

    public ScanCallback getScanCallback() {
        return scanCallback;
    }

    public void setScanCallback(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    public void start() {
        if (!isScanning()) {
            scanning = true;
            notifyStart();
        }
    }

    public void stop() {
        if (isScanning()) {
            scanning = false;
            notifyCancel();
        }
    }

    public boolean isScanning() {
        return scanning;
    }

    @Override
    public void close() {
        if (null != dataHandler) {
            dataHandler.destroy();
            dataHandler = null;
        }
    }

    /**
     * 传输图像数据
     *
     * @param data 图像数据
     */
    public void postData(byte[] data) {
        if (isScanning()) {
            dataHandler.postData(data);
        }
    }

    private void notifyResult(final Object result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != scanCallback) {
                    scanCallback.onScanResult(result);
                }
            }
        });
    }

    private void notifyStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != scanCallback) {
                    scanCallback.onScanStart();
                }
            }
        });
    }

    private void notifyCancel() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != scanCallback) {
                    scanCallback.onScanStop();
                }
            }
        });
    }

}
