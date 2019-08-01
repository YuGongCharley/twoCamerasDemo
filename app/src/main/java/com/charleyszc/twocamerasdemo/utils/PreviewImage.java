package com.charleyszc.twocamerasdemo.utils;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PreviewImage {

    private static ReentrantReadWriteLock colorLock = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock redLock = new ReentrantReadWriteLock();
    public static PreviewImage colorData = new PreviewImage();
    public static PreviewImage redData = new PreviewImage();
    public int mWidth = 0;
    public int mHeight = 0;
    public byte[] mData = null;


    public static void setColorPic(byte[] data,int w,int h){
        colorLock.writeLock().lock();
        colorData.mData=data;
        colorData.mWidth=w;
        colorData.mHeight=h;
        colorLock.writeLock().unlock();
    }


    public static PreviewImage getColorPic() {
        PreviewImage colorImage=new PreviewImage();
        colorLock.readLock().lock();
        colorImage.mWidth=colorData.mWidth;
        colorImage.mHeight=colorData.mHeight;
        if (colorData.mData!=null){
            colorImage.mData=new byte[colorData.mData.length];
            System.arraycopy(colorData.mData,0,colorImage.mData,0,colorData.mData.length);
        }
        colorLock.readLock().unlock();
        return colorImage;
    }

    public static void setRedPic(byte[] data,int w,int h){
        redLock.writeLock().lock();
        redData.mData=data;
        redData.mWidth=w;
        redData.mHeight=h;
        redLock.writeLock().unlock();
    }


    public static PreviewImage getRedPic(){
        PreviewImage redImage=new PreviewImage();
        redLock.readLock().lock();
        redImage.mWidth=redData.mWidth;
        redImage.mHeight=redData.mHeight;
        if (redData.mData!=null){
            redImage.mData=new byte[redData.mData.length];
            System.arraycopy(redData.mData,0,redImage.mData,0,redData.mData.length);
        }
        redLock.readLock().unlock();
        return redImage;
    }

}
