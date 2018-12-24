# Zbar
相关Zbar，请查看：http://zbar.sourceforge.net

# DroidZbar
基于zbar库实现的Android平台扫码功能。提供了以下4个类：
* DataHandler 数据处理，扫描图像数据，触发回调，内部有自身线程管理。
* Scanner 扫描器，提供开始、结束操作，以及扫描回调。使用DataHandler实现扫码功能。
* ScanCallback 扫描回调，Scanner的回调类。
* ScanHandler 扫描处理，封装了Scanner的使用，将Scanner与Camera、SurfaceHolder连接起来。

## 使用
### Gradle
```
compile 'com.mosect:DroidZbar:1.0.0'
```
### Maven
```
<dependency>
  <groupId>com.mosect</groupId>
  <artifactId>DroidZbar</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

## DataHandler
扫码数据处理，创建需要提供宽、高和图像格式。一般，安卓默认的相机预览图像格式是"Y800"，如需了解更多格式，可以查询以下文章：
https://blog.csdn.net/bbdxf/article/details/79356259  
提供以下方法：
```
/**
 * 创建扫描数据处理器
 *
 * @param width  图像宽
 * @param height 图像高
 */
public DataHandler(int width, int height, String format) {...}

/**
 * 传输图像数据
 *
 * @param data 图像数据
 */
public void postData(byte[] data) {...}

/**
 * 摧毁数据处理器
 */
public void destroy() {...}
```

## Scanner
封装了对DataHandler的使用，提供start（开始扫描）、stop（介绍扫描）、close（关闭扫描器）、setScanCallback（设置回调）方法。回调里所有被触发的方法都在主线程。

## ScanCallback
```
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
```

## ScanHandler
为了更好使用相机扫码，封装了此类，里面将Scanner与Camera和SurfaceHolder联系起来。
```
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
public Camera getCamera() {...}

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
```
具体使用方法：
```
ScanHandler scanHandler = new ScanHandler() {...};
SurfaceHolder surfaceHolder = surfaceView.getHolder();
surfaceHolder.addCallback(scanHandler);
```
其他控制流程，需要根据实际情况使用getScanner方法对Scanner进行操作。

# 联系方式
```
邮箱：zhouliuyang1995@163.com
QQ：905340954
个人主页：http://www.mosect.com:5207
```
