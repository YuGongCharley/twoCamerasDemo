package com.charleyszc.twocamerasdemo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.charleyszc.twocamerasdemo.R;
import com.charleyszc.twocamerasdemo.myinterface.ToDetectionBitmap;
import com.charleyszc.twocamerasdemo.utils.CameraThread;
import com.charleyszc.twocamerasdemo.utils.PreviewImage;

import java.io.IOException;

import static com.charleyszc.twocamerasdemo.utils.ContantsUtils.GETCOLORPIC;
import static com.charleyszc.twocamerasdemo.utils.ContantsUtils.GETREDPIC;

/**
 * Created by szc on 2019/05/13
 */
public class MainActivity extends BaseActivity implements Handler.Callback {

    Camera redCamera;
    Camera colorCamera;
    CameraThread thread;
    Handler handler;
    SurfaceView surfaceViewRed;
    SurfaceView surfaceViewColor;
    SurfaceHolder surfaceHolderRed;
    SurfaceHolder surfaceHolderColor;
    ToDetectionBitmap detectioninfo;

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
        }else {
            initRedCamera(); //0
            initColorCamera(); //1
            setRedCamera(redCamera);
            setColorCamera(colorCamera);
        }
        initThread();
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
    }

    public void setColorCamera(Camera camera) {
        this.colorCamera = camera;
        System.out.println("打开摄像机");
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

    public void setRedCamera(Camera camera) {
        this.redCamera = camera;
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

    private void initThread() {
        thread = new CameraThread();
        handler = new Handler(this);
        thread.setContextAndHolder(this, handler);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (thread != null) {
            System.out.println("启动  camera Thread");
            thread.startCameraThread();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            thread.stopCameraThread();
        }
        if (redCamera != null) {
            redCamera.setPreviewCallback(null);
            redCamera.stopPreview();
            redCamera.release();
            redCamera = null;
        }

        shutDownColor();
    }

    public void shutDownColor() {
        if (colorCamera != null) {
            colorCamera.setPreviewCallback(null);
            colorCamera.stopPreview();
            colorCamera.release();
            colorCamera = null;
        }
    }

    public static boolean isOpenCamera;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case GETREDPIC:
                LogUtils.i("红外");
                if (!isOpenCamera) {
                    colorCamera.startPreview();   //显示相机
                    thread.isShowColorCamera = true;
                }
                break;

            case GETCOLORPIC:   //彩色相机)
                Bitmap bitmap = (Bitmap) msg.obj;
                detectioninfo.setBitmap(bitmap);
                break;
            default:
                break;
        }
        return false;
    }
}
