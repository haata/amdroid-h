package com.sound.ampache;

import com.sound.ampache.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class prefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    protected void onDestroy() {
        /* we want to tell other activities that we need to reload */
        super.onDestroy();
        amdroid.confChanged = true;
        amdroid.comm.authToken = null;
    }
}

