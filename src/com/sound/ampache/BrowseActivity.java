package com.sound.ampache;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BrowseActivity extends BaseActivity {
    
    //Root list and adapter. This is only used to display the root options. 
    private ArrayList<String> root = new ArrayList<String>(Arrays.asList(new String[]{"Artists", "Albums", "Tags"}));
    private ArrayAdapter<String> rootAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browseactivity_layout);       
        
        rootAdapter = new ArrayAdapter<String>(this, R.layout.list_item_music_root , root);
        
        lv = (ListView) findViewById(R.id.browse_music_list);
        
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        directive = new String[2];
        directive[0] = "root";
        directive[1] = "";
        
        updateList(true);
    }

    /*
     *  Need to override these functions to cope with the "rootList". I.e. display "Artist" "Album" "Tags"
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        if (history.getLast()[0].equals("root")) {
            directive[0] = (String) adapterView.getItemAtPosition(position);
            directive[1] = "";
            updateList(true);
        }
        else
            super.onItemClick(adapterView, view, position, arg3);
    }
    
    public boolean onItemLongClick(AdapterView l, View v, int position, long id) {
        if (history.getLast()[0].equals("root"))
            return true;

        return super.onItemLongClick(l, v, position, id);
    }

    public void updateList(boolean setHistory) {

        if (directive[0].equals("root")) {
            lv.setAdapter(rootAdapter);

        } else {
            dataHandler.stop = true;
            dataHandler.removeMessages(0x1336);
            dataHandler.removeMessages(0x1337);
            dataHandler = new DataHandler();

            Message requestMsg = new Message();

            requestMsg.arg1 = 0;
            requestMsg.what = 0x1336;

            if (directive[0].equalsIgnoreCase("Artists")) {
                directive[0] = "artists";
                requestMsg.arg2 = amdroid.comm.artists;
            } else if (directive[0].equalsIgnoreCase("Albums")) {
                directive[0] = "albums";
                requestMsg.arg2 = amdroid.comm.albums;
            } else if (directive[0].equalsIgnoreCase("Tags")) {
                directive[0] = "tags";
                requestMsg.what = 0x1337;
            } else {
                requestMsg.what = 0x1337;
            }

            if (lv.getAdapter() != listAdapter)
                lv.setAdapter(listAdapter);
            listAdapter.clear();

            requestMsg.obj = directive;

            requestMsg.replyTo = new Messenger(dataHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
        }

        if (setHistory)
            history.add(directive.clone());

    }

}
