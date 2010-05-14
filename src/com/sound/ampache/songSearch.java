package com.sound.ampache;

/* Copyright (c) 2008-2009 	Kevin James Purdy <purdyk@gmail.com>                                              
 * Copyright (c) 2010 		Krisopher Heijari <iif.ftw@gmail.com>
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sound.ampache.objects.Song;
import com.sound.ampache.objects.ampacheObject;
 
public final class songSearch extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener{
    
protected LinkedList<String[]> history = new LinkedList<String[]>();
    
    private ListView lv;
    private DataHandler dataReadyHandler;
    private String title;
    private String[] directive;
    private Boolean isFetching = false;
    private ProgressDialog dlog;
    private Spinner searchCriteria;
    private EditText searchString;
    
    // ArrayList and adapter to display dynamic content from the ampache server
    private ArrayList<ampacheObject> list;
    private collectionAdapter listAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity_layout);
        
        searchCriteria = (Spinner) findViewById(R.id.search_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.search, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCriteria.setAdapter(adapter);
        
        searchString = (EditText) findViewById(R.id.search_text);
        
        list = new ArrayList<ampacheObject>();
        listAdapter = new collectionAdapter(this, R.layout.browsable_item, list);
        
        lv = (ListView) findViewById(R.id.search_list);
        
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        /* TODO Make the text filter work for our collectionAdapter */
        //lv.setTextFilterEnabled(true);
        
        /*  Bind clicklistener for our serach button */
        ((ImageButton) findViewById(R.id.search_button)).setOnClickListener(this);
        
        dataReadyHandler = new DataHandler(listAdapter, directive);
        
        lv.setAdapter(listAdapter);
            
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

        listAdapter.clear();
        if (lv.getAdapter()!=listAdapter)
            lv.setAdapter(listAdapter);
        
        directive=tmpDirective;

        requestMsg.obj = directive;

        requestMsg.replyTo = new Messenger(dataReadyHandler);
        amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);

        if (setHistory)
            history.add(directive.clone());

    }
    
    public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
        
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
    

    @Override
    public void onClick(View v) {
        String s = searchString.getText().toString();
        if (s.length()<=0)
            return;
        
        directive = new String[2];
        
        String c = (String) searchCriteria.getSelectedItem();
        if (c.equals("All"))
            directive[0] = "search_songs";
        else if (c.equals("Artists"))
            directive[0] = "artists";
        else if (c.equals("Albums"))
            directive[0] = "albums";
        else if (c.equals("Tags"))
            directive[0] = "tags";
        else if (c.equals("Songs"))
            directive[0] = "songs";
        else
            return;
        
        try {
            directive[1] = URLEncoder.encode(s, "UTF-8");
        } catch (Exception poo) {
            return;
        }
        
        // clear history when searching, we should only be able to go back if browsing after a search.
        history.clear();
        updateList(directive, true);

    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return false;
    }
    
}
