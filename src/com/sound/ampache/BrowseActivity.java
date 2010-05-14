package com.sound.ampache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sound.ampache.objects.Song;
import com.sound.ampache.objects.ampacheObject;

public class BrowseActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{
    
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
    
    // Constant root list and adapter. This is only used to display the root options. 
    private ArrayList<String> root = new ArrayList<String>(Arrays.asList(new String[]{"Artists", "Albums", "Tags"}));
    private ArrayAdapter<String> rootAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browseactivity_layout);       
        
        list = new ArrayList<ampacheObject>();
        listAdapter = new collectionAdapter(this, R.layout.browsable_item, list);
        
        rootAdapter = new ArrayAdapter<String>(this, R.layout.list_item_music_root , root);
        
        lv = (ListView) findViewById(R.id.browse_music_list);
        
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        /* TODO Make the text filter work for our array adapter */
        //lv.setTextFilterEnabled(true);
        
        directive = new String[2];
        directive[0] = "root";      
        dataReadyHandler = new DataHandler(listAdapter, directive);
        
        updateList(directive, true);    
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        String[] tmpDirective = new String[2];
        ampacheObject val = null;
        if (history.getLast()[0].equals("root")) {
            tmpDirective[0] = (String) adapterView.getItemAtPosition(position);
            tmpDirective[1] = "";
        } else {
            val = (ampacheObject) adapterView.getItemAtPosition(position);
            if (val == null)
                return;
            if (val.getType().equals("Song")) {
                Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
                amdroid.addPlaylistCurrent((Song) val);
                return;
            }
            tmpDirective[0]=val.childString();
            tmpDirective[1]=val.id;
        }
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
        if (tmpDirective[0].equals("root")) {
            lv.setAdapter(rootAdapter);

        } else {

            dataReadyHandler.removeMessages(0x1336);
            dataReadyHandler.removeMessages(0x1337);
            dataReadyHandler.stop = true;
            dataReadyHandler = new DataHandler(listAdapter, directive);

            Message requestMsg = new Message();

            requestMsg.arg1 = 0;
            requestMsg.what = 0x1336;
            directive[1] = "";

            if (tmpDirective[0].equalsIgnoreCase("Artists")) {
                directive[0] = "artists";
                requestMsg.arg2 = amdroid.comm.artists;
            } else if (tmpDirective[0].equalsIgnoreCase("Albums")) {
                directive[0] = "albums";
                requestMsg.arg2 = amdroid.comm.albums;
            } else if (tmpDirective[0].equalsIgnoreCase("Tags")) {
                directive[0] = "tags";
                requestMsg.what = 0x1337;
            } else {
                requestMsg.what = 0x1337;
                directive[0] = tmpDirective[0];
                directive[1] = tmpDirective[1];
            }
            
            if (lv.getAdapter()!=listAdapter)
                lv.setAdapter(listAdapter);
            listAdapter.clear();
            
            requestMsg.obj = directive;

            requestMsg.replyTo = new Messenger(dataReadyHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
            
        }
        if (setHistory)
            history.add(directive.clone());

    }
    
    public boolean onItemLongClick(AdapterView l, View v, int position, long id) {
        
        if (history.getLast()[0].equals("root"))
            return true;
        
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
    public boolean onPrepareOptionsMenu(Menu menu){
        return true;
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
        if (history.size() > 1) {
            history.removeLast();
            updateList(history.getLast(), false);
        }
        else
            ((AmdroidActivityGroup) getParent()).setActivity(AmdroidActivityGroup.GOTO_HOME);
    }
}
