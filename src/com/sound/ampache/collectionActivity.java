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
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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
import com.sound.ampache.objects.*;
import java.lang.Integer;

public final class collectionActivity extends ListActivity implements ampacheCommunicator.ampacheDataReceiver
{

    private ArrayList<ampacheObject> list = new ArrayList();
    public Handler dataReadyHandler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //debugging crap
        //Debug.startMethodTracing();
        //Debug.enableEmulatorTraceOutput();
        //Debug.waitForDebugger();

        showDialog(0);

        Intent intent = getIntent();

        String[] directive;
        directive = intent.getStringArrayExtra("directive");

        if (directive == null) {
            directive = new String[2];
            directive[0] = "artists";
            directive[1] = "";
        }

        // and be prepared to handle the response
        dataReadyHandler = new Handler() {
            public void handleMessage(Message msg) {
                dismissDialog(0);
                //list = (ArrayList) msg.obj;
                setListAdapter(new collectionAdapter(collectionActivity.this, (ArrayList) msg.obj));
            }
        };
        
        // Are we being 're-created' ?
        list = savedInstanceState != null ? (ArrayList) savedInstanceState.getSerializable("list") : null;
        // If not, queue up a data fetch
        if (list == null) {
            //ampacheRequest req = amdroid.comm.new ampacheRequest(directive[0], directive[1], this);
            Message requestMsg = new Message();
            
            //tell it what to do
            requestMsg.obj = directive;
            
            //tell it how to handle the stuff
            requestMsg.replyTo = new Messenger (this.dataReadyHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
            //req.start();
        }

        getListView().setTextFilterEnabled(true);

    }
    
    //The fetch thread calls this function.
    public void receiveObjects(ArrayList data) {
        list = data;
        dismissDialog(0);
        //Notify the original thread that data is ready
        Message msg = new Message();
        msg.what = 0x1337;
        this.dataReadyHandler.sendMessage(msg);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        //Toast.makeText(this, "Saving instance..", Toast.LENGTH_SHORT).show();
        bundle.putSerializable("list", list);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        ampacheObject val = (ampacheObject) l.getItemAtPosition(position);
        if (val == null)
            return;
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
            holder.add.setOnClickListener( new enqueueListener(mCtx, position, cur));
            return convertView;
        }
    }

    private class enqueueListener implements View.OnClickListener
    {
        int pos;
        Context mCtx;
        ampacheObject cur;

        public enqueueListener(Context context, int position, ampacheObject cur) {
            mCtx = context;
            pos = position;
            this.cur = cur;
        }

        public void onClick(View v) {
            Toast.makeText(mCtx, "Enqueue " + cur.getType() + ": " + cur.toString(), Toast.LENGTH_SHORT).show();
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
