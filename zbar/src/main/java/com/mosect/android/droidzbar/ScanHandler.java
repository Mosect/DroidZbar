package com.mosect.android.droidzbar;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.Closeable;
import java.io.IOException;

/**
 * 扫描处理器，需要将此类对象添加到SurfaceHolder的回调才能工作：<br>
 * ScanHandler scanHandler = new ScanHandler() {...};<br>
 * SurfaceHolder surfaceHolder = surfaceView.getHolder();<br>
 * surfaceHolder.addCallback(scanHandler);<br>
 */
public abstract class ScanHandler implements SurfaceHolder.Callback, Camera.PreviewCallback, Closeable {

    private Scanner scanner;
    private Camera camera;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = onOpenCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 需要关闭旧扫描器
        if (null != scanner) {
            scanner.close();
            scanner = null;
        }

        if (null != camera) { // 存在相机，即打开相机成功
            onInitCamera(); // 初始化相机
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setPreviewCallbackWithBuffer(this); // 设置预览回调
            Camera.Size size = camera.getParameters().getPreviewSize(); // 获取预览图像大小
            // 计算Buffer大小
            int bufferLength = size.width * size.height * ImageFormat
                    .getBitsPerPixel(camera.getParameters().getPreviewFormat()) / 8;
            // 必须添加一个回调buffer
            camera.addCallbackBuffer(new byte[bufferLength]);
            // 创建扫描器
            scanner = onCreateScanner(size.width, size.height);
            if (null != scanner) {
                // 初始化扫描器
                onInitScanner();
            }
            onStart();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 只关闭相机
        if (null != camera) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (null != scanner) {
            scanner.postData(data); // 传输图像数据
        }
        // 必须添加回调buffer，不然onPreviewFrame不调用
        camera.addCallbackBuffer(data);
    }

    @Override
    public void close() {
        if (null != camera) {
            camera.release();
            camera = null;
        }
        if (null != scanner) {
            scanner.close();
            scanner = null;
        }
    }

    /**
     * 初始化相机
     */
    protected void onInitCamera() {

    }

    /**
     * 初始化扫描器
     */
    protected void onInitScanner() {
    }

    protected void onStart() {
    }

    /**
     * 获取扫描器
     *
     * @return 扫描器
     */
    public Scanner getScanner() {
        return scanner;
    }

    protected void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    protected void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * 获取相机
     *
     * @return 相机
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * 打开相机
     *
     * @return 相机
     */
    protected abstract Camera onOpenCamera();

    /**
     * 创建扫描器
     *
     * @param width  宽
     * @param height 高
     * @return 扫描器
     */
    protected abstract Scanner onCreateScanner(int width, int height);
}
