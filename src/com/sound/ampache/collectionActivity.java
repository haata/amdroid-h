package com.sound.ampache;

import android.app.ListActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Window;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import com.sound.ampache.objects.*;
import java.lang.Integer;

public final class collectionActivity extends ListActivity 
{

    public dataHandler dataReadyHandler;
    private String title;
    private ArrayList<ampacheObject> list = null;
    private String[] directive;
    private Boolean isFetching = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* fancy spinner */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        //debugging crap
        //Debug.waitForDebugger();

        // Verify a valid session.
        amdroid.comm.ping();

        // We've tried to login, and failed, so present the user with the preferences pane
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return;
        }

        // Verify a valid session. The if statement should probably not be there.
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null)
            amdroid.comm.ping();

        // We've tried to login, and failed, so present the user with the preferences pane
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = getIntent();

        directive = intent.getStringArrayExtra("directive");
        title = intent.getStringExtra("title");

        setTitle(title);

        // and be prepared to handle the response
        dataReadyHandler = new dataHandler();
        
        // Are we being 're-created' ?
        list = savedInstanceState != null ? (ArrayList) savedInstanceState.getParcelableArrayList("list") : null;

        // Maybe we have the cache already?
        if (amdroid.cache.containsKey(directive[0])) { 
            list = amdroid.cache.getParcelableArrayList(directive[0]); 
        }

        // If not, queue up a data fetch
        if (list == null) {
            isFetching = true;
            //Tell them we're loading
            showDialog(0);

            //ampacheRequest req = amdroid.comm.new ampacheRequest(directive[0], directive[1], this);
            Message requestMsg = new Message();

            requestMsg.arg1 = 0;
            requestMsg.what = 0x1336;

            /* we want incremental pulls for the large ones */
            if (directive[0].equals("artists")) {
                list = new ArrayList(amdroid.comm.artists);
                requestMsg.arg2 = amdroid.comm.artists;
            } else if (directive[0].equals("albums")) {
                list = new ArrayList(amdroid.comm.albums);
                requestMsg.arg2 = amdroid.comm.albums;
            } else if (directive[0].equals("songs")) {
                list = new ArrayList(amdroid.comm.songs);
                requestMsg.arg2 = amdroid.comm.songs;
            } else {
                requestMsg.what = 0x1337;
                getListView().setTextFilterEnabled(true);
            }

            //tell it what to do
            requestMsg.obj = directive;

            //tell it how to handle the stuff
            requestMsg.replyTo = new Messenger (this.dataReadyHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
        } else {
            setListAdapter(new collectionAdapter(this, R.layout.browsable_item, list));
            getListView().setTextFilterEnabled(true);
        }

    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (!isFetching) {
            bundle.putParcelableArrayList("list", list);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (isFetching) {
            dataReadyHandler.removeMessages(0x1336);
            dataReadyHandler.removeMessages(0x1337);
            dataReadyHandler.stop = true;
            amdroid.requestHandler.stop = true;
        }
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
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching " + title +"...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    private class dataHandler extends Handler {

        public Boolean stop = false;
        private collectionAdapter ca;

        public void handleMessage(Message msg) {
            if (stop)
                return;
            switch (msg.what) {
            case (0x1336):
                /* Handle incremental updates */
                list.addAll((ArrayList) msg.obj);
                
                /* first inc, hid the dialog and set the adapter */
                if (msg.arg1 == 0) {
                    ca = new collectionAdapter(collectionActivity.this, R.layout.browsable_item, list);
                    setListAdapter(ca);
                    dismissDialog(0);
                    setProgressBarIndeterminateVisibility(true);
                }
               
                /* queue up the next inc */
                if (msg.arg1 < msg.arg2) {
                    Message requestMsg = new Message();
                    requestMsg.obj = directive;
                    requestMsg.what = 0x1336;
                    requestMsg.arg1 = msg.arg1 + 100;
                    requestMsg.arg2 = msg.arg2;
                    requestMsg.replyTo = new Messenger (this);
                    amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
                    ca.notifyDataSetChanged();
                } else {
                    /* we've completed incremental fetch, cache it baby! */
                    ca.notifyDataSetChanged();
                    amdroid.cache.putParcelableArrayList(directive[0], list);
                    setProgressBarIndeterminateVisibility(false);
                    getListView().setTextFilterEnabled(true);
                    isFetching = false;
                }
                break;
            case (0x1337):
                /* Handle primary updates */
                list = (ArrayList) msg.obj;
                setListAdapter(new collectionAdapter(collectionActivity.this, R.layout.browsable_item, list));
                dismissDialog(0);
                isFetching = false;
                break;
            case (0x1338):
                /* handle an error */
                dismissDialog(0);
                Toast.makeText(collectionActivity.this, "Error:" + (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            case (0x1339):
                /* handle playlist enqueues */
                amdroid.playlistCurrent.addAll((ArrayList) msg.obj);
                break;
            }
        }
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
            
            if (cur != null) {
                holder.title.setText(cur.toString());
                holder.add.setOnClickListener( new enqueueListener(mCtx, position, cur));
            } else {
                holder.title.setText("Loading...");
            }
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
