package com.sound.ampache;

/* Copyright (c) 2008 Kevin James Purdy <purdyk@onid.orst.edu>
 *
 * +------------------------------------------------------------------------+
 * | This program is free software; you can redistribute it and/or          |
 * | modify it under the terms of the GNU General Public License            |
 * | as published by the Free Software Foundation; either version 2         |
 * | of the License, or (at your option) any later version.                 |
 * |                                                                        |
 * | This program is distributed in the hope that it will be useful,        |
 * | but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 * | MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          |
 * | GNU General Public License for more details.                           |
 * |                                                                        |
 * | You should have received a copy of the GNU General Public License      |
 * | along with this program; if not, write to the Free Software            |
 * | Foundation, Inc., 59 Temple Place - Suite 330,                         |
 * | Boston, MA  02111-1307, USA.                                           |
 * +------------------------------------------------------------------------+
 */

import com.sound.ampache.ampacheCommunicator;
import com.sound.ampache.ampacheCommunicator.ampacheRequestHandler;
import android.app.Application;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.ArrayList;
import com.sound.ampache.objects.*;
import android.os.Debug;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.media.AudioManager;
import android.content.Context;

public final class amdroid extends Application {

    public static ampacheCommunicator comm;
    public static ampacheRequestHandler requestHandler;
    public static SharedPreferences prefs;
    public static ArrayList<Song> playlistCurrent;
    public static MediaPlayer mp;
    public static int playingIndex;
    public static int bufferPC;
    public static Boolean playListVisible;
    public static Boolean confChanged;
    protected static Bundle cache;
    private static Boolean mResumeAfterCall = false;

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

    public void onCreate() {
        //Debug.waitForDebugger();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mp = new MediaPlayer();

        playingIndex = 0;
        bufferPC = 0;

        cache = new Bundle();
        
        //Make sure we check for phone calls
        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        try {
            comm = new ampacheCommunicator(prefs, this);
            comm.perform_auth_request();
            requestHandler = comm.new ampacheRequestHandler();
            requestHandler.start();
        } catch (Exception poo) {
            
        }
        playlistCurrent = new ArrayList();
    }

    public void onDestroy() {
        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, 0);
    }
}
