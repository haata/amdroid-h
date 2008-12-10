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

import com.sound.ampache.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class dashActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dash);

        /* We need a listener for all of the buttons */
        
        Button temp;
        temp = (Button) findViewById(R.id.artists);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.albums);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.songs);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.playlists);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.player);
        temp.setOnClickListener(this);
        temp = (Button) findViewById(R.id.settings);
        temp.setOnClickListener(this); 
        
        /* Verify a valid session */
        amdroid.comm.ping();

        /*  We've tried to login, and failed, so present the user with the preferences pane */
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return;
        }


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

        case (R.id.songs):
            dir[0] = "songs";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Songs");
            break;

        case (R.id.playlists):
            dir[0] = "playlists";
            intent = new Intent().setClass(this, collectionActivity.class).putExtra("directive", dir).putExtra("title", "Playlists");
            break;

        case (R.id.player):
            intent = new Intent().setClass(this, playlistActivity.class);
            break;

        case (R.id.settings):
            intent = new Intent().setClass(this, prefsActivity.class);
            break;

        default:
        }
        startActivity(intent);
    }

}
