package com.zegocloud.demo.bestpractice;

import com.faceunity.nama.FURenderer;

public class App extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FURenderer.getInstance().setup(this);
    }
}
