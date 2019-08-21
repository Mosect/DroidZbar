package com.mosect.android.droidzbar;

import android.os.Handler;

import java.io.Closeable;

/**
 * 扫描器
 */
public class Scanner implements Closeable {

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
        this(width, height, format, null);
    }

    /**
     * 创建扫描器
     *
     * @param width  宽
     * @param height 高
     * @param format 格式
     */
    public Scanner(int width, int height, String format, int[] crop) {
        this.handler = new Handler();
        this.dataHandler = new DataHandler(width, height, format) {
            @Override
            protected void onScanResult(Object result) {
                notifyResult(result);
            }
        };
        if (null != crop && crop.length == 4) {
            this.dataHandler.getImage().setCrop(crop[0], crop[1], crop[2], crop[3]);
        }
    }

    public ScanCallback getScanCallback() {
        return scanCallback;
    }

    public void setScanCallback(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    public void start() {
        if (!isScanning()) {
            dataHandler.startLoop();
            notifyStart();
        }
    }

    public void stop() {
        if (isScanning()) {
            dataHandler.stopLoop();
            notifyCancel();
        }
    }

    public boolean isScanning() {
        return dataHandler.isRunning();
    }

    @Override
    public void close() {
        if (null != dataHandler) {
            dataHandler.close();
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
