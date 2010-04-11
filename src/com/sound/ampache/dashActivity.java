package com.sound.ampache;

/* Copyright (c) 2008 Kevin James Purdy <purdyk@onid.orst.edu>
 * Copyright (c) 2010 Jacob Alexander   < haata@users.sf.net >
 * Copyrigth (c) 2010 MTGap
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

import com.sound.ampache.R;
import android.app.Activity;
import android.app.SearchManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class dashActivity extends Activity implements OnClickListener {
    private static final int SEARCH_MUSIC = 100;
    private static final int SETTINGS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        setContentView(R.layout.dash);
	
        /* We need a listener for all of the buttons */
        Button temp;
        temp = (Button) findViewById(R.id.artists);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.albums);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.tags);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.playlists);
        temp.setOnClickListener(this);
        
        //If they tap the "Now Playing" section, take them to the playlist.
        findViewById(R.id.nowplaying).setOnClickListener(this);
        
        /* Verify a valid session */
        //amdroid.comm.ping();

        /*  We've tried to login, and failed, so present the user with the preferences pane */
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display song name in the "Now Playing" section.
        TextView st = (TextView) findViewById(R.id.title);
        String title = "";
        try {
            if (amdroid.mp.isPlaying()) {
                title = "Now Playing - " + amdroid.playlistCurrent.get(amdroid.playingIndex).name;
            } else {
                title = "Paused - " + amdroid.playlistCurrent.get(amdroid.playingIndex).name;
            }
        } catch(Exception e) {
            title = "No Song Selected";
        }

        st.setText(title);

        // Display song info in the "Now Playing" section
        TextView si = (TextView) findViewById(R.id.artist);
        String info = "";
        try {
            info = amdroid.playlistCurrent.get(amdroid.playingIndex).extraString();
        } catch(Exception e) {
            info = "Show current playlist";
        }

        si.setText(info);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, SEARCH_MUSIC, 0, "Search Music").setIcon(android.R.drawable.ic_menu_search);
        return true;
    }
    
    public void onClick(View v) {
        Intent intent = null;
        String[] dir = new String[2];
        dir[1] = "";

        switch (v.getId()) {
        case (R.id.artists):
            dir[0] = "artists";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Artists");
            break;

        case (R.id.albums):
            dir[0] = "albums";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Albums");
            break;

        case (R.id.tags):
            dir[0] = "tags";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Tags");
            break;

        case (R.id.playlists):
            dir[0] = "playlists";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Playlists");
            break;
            
        case (R.id.nowplaying):
            intent = new Intent().setClass(this, playlistActivity.class);
            break;

        default:
            return;
        }
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
        case SETTINGS:
            intent = new Intent().setClass(this, prefsActivity.class);
            break;
            
        case SEARCH_MUSIC:
            onSearchRequested();
            break;
            
        default:
            return false;
        }
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return false;
    }
}

// ex:tabstop=4 shiftwidth=4 expandtab:

