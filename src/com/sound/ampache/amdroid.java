

package com.sound.ampache;

import com.sound.ampache.ampacheCommunicator;
import com.sound.ampache.ampacheCommunicator.ampacheRequestHandler;
import android.app.Application;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.ArrayList;
import com.sound.ampache.objects.*;
import android.os.Debug;

public final class amdroid extends Application {

    public static ampacheCommunicator comm;
    public static ampacheRequestHandler requestHandler;

    public static ArrayList<Song> playlistCurrent;

    public void onCreate() {
        //Debug.waitForDebugger();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        try {
            comm = new ampacheCommunicator(PreferenceManager.getDefaultSharedPreferences(this), this);
            comm.perform_auth_request();
            requestHandler = comm.new ampacheRequestHandler();
            requestHandler.start();
        } catch (Exception poo) {
            
        }
        playlistCurrent = new ArrayList();
    }

}
