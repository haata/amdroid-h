package com.sound.ampache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.sound.ampache.objects.Song;
import com.sound.ampache.objects.ampacheObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class PlaylistsActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
    
    protected LinkedList<String[]> history = new LinkedList<String[]>();
    
    private ListView lv;
    private DataHandler dataReadyHandler;
    private String title;
    private String[] directive;
    private Boolean isFetching = false;
    private ProgressDialog dlog;
    
    // ArrayList and adapter to display dynamic content from the ampache server
    private ArrayList<ampacheObject> list;
    private collectionAdapter listAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlistsactivity_layout);       
        
        list = new ArrayList<ampacheObject>();
        listAdapter = new collectionAdapter(this, R.layout.browsable_item, list);
        
        lv = (ListView) findViewById(R.id.playlists_list);
        
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        /* TODO Make the text filter work for our collectionAdapter */
        //lv.setTextFilterEnabled(true);
        
        directive = new String[2];
        directive[0] = "playlists";
        directive[1] = "";
        dataReadyHandler = new DataHandler(listAdapter, directive);
        
        updateList(directive, true);    
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        String[] tmpDirective = new String[2];
        ampacheObject val = null;

        val = (ampacheObject) adapterView.getItemAtPosition(position);
        if (val == null)
            return;
        if (val.getType().equals("Song")) {
            Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
            amdroid.addPlaylistCurrent((Song) val);
            return;
        }
        tmpDirective[0] = val.childString();
        tmpDirective[1] = val.id;

        updateList(tmpDirective, true);
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
    
    public void updateList(String[] tmpDirective, boolean setHistory) {

        dataReadyHandler.removeMessages(0x1337);
        dataReadyHandler.stop = true;
        dataReadyHandler = new DataHandler(listAdapter, directive);

        Message requestMsg = new Message();

        requestMsg.arg1 = 0;
        requestMsg.what = 0x1337;
        directive[0] = tmpDirective[0];
        directive[1] = tmpDirective[1];

        listAdapter.clear();
        if (lv.getAdapter()!=listAdapter)
            lv.setAdapter(listAdapter);

        requestMsg.obj = directive;

        requestMsg.replyTo = new Messenger(dataReadyHandler);
        amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);

        if (setHistory)
            history.add(directive.clone());

    }
    
    public boolean onItemLongClick(AdapterView l, View v, int position, long id) {
        
        ampacheObject cur = (ampacheObject) l.getItemAtPosition(position);
        Toast.makeText(this, "Enqueue " + cur.getType() + ": " + cur.toString(), Toast.LENGTH_LONG).show();
        if (cur.hasChildren()) {
            Message requestMsg = new Message();
            requestMsg.obj = cur.allChildren();
            requestMsg.what = 0x1339;
            //tell it how to handle the stuff
            requestMsg.replyTo = new Messenger(dataReadyHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
        } else {
            amdroid.addPlaylistCurrent((Song) cur);
        }
        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (history.size() > 1) {
            history.removeLast();
            updateList(history.getLast(), false);
            return;
        }
        super.onBackPressed();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return true;
    }
}
