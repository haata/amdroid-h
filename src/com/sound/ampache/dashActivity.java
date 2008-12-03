package com.sound.ampache;

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
        temp = (Button)findViewById(R.id.artists);
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

        // Verify a valid session. The if statement should probably not be there.                                      
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null)
            amdroid.comm.ping();

        // We've tried to login, and failed, so present the user with the preferences pane                             
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
