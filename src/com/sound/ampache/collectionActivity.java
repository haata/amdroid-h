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

public final class collectionActivity extends ListActivity 
{

    public Handler dataReadyHandler;
    private String title;
    private ArrayList<ampacheObject> list = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //debugging crap
        //Debug.startMethodTracing();
        //Debug.enableEmulatorTraceOutput();
        //Debug.waitForDebugger();

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

        Intent intent = getIntent();

        String[] directive;
        directive = intent.getStringArrayExtra("directive");
        title = intent.getStringExtra("title");

        if (directive == null) {
            directive = new String[2];
            directive[0] = "playlists";
            directive[1] = "";
            title = "Playlists";
        }

        setTitle(title);

        // and be prepared to handle the response
        dataReadyHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0x1336) {

                }
                if (msg.what == 0x1337) {
                    list = (ArrayList) msg.obj;
                    setListAdapter(new collectionAdapter(collectionActivity.this, R.layout.browsable_item, list));
                    dismissDialog(0);
                } else if (msg.what == 0x1338) {
                    dismissDialog(0);
                    Toast.makeText(collectionActivity.this, "Error:" + (String) msg.obj, Toast.LENGTH_LONG).show();
                } else if (msg.what == 0x1339) {
                    amdroid.playlistCurrent.addAll((ArrayList) msg.obj);
                }
                
            }
            };
        
        // Are we being 're-created' ?
        list = savedInstanceState != null ? (ArrayList) savedInstanceState.getParcelableArrayList("list") : null;

        // If not, queue up a data fetch
        
        if (list == null) {
            //Tell them we're loading
            showDialog(0);

            //ampacheRequest req = amdroid.comm.new ampacheRequest(directive[0], directive[1], this);
            Message requestMsg = new Message();
            
            //tell it what to do
            requestMsg.obj = directive;
            requestMsg.what = 0x1337;

            //tell it how to handle the stuff
            requestMsg.replyTo = new Messenger (this.dataReadyHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
        } else {
            setListAdapter(new collectionAdapter(this, R.layout.browsable_item, list));
        }

        getListView().setTextFilterEnabled(true);

    }

    protected void onResume(Bundle bundle) {
        if (amdroid.confChanged) {
            amdroid.confChanged = false;
            setIntent(getIntent());
        }
    }
    
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList("list", list);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        ampacheObject val = (ampacheObject) l.getItemAtPosition(position);
        if (val == null)
            return;
        Intent intent = new Intent().setClass(this, collectionActivity.class);
        if (val.getType().equals("Song")) {
            Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
            amdroid.playlistCurrent.add((Song) val);
            return;
        } else {
            String[] dir = {val.childString(), val.id};
            intent = intent.putExtra("directive", dir).putExtra("title", val.getType() + ": " + val.toString());
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
        dialog.setMessage("Fetching " + title +"...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.collection_menu, menu);
        return true;
    }

    private class collectionAdapter extends ArrayAdapter
    {
        
        private Context mCtx;
        private int resid;
        private LayoutInflater mInflater;

        
        public collectionAdapter(Context context, int resid, ArrayList list) {
            super(context, resid, list);
            this.resid = resid;
            mCtx = context;
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            bI holder;
            ampacheObject cur = (ampacheObject) getItem(position);

            /* we don't reuse */
            if (convertView == null) {
                convertView = mInflater.inflate(resid, null);
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
                Message requestMsg = new Message();
                requestMsg.obj = cur.allChildren();
                requestMsg.what = 0x1339;
                //tell it how to handle the stuff
                requestMsg.replyTo = new Messenger (collectionActivity.this.dataReadyHandler);
                amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
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
