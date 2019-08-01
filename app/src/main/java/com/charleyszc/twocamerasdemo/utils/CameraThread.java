package com.charleyszc.twocamerasdemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.LogUtils;

import java.io.ByteArrayOutputStream;

import static com.charleyszc.twocamerasdemo.utils.ContantsUtils.GETCOLORPIC;

public class CameraThread implements Runnable {

    private Context context;
    private volatile Thread mainLoop = null;
    private Boolean shouldStop = false;    //线程是否开启
    boolean isShowRedCamera;    //检测红外摄像头
    private Handler mHandler;
    public static boolean isShowColorCamera;   //开启彩色摄像头

    public void setContextAndHolder(Context context, Handler handler) {
        this.context = context;
        this.mHandler=handler;
    }

    public void startCameraThread() {
        shouldStop = false;
        if (mainLoop == null) {
            mainLoop = new Thread(this);
            mainLoop.start();
        }
    }

    public void stopCameraThread() {
        shouldStop = true;
        if (mainLoop != null) {
            mainLoop.interrupt();
            try {
                mainLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mainLoop = null;
    }

    @Override
    public void run() {
        while (!shouldStop) {
            if (isShowColorCamera && isShowRedCamera){
                LogUtils.i("彩色相机启用");
                PreviewImage colorData = PreviewImage.getColorPic();
                if (colorData.mData != null && colorData.mWidth > 0 && colorData.mHeight > 0){
                    Bitmap colorBitmap = yuvImage(colorData.mData, colorData.mWidth, colorData.mHeight);
                    Message message=new Message();
                    message.what=GETCOLORPIC;
                    message.obj=colorBitmap;
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    public Bitmap yuvImage(byte[] d, int w, int h) {
        Rect rect = new Rect(0, 0, w, h);
        YuvImage yuvImg = new YuvImage(d, 17, w, h, null);
        ByteArrayOutputStream byteImg = new ByteArrayOutputStream();
        yuvImg.compressToJpeg(rect, 100, byteImg);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteImg.toByteArray(), 0, byteImg.size());
        bitmap = rotateBitmap(bitmap, 90);//旋转
        bitmap = ImageUtils.getFlipBitmap(bitmap); //镜像
        if (bitmap != null) {
            return bitmap;
        } else {
            return null;
        }

    }

    private static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }



}
