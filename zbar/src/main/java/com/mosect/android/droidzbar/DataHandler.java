package com.mosect.android.droidzbar;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/**
 * 扫描数据处理器
 */
public abstract class DataHandler implements Runnable {

    private ImageScanner imageScanner; // zbar图像扫描器
    private Image image; // 一帧图像，正在扫描的图像

    private byte[] lockedData; // 锁定的数据，正在扫描的数据
    private byte[] bufferData; // 缓存数据，传输进来的数据

    private int postCount; // 传输进来的次数，未处理数据次数
    private boolean running; // 是否正在运行


    /**
     * 创建扫描数据处理器
     *
     * @param width  图像宽
     * @param height 图像高
     */
    public DataHandler(int width, int height, String format) {
        this.imageScanner = new ImageScanner();
        this.image = new Image(width, height, format);
    }

    /**
     * 传输图像数据
     *
     * @param data 图像数据
     */
    public void postData(byte[] data) {
        synchronized (this) {
            // 将传输进来的数据复制到缓存数据上
            if (null == data) {
                bufferData = null;
            } else {
                if (null == bufferData || bufferData.length != data.length) {
                    bufferData = new byte[data.length];
                }
                System.arraycopy(data, 0, bufferData, 0, bufferData.length);
            }
            // 将未处理的次数加1
            postCount++;
            // 如果不在运行中，开启扫描线程执行扫描
            if (!running) {
                new Thread(this).run();
            }
        }
    }

    /**
     * 扫描核心代码
     */
    @Override
    public void run() {
        // 标记正在运行
        synchronized (this) {
            running = true;
        }
        // 开启循环不断扫描
        while (true) {
            boolean has = false; // 是否有数据可以扫描
            synchronized (this) {
                // 如果存在未处理的缓存数据，将缓存数据复制到锁定数据上
                if (postCount > 0) {
                    postCount = 0;
                    has = true;
                    if (null != bufferData) {
                        if (null == lockedData || lockedData.length != bufferData.length) {
                            lockedData = new byte[bufferData.length];
                        }
                        System.arraycopy(bufferData, 0, lockedData, 0, lockedData.length);
                    }
                }
            }
            if (has) { // 存在数据
                handleData(); // 处理数据
            } else { // 不存在数据，停止线程
                // 标记为不在运行
                synchronized (this) {
                    running = false;
                }
                break;
            }
        }
    }

    /**
     * 摧毁数据处理器
     */
    public void destroy() {
        synchronized (this) {
            if (null != image) {
                image.destroy();
                image = null;
            }
            if (null != imageScanner) {
                imageScanner.destroy();
                imageScanner = null;
            }
        }
    }

    /**
     * 处理数据
     */
    private void handleData() {
        if (null != lockedData) { // 存在锁定数据
            image.setData(lockedData); // 将锁定数据复制到图像上
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
    }

    /**
     * 扫描结果
     *
     * @param result 结果（一般为byte[]类型）
     */
    abstract void onScanResult(Object result);
}
