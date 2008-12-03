

package com.sound.ampache;

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

    public void onCreate() {
        //Debug.waitForDebugger();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mp = new MediaPlayer();

        playingIndex = 0;
        bufferPC = 0;

        cache = new Bundle();

        try {
            comm = new ampacheCommunicator(prefs, this);
            comm.perform_auth_request();
            requestHandler = comm.new ampacheRequestHandler();
            requestHandler.start();
        } catch (Exception poo) {
            
        }
        playlistCurrent = new ArrayList();
    }

}
