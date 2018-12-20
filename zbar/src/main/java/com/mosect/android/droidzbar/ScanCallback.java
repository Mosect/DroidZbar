package com.mosect.android.droidzbar;

/**
 * 扫描回调，所有回调都是在主线程执行
 */
public interface ScanCallback {

    /**
     * 扫描开始
     */
    void onScanStart();

    /**
     * 扫描介绍
     */
    void onScanStop();

    /**
     * 扫描到有效结果
     *
     * @param result 结果（一般为byte[]）
     */
    void onScanResult(Object result);
}