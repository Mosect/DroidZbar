package com.mosect.android.droidzbar;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.Closeable;

/**
 * 安全的扫码器，处理多线程访问
 */
abstract class SafeScanner implements Closeable {

    private ImageScanner imageScanner; // zbar图像扫描器
    private Image image; // 一帧图像，正在扫描的图像
    private boolean handling = false;
    private boolean closed = false;
    private boolean wantClose = false;
    private final Object lock = new Object();

    SafeScanner(int width, int height, String format) {
        this.imageScanner = new ImageScanner();
        this.image = new Image(width, height, format);
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (!closed) {
                if (!handling) {
                    safeClose();
                } else {
                    if (!wantClose) {
                        wantClose = true;
                    }
                }
            }
        }
    }

    Image getImage() {
        return image;
    }

    /**
     * 处理数据，此方法在数据处理线程执行
     *
     * @param data 数据
     */
    void handleData(byte[] data) {
        // 先判断是否需要关闭
        synchronized (lock) {
            if (closed) {
                return;
            }
            if (wantClose) {
                safeClose();
                wantClose = false;
                return;
            }
            handling = true;
        }

        // 再处理数据
        if (null != data) { // 存在锁定数据
            image.setData(data); // 将锁定数据复制到图像上
            if (imageScanner.scanImage(image) != 0) { // 扫描成功
                // 循环查找所有结果
                SymbolSet symSet = imageScanner.getResults();
                if (null != symSet) {
                    for (Symbol sym : symSet) {
                        // 触发扫描结果回调
                        onScanResult(sym.getDataBytes());
                    }
                }
            }
        }

        // 处理完数据，需要再次判断是否需要关闭
        synchronized (lock) {
            handling = false;
            if (wantClose) {
                safeClose();
                wantClose = false;
            }
        }
    }

    private void safeClose() {
        imageScanner.destroy();
        image.destroy();
        closed = true;
    }

    /**
     * 扫描结果
     *
     * @param result 结果（一般为byte[]类型）
     */
    protected abstract void onScanResult(Object result);
}
