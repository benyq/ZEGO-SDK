package com.zegocloud.demo.bestpractice.internal;

import android.annotation.SuppressLint;
import android.opengl.EGL14;
import android.util.Log;

import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.data.FaceUnityDataFactory;
import com.faceunity.nama.listener.FURendererListener;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoCustomVideoProcessHandler;
import im.zego.zegoexpress.constants.ZegoPublishChannel;


public class FaceunityVideoProcess extends IZegoCustomVideoProcessHandler {

    private static final String TAG = FaceunityVideoProcess.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static FaceunityVideoProcess instance;

    public static FaceunityVideoProcess getInstance() {
        if (instance == null) {
            synchronized (FaceunityVideoProcess.class) {
                if (instance == null) {
                    instance = new FaceunityVideoProcess();
                }
            }
        }
        return instance;
    }

    private FaceunityVideoProcess() {
        faceUnityDataFactory = new FaceUnityDataFactory(-1);
    }

    private ZegoExpressEngine expressEngine;
    private FaceUnityDataFactory faceUnityDataFactory;

    // 当zegoExpressEngine小销毁时调用
    public synchronized void release() {
        instance = null;
        expressEngine = null;
        faceUnityDataFactory = null;
        FURenderKit.getInstance().releaseSafe();
    }

    public void setExpressEngine(ZegoExpressEngine expressEngine) {
        this.expressEngine = expressEngine;
    }


    public FaceUnityDataFactory getFaceUnityDataFactory() {
        return faceUnityDataFactory;
    }

    @Override
    public void onStart(ZegoPublishChannel channel) {
        Log.i("CustomVideoProcess", "Start: " + EGL14.eglGetCurrentContext());
        FURenderer.getInstance().prepareRenderer(null);
        FURenderKit.getInstance().clearCacheResource();
        faceUnityDataFactory.bindCurrentRenderer();
    }

    @Override
    public void onStop(ZegoPublishChannel channel) {
        Log.i("CustomVideoProcess", "Stop: " + EGL14.eglGetCurrentContext());
    }

    @Override
    public void onCapturedUnprocessedTextureData(int textureID, int width, int height, long referenceTimeMillisecond, ZegoPublishChannel channel) {
        if (expressEngine == null) {
            Log.e(TAG, "onCapturedUnprocessedTextureData fail: expressEngine is null");
            return;
        }
        Log.d(TAG, "onCapturedUnprocessedTextureData: witth: " + width + ", height: " + height);
        int newTexId = FURenderer.getInstance().onDrawFrameDualInput(null, textureID, width, height);
        expressEngine.sendCustomVideoProcessedTextureData(newTexId, width, height, referenceTimeMillisecond);
    }

}
