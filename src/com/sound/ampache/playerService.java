package com.sound.amdriod;

import android.app.Service;

import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.media.AudioManager;
import android.content.Context;
import java.util.ArrayList;

import com.sound.ampache.objects.*;

public class playerService extends Service {
    private IBinder mBinder;
    private static boolean mServiceInUse = false;
    private ArrayList<Song> playlistCurrent;
    private MediaPlayer mp;
    private static Boolean mResumeAfterCall;
   
    //Handle phone calls                                                                                         
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    int ringvolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    if (ringvolume > 0) {
                        mResumeAfterCall = (mp.isPlaying() || mResumeAfterCall);
                        mp.pause();
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    // pause the music while a conversation is in progress                                       
                    mResumeAfterCall = (mp.isPlaying() || mResumeAfterCall);
                    mp.pause();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    // start playing again                                                                       
                    if (mResumeAfterCall) {
                        // resume playback only if music was playing                                             
                        // when the call was answered                                                            
                        mp.start();
                        mResumeAfterCall = false;
                    }
                }
            }
        };


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

    @Override
    public void onCreate() {
        mp = new MediaPlayer();

    }
}