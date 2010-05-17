package com.sound.ampache;

import android.os.Bundle;
import android.widget.ListView;


public class PlaylistsActivity extends BaseActivity {
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlistsactivity_layout);       
        
        lv = (ListView) findViewById(R.id.playlists_list);       
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        directive = new String[2];
        directive[0] = "playlists";
        directive[1] = "";
        
        updateList(true);    
    }

}
