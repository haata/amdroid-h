package com.sound.ampache;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.List;
import java.util.ArrayList;
import java.net.*;
import java.io.*;


public final class collectionActivity extends ListActivity
{
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        setListAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, strings));

        /*
        Intent intent = getIntent();
        String path = intent.getStringExtra("com.sound.apache.Path");

        if (path == null) {
            path = "";
        }

        
        setListAdapter(new SimpleAdapter( this, getData(path),
                                          android.R.layout.simple_list_item_1, 
                                          new String[] { "title" },
                                          new int[] { android.R.id.text1 }));
        */
    }

    /*
    protected List getData(String prefix) {
        List<String> data = new ArrayList<String>();
        int len = strings.size();
        for (int i = 0; i < len; i++) {
            String str = strings[i];
            addItem(data, str);
        }
    
        } */

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String val = (String) l.getItemAtPosition(position);
        Toast.makeText(this, val, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (item.getItemId()) {
        case R.id.prefs:
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return true;
        case R.id.playing:
            Intent playIntent = new Intent().setClass(this, playlistActivity.class);
            startActivity(playIntent);
            return true;
        case R.id.collection:
            try {
                URL myurl = new URL("http://lt-dan.sandbenders.org/");
                BufferedReader reader = new BufferedReader(new InputStreamReader(myurl.openStream()));
                Toast.makeText(this, reader.readLine(), Toast.LENGTH_SHORT).show();
            } catch (java.io.IOException exc) {
                Toast.makeText(this, exc.toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource("http://lt-dan.sandbenders.org/ampache/play/index.php?song=18548&uid=2&sid=0145935e8732f0cc21407a3b5e451ed9&name=/The%20%20Rapture%20-%20First%20Gear.mp3");
            mp.prepare();
            mp.start();
        } catch (java.io.IOException blah) {
            Toast.makeText(this, blah.toString(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private String[] strings = {"1", "2", "3", "4"};
}
