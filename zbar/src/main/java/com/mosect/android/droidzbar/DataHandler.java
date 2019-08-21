package com.mosect.android.droidzbar;

/**
 * 扫描数据处理器
 */
public abstract class DataHandler extends SafeScanner implements Runnable {

    private SwapBuffer swapBuffer;
    private boolean running; // 是否正在运行

    /**
     * 创建扫描数据处理器
     *
     * @param width  图像宽
     * @param height 图像高
     */
    public DataHandler(int width, int height, String format) {
        super(width, height, format);
        this.swapBuffer = new SwapBuffer();
    }

    /**
     * 传输图像数据
     *
     * @param data 图像数据
     */
    public void postData(byte[] data) {
        // 将传输进来的数据复制到缓存数据上
        if (null != data) {
            byte[] buffer = swapBuffer.startWrite(data.length);
            System.arraycopy(data, 0, buffer, 0, buffer.length);
            swapBuffer.endWrite();
        }
    }

    /**
     * 扫描核心代码
     */
    @Override
    public void run() {
        // 开启循环不断扫描
        while (running) {
            if (swapBuffer.isEmpty()) { // 不存在数据，停止线程
                // 标记为不在运行
                // 小睡一会等待数据
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                byte[] data = swapBuffer.startRead();
                handleData(data);
                swapBuffer.endRead();
            }
        }
    }

    @Override
    public void close() {
        super.close();
        if (running) {
            running = false;
        }
    }

    public void startLoop() {
        if (!running) {
            running = true;
            new Thread(this).start();
        }
    }

    public void stopLoop() {
        if (running) {
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }
}
