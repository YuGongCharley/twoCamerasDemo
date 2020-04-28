package com.charleyszc.twocamerasdemo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.charleyszc.twocamerasdemo.R;

import com.charleyszc.twocamerasdemo.utils.PreviewImage;
import java.io.IOException;

/**
 * Created by szc on 2019/05/13
 */
public class MainActivity extends BaseActivity{

    Camera redCamera;
    Camera colorCamera;
    SurfaceView surfaceViewRed;
    SurfaceView surfaceViewColor;
    SurfaceHolder surfaceHolderRed;
    SurfaceHolder surfaceHolderColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceViewRed = findViewById(R.id.redsurface);
        surfaceViewColor = findViewById(R.id.colorsurface);
        initView();
        //动态权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
            initRedCamera(); //0
            initColorCamera(); //1

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {//new add
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    //hide title&navigation
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }


    private void initView() {
        surfaceHolderRed = surfaceViewRed.getHolder();
        surfaceHolderColor = surfaceViewColor.getHolder();
    }


    //打开红外色相机
    private void initRedCamera() {
        redCamera = Camera.open(0);
        redCamera.setDisplayOrientation(90);

        Camera.Parameters parameters = redCamera.getParameters();
        final int w = parameters.getPreviewSize().width;
        final int h = parameters.getPreviewSize().height;
        redCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                PreviewImage.setRedPic(data, w, h);
            }
        });
        redCamera.startPreview();   //显示相机

        System.out.println("打开摄像机");
        surfaceHolderRed.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    redCamera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = redCamera.getParameters();
                    final int w = parameters.getPreviewSize().width;
                    final int h = parameters.getPreviewSize().height;
                    redCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            PreviewImage.setColorPic(data, w, h);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                redCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }

    public void initColorCamera() {
        colorCamera = Camera.open(1);
        colorCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = colorCamera.getParameters();
        final int w = parameters.getPreviewSize().width;
        final int h = parameters.getPreviewSize().height;
        colorCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                PreviewImage.setColorPic(data, w, h);
            }
        });
        colorCamera.startPreview();   //显示相机

        surfaceHolderColor.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    colorCamera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = colorCamera.getParameters();
                    final int w = parameters.getPreviewSize().width;
                    final int h = parameters.getPreviewSize().height;
                    colorCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            PreviewImage.setColorPic(data, w, h);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                colorCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (redCamera != null) {
            redCamera.setPreviewCallback(null);
            redCamera.stopPreview();
            redCamera.release();
            redCamera = null;
        }
        if (colorCamera != null) {
            colorCamera.setPreviewCallback(null);
            colorCamera.stopPreview();
            colorCamera.release();
            colorCamera = null;
        }

    }



}
