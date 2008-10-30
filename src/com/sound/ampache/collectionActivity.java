package com.sound.ampache;

import android.app.ListActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
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

    ArrayList<ampacheObject> list;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Debug.startMethodTracing();
        //Debug.enableEmulatorTraceOutput();

        //Debug.waitForDebugger();

        Intent intent = getIntent();

        String[] directive;
        directive = intent.getStringArrayExtra("directive");

        showDialog(0);

        if (directive == null) {
            directive = new String[2];
            directive[0] = "artists";
            directive[1] =  "";
        }

	list = savedInstanceState != null ? (ArrayList) savedInstanceState.getSerializable("list") : null;
	if (list == null) {
	    try {
		list = amdroid.comm.fetch(directive[0], directive[1]);
	    } catch (Exception poo) {
		Toast.makeText(this, poo.toString(), Toast.LENGTH_LONG).show();
		list = new ArrayList();
	    }
	}
        /* set up our list adapter to handle the data */
        //setListAdapter(new ArrayAdapter<ampacheObject> (this, android.R.layout.simple_list_item_1, myList));
	//Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

        setListAdapter(new collectionAdapter(this, list));

	getListView().setTextFilterEnabled(true);

        dismissDialog(0);
    }
    
    protected void onSaveInstanceState(Bundle bundle) {
	super.onSaveInstanceState(bundle);
	//Toast.makeText(this, "Saving instance..", Toast.LENGTH_SHORT).show();
        bundle.putSerializable("list", list);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
	if (l.getItemAtPosition(position) == null) {
	    return;
	}
        ampacheObject val = (ampacheObject) l.getItemAtPosition(position);
        Intent intent = new Intent().setClass(this, collectionActivity.class);
        if (val.getType().equals("artist")) {
            String[] dir = {"artist_albums", val.id};
            intent = intent.putExtra("directive", dir).putExtra("title", "Artist: " + val.toString());
        } else if (val.getType().equals("album")) {
            String[] dir = {"album_songs", val.id};
            intent = intent.putExtra("directive", dir).putExtra("title", "Album: " + val.toString());
        } else if (val.getType().equals("song")) {
	    Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private class collectionAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        private ArrayList myList;
        
	private Context mCtx;

        public collectionAdapter(Context context, ArrayList list) {
            mInflater = LayoutInflater.from(context);
            myList = list;
	    mCtx = context;
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
            ampacheObject cur = (ampacheObject) myList.get(position);
            
            /* we don't reuse */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.browsable_item, null);
                holder = new bI();
                
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.add = (ImageView) convertView.findViewById(R.id.add);

                convertView.setTag(holder);
            } else {
                holder = (bI) convertView.getTag();
            }
            
            holder.title.setText(cur.toString());
	    holder.add.setOnClickListener( new enqueueListener(mCtx, position));
            return convertView;
        }
    }
    
    private class enqueueListener implements View.OnClickListener
    {
	int pos;
	Context mCtx;
	
	public enqueueListener(Context context, int position) {
	    mCtx = context;
	    pos = position;
	}
	
	public void onClick(View v) {
	    ampacheObject cur = (ampacheObject) list.get(pos);
	    Toast.makeText(mCtx, "Enqueue " + cur.getType() + ": " + cur.toString(), Toast.LENGTH_LONG).show();
	    if (cur.hasChildren()) {
		amdroid.playlistCurrent.addAll(cur.allChildren());
	    } else {
		amdroid.playlistCurrent.add((Song) cur);
	    }
	}
    }

    static class bI {
        TextView title;
        ImageView add;
    }
}
