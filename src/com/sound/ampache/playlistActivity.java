package com.sound.ampache;

/* Copyright (c) 2008 Kevin James Purdy <purdyk@onid.orst.edu>
 * Copyright (c) 2010 Jacob Alexander   < haata@users.sf.net >
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.sound.ampache.amdroid.IntegerNotifiable;
import com.sound.ampache.objects.Song;

public final class playlistActivity extends Activity implements OnItemClickListener, IntegerNotifiable
{
    private ListView lv;
    private ImageView artView;

    private playlistAdapter pla;

    private Boolean albumArtEnabled = false;
    
    private GlobalMediaPlayerControl gmpc = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Set up our view :D
        setContentView(R.layout.playlist);

        lv = (ListView) findViewById(R.id.list);
        lv.setOnItemClickListener(this);

        pla = new playlistAdapter(this);
        lv.setAdapter(pla);
        
        // register our adapter to be called when a change to the currentPlaylist ocurrs  
        amdroid.registerBaseAdapterNotifiable(pla);
        
        //register ourselves to receive callbacks when playing index changes occurr
        amdroid.registerIntegerNotifiable(this);

        // Setup Album Art View TODO
        artView = (ImageView)findViewById(R.id.picview);

        // Load Album Art on Entry, currently SLOOOOOOW so TODO
        //if ( amdroid.playlistCurrent.size() > 0 )
        //    loadAlbumArt();

        // Center the playlist at the current song
        centerList( 0 );
        
        // Get reference to our mediaplayercontrol
        gmpc = GlobalMediaPlayerControl.getMpc();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.pl_clear:
            if (gmpc.isPlaying())
                amdroid.mp.stop();
            amdroid.setPlayingIndex(0);
            pla.clearItems();
            //mc.setEnabled(false);
            break;

        case R.id.pl_save:
            try {
                FileOutputStream pout = openFileOutput("playlist", 0);
                ObjectOutputStream pos = new ObjectOutputStream(pout);
                pos.writeObject(amdroid.playlistCurrent);
                pout.close();
            } catch (Exception poo) {
                Toast.makeText(this, "Error: " + poo.toString(), Toast.LENGTH_LONG).show();
            }
            break;

        case R.id.pl_load:
            if (gmpc.isPlaying())
                amdroid.mp.stop();
            amdroid.setPlayingIndex(0);
            //mc.setEnabled(false);
            try {
                FileInputStream pin = openFileInput("playlist");
                ObjectInputStream poin = new ObjectInputStream(pin);
                amdroid.playlistCurrent = (ArrayList<Song>) poin.readObject();
                pin.close();
            } catch (Exception poo) {
                Toast.makeText(this, "Error: " + poo.toString(), Toast.LENGTH_LONG).show();
            }
            pla.refresh();
            break;


        case R.id.pl_albumart:
            if (albumArtEnabled) {
                albumArtEnabled = false;
                artView.setVisibility(View.GONE);
            }
            else {
                artView.setVisibility(View.VISIBLE);
                albumArtEnabled = true;
                loadAlbumArt();
            }
            break;
        }
        return true;
    }

    private void loadAlbumArt()
    {
        if (amdroid.playlistCurrent.size()<=0 || !albumArtEnabled){
            artView.setVisibility(View.GONE);
            return;
        }
        
        int i = amdroid.getPlayingIndex();
        if (i>=amdroid.playlistCurrent.size())
            return;
        Song chosen = (Song) amdroid.playlistCurrent.get(amdroid.getPlayingIndex());

        Log.i("Amdroid", "Art URL     - " + chosen.art );
        Log.i("Amdroid", "Art URL (C) - " + chosen.liveArt() );

        try {
            URL artUrl = new URL( chosen.liveArt() );
            Object artContent = artUrl.getContent();
            Drawable albumArt = Drawable.createFromStream( (InputStream) artContent, "src" );
            artView.setImageDrawable( albumArt );

            if ( artView.getDrawable() != null )
                artView.setVisibility( View.VISIBLE );
            /* Something needs to happen here to clear the image view, too lazy atm */
            else
            {
                artView.setVisibility( View.GONE );
            }
            

        } catch ( MalformedURLException e ) {
            Log.i("Amdroid", "Album Art URL sucks! Try something else.");
        } catch ( IOException e ) {
            Log.i("Amdroid", "Teh interwebs died...");
        }
    }


    /* These functions help with displaying the |> icon next to the currently playing song */
    private void turnOffPlayingView() {
        /* TODO we should probably keep track of which song we've displayed a playing icon for. 
         * Looping through all items in the listview will be unneffictive for larger lists */
        for (int i=0; i < lv.getChildCount(); i++){
            View holder = lv.getChildAt(i);
            if (holder != null) {
                ImageView img = (ImageView) holder.findViewById(R.id.art);
                img.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void turnOnPlayingView() {
        if (amdroid.getPlayingIndex() >= lv.getFirstVisiblePosition() && amdroid.getPlayingIndex() <= lv.getLastVisiblePosition()) {
            View holder = lv.getChildAt(amdroid.getPlayingIndex() - lv.getFirstVisiblePosition());
            if (holder != null) {
                ImageView img = (ImageView) holder.findViewById(R.id.art);
                img.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView l, View v, int position, long id) {
        if (gmpc.prepared) {
            amdroid.setPlayingIndex(position);
            gmpc.play();
        }
    }


    private void centerList ( int adjust )
    {
            lv.setSelection( amdroid.getPlayingIndex() + adjust );
    }

    private class playlistAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        public playlistAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return amdroid.playlistCurrent.size();
        }

        public Object getItem(int position) {
            return amdroid.playlistCurrent.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        public void clearItems() {
            amdroid.clearPlaylistCurrent();
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            plI holder;
            Song cur = amdroid.playlistCurrent.get(position);

            /* we don't reuse  */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.playlist_item, null);
                holder = new plI();

                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.other = (TextView) convertView.findViewById(R.id.other);
                holder.art = (ImageView) convertView.findViewById(R.id.art);

                convertView.setTag(holder);
            } else {
                holder = (plI) convertView.getTag();
            }

            holder.title.setText(cur.name);
            holder.other.setText(cur.extraString());
            if (amdroid.getPlayingIndex() == position) {
                holder.art.setVisibility(View.VISIBLE);
            } else {
                holder.art.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    static class plI {
        TextView title;
        TextView other;
        ImageView art;
    }


    @Override
    public void playingIndexChange() {
        turnOffPlayingView();
        centerList(-1);
        turnOnPlayingView();
        loadAlbumArt();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    public void onBackPressed() {
           ((AmdroidActivityGroup) getParent()).setActivity(AmdroidActivityGroup.GOTO_HOME);
    }


}

// ex:tabstop=4 shiftwidth=4 expandtab:

