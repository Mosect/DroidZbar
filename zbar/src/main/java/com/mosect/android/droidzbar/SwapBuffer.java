package com.mosect.android.droidzbar;

class SwapBuffer {

    private boolean wrote = false;
    private boolean read = false;
    private byte[] freeBuffer; // 空闲的buffer
    private byte[] okBuffer; // 可用buffer
    private byte[] readyBuffer; // 已经准备好的buffer

    byte[] startWrite(int size) {
        synchronized (this) {
            if (size <= 0) {
                throw new IllegalArgumentException("size=" + size);
            }
            if (wrote) {
                throw new IllegalStateException("Wrote!");
            }
            if (null == freeBuffer || freeBuffer.length != size) {
                System.out.println("zbar.SwapBuffer:newBuffer");
                freeBuffer = new byte[size];
            }
            wrote = true;
            return freeBuffer;
        }
    }

    void endWrite() {
        synchronized (this) {
            if (null == freeBuffer || !wrote) {
                throw new IllegalStateException("No call startWrite!");
            }
            wrote = false;
            byte[] cache = readyBuffer;
            readyBuffer = freeBuffer;
            freeBuffer = cache;
            if (!read) {
                // 不在读取中
                swap();
            }
        }
    }

    byte[] startRead() {
        synchronized (this) {
            if (read) {
                throw new IllegalStateException("Read!");
            }
            read = true;
            return okBuffer;
        }
    }

    void endRead() {
        synchronized (this) {
            if (!read) {
                throw new IllegalStateException("No call startRead!");
            }
            read = false;
            if (!wrote) {
                // 不在写入中
                swap();
            }
        }
    }

    boolean isEmpty() {
        return null == okBuffer && null == readyBuffer;
    }

    private void swap() {
        byte[] temp = okBuffer;
        okBuffer = readyBuffer;
        readyBuffer = null;
        if (null != temp) {
            freeBuffer = temp;
        }
    }
}
