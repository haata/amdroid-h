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
import java.io.*;
import com.sound.ampache.ampacheCommunicator;
import com.sound.ampache.objects.*;

public final class collectionActivity extends ListActivity
{
    
    ampacheCommunicator comm;
    List myList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        try {
            comm = new ampacheCommunicator(PreferenceManager.getDefaultSharedPreferences(this));
            comm.perform_auth_request();
            myList = comm.fetch("artists", "");
        } catch (Exception poo) {
            Toast.makeText(this, poo.toString(), Toast.LENGTH_LONG).show();
        }
        
        /* Toast.makeText(this, myList.size(), Toast.LENGTH_LONG).show();  */

        /* setListAdapter(new ArrayAdapter<ampacheObject> (this, android.R.layout.simple_list_item_1, myList)); */
        
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
            /*        case R.id.collection:
            try {
                URL myurl = new URL("http://lt-dan.sandbenders.org/");
                BufferedReader reader = new BufferedReader(new InputStreamReader(myurl.openStream()));
                Toast.makeText(this, reader.readLine(), Toast.LENGTH_SHORT).show();
            } catch (java.io.IOException exc) {
                Toast.makeText(this, exc.toString(), Toast.LENGTH_SHORT).show();
            }
            return true;*/
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
