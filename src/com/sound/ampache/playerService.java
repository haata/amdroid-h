package com.sound.amdriod;

import android.app.Service;

import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class playerService extends Service {
    private IBinder mBinder;
    private boolean mServiceInUse = false;
   
    @Override
    public IBinder onBind(Intent intent) {
        //mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
        return mBinder;
    }
    
    @Override
    public void onRebind(Intent intent) {
        //mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
    }

}