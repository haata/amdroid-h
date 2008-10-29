

package com.sound.ampache;

import com.sound.ampache.ampacheCommunicator;
import android.app.Application;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.ArrayList;
import com.sound.ampache.objects.*;

public final class amdroid extends Application {

    public static ampacheCommunicator comm;

    public static ArrayList<Song> playlistCurrent;

    public void onCreate() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        try {
            comm = new ampacheCommunicator(PreferenceManager.getDefaultSharedPreferences(this), this);
            comm.perform_auth_request();
        } catch (Exception poo) {
            
        }
        playlistCurrent = new ArrayList();
    }

}