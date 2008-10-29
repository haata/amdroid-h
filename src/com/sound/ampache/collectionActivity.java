package com.sound.ampache;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import com.sound.ampache.ampacheCommunicator;
import com.sound.ampache.objects.*;
import java.lang.Integer;

public final class collectionActivity extends ListActivity
{
    
    private ArrayList<ampacheObject> myList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ArrayList<ampacheObject> myList = new ArrayList();

        Intent intent = getIntent();

        String[] directive;
        directive = intent.getStringArrayExtra("directive");

        if (directive == null) {
            directive = new String[2];
            directive[0] = "artists";
            directive[1] =  "";
        }

        try {
            myList = amdroid.comm.fetch(directive[0], directive[1]);
        } catch (Exception poo) {
            Toast.makeText(this, poo.toString(), Toast.LENGTH_LONG).show();
        }
        
        /* set up our list adapter to handle the data */
        //setListAdapter(new ArrayAdapter<ampacheObject> (this, android.R.layout.simple_list_item_1, myList));
        setListAdapter(new collectionAdapter(this));
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        ampacheObject val = (ampacheObject) l.getItemAtPosition(position);
        Intent intent = new Intent().setClass(this, collectionActivity.class);
        if (val.type.equals("artist")) {
            String[] dir = {"artist_albums", val.id};
            intent = intent.putExtra("directive", dir).putExtra("title", "Artist: " + val.toString());
        } else if (val.type.equals("album")) {
            String[] dir = {"album_songs", val.id};
            intent = intent.putExtra("directive", dir).putExtra("title", "Album: " + val.toString());
        } else if (val.type.equals("song")) {
            amdroid.playlistCurrent.add((Song) val);
            return;
        }
        startActivity(intent);
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
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private class collectionAdapter extends BaseAdapter
    {
	private LayoutInflater mInflater;

        public collectionAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return myList.size();
        }

        public Object getItem(int position) {
            return myList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            bI holder;
            ampacheObject cur = myList.get(position);
            
            /* we don't reuse */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.browsable_item, null);
                holder = new bI();
		
                holder.title = (TextView) convertView.findViewById(R.id.title);
		holder.add = (ImageButton) convertView.findViewById(R.id.add);
		convertView.setTag(holder);
	    } else {
		holder = (bI) convertView.getTag();
	    }
            
	    holder.title.setText(cur.toString());
	    return convertView;
	}
    }
    
    static class bI {
	TextView title;
	ImageButton add;
    }
}
