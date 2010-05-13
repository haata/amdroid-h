package com.sound.ampache;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;

public class AmdroidActivityGroup extends ActivityGroup implements OnClickListener{
    
    //Identifiers for the different activities. The id's can be anything as longs as they are unique. 
    public static final String GOTO_HOME = "goto_home";
    public static final String GOTO_MUSIC = "goto_music";
    public static final String GOTO_PLAYLISTS = "goto_playlists";
    public static final String GOTO_SEARCH = "goto_search";
    public static final String GOTO_PLAYING = "goto_playing";
    
    private LocalActivityManager localActivityManager;
    private FrameLayout activityFrame;
    // Re-usable intent for all our activities
    private Intent intent;
    private GlobalMediaPlayerControl globalPlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amdroidactivitygroup_layout);
        
        intent = new Intent();
        
        /*  Bind onclicklisteners to our buttons in menuview_laout.xml */
        ImageButton b = (ImageButton)findViewById(R.id.goto_home);
        b.setOnClickListener(this);
        b = (ImageButton)findViewById(R.id.goto_music);
        b.setOnClickListener(this);
        b = (ImageButton)findViewById(R.id.goto_playlists);
        b.setOnClickListener(this);
        b = (ImageButton)findViewById(R.id.goto_playing);
        b.setOnClickListener(this);
        b = (ImageButton)findViewById(R.id.goto_search);
        b.setOnClickListener(this);
        
        localActivityManager = getLocalActivityManager();
        activityFrame = (FrameLayout) findViewById(R.id.activity_frame);
        globalPlay = (GlobalMediaPlayerControl) findViewById(R.id.drawer);
        
        /*  re-usable intent for switching between activities */
        intent = new Intent();
        /*  choose which activity to spawn on startup. */
        intent.setClass(this, dashActivity.class);
        setActivity(GOTO_HOME, intent);
        
        amdroid.comm.ping();
        
        /*  We've tried to login, and failed, so present the user with the preferences pane */
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return;
        }       
        
    }
    
    public void setActivity(String id, Intent intent){
        Window w = localActivityManager.startActivity(id, intent);
        View v = w.getDecorView();
        activityFrame.removeAllViews();
        activityFrame.addView(v);
        /* Always keep our SlidingDrawer on top */
        globalPlay.bringToFront();
    }
    

    @Override
    public void onClick(View v) {
        
        /* 
         * Check if we have a vild session, if not we try to establish one and then check again. 
         * The order is important, must check if authToken is null before evaluating as a string. 
         */
        if (amdroid.comm.authToken == null || amdroid.comm.authToken.equals(""))
            amdroid.comm.ping();

        if (amdroid.comm.authToken == null || amdroid.comm.authToken.equals("")) {
            Toast.makeText(this, "Connection problems: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            return;
        }
        
        switch (v.getId()) {
        case (R.id.goto_home):
            intent.setClass(this, dashActivity.class);
            setActivity(GOTO_HOME, intent);
            break;
        
        case (R.id.goto_music):
            intent.setClass(this, BrowseActivity.class);
            setActivity(GOTO_MUSIC, intent);
            break;

        case (R.id.goto_playlists):
            intent.setClass(this, PlaylistsActivity.class);
            setActivity(GOTO_PLAYLISTS, intent);
            break;

        case (R.id.goto_playing):
            intent.setClass(this, playlistActivity.class);
            setActivity(GOTO_PLAYING, intent);
            break;

        case (R.id.goto_search):
            intent.setClass(this, songSearch.class);
            setActivity(GOTO_SEARCH, intent);
            break;

        default:
            break;
        }
        
    }
    
    
    /*  Call our active activitys event handlers */
    public void onBackPressed(){
        getCurrentActivity().onBackPressed();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return this.getCurrentActivity().onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Activity a = this.getCurrentActivity();
        boolean ret = false;
        if (a.getClass()==playlistActivity.class || a.getClass()==dashActivity.class )
            ret = a.onOptionsItemSelected(item);
        return ret;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
        
        return true;
    }
    
    
}

/*@Override
protected void onResume() {
    super.onResume();

    // Display song name in the "Now Playing" section.
    TextView st = (TextView) findViewById(R.id.title);
    String title = "";
    try {
        if (amdroid.mp.isPlaying()) {
            title = "Now Playing - " + amdroid.playlistCurrent.get(amdroid.playingIndex).name;
        } else {
            title = "Paused - " + amdroid.playlistCurrent.get(amdroid.playingIndex).name;
        }
    } catch(Exception e) {
        title = "No Song Selected";
    }

    st.setText(title);

    // Display song info in the "Now Playing" section
    TextView si = (TextView) findViewById(R.id.artist);
    String info = "";
    try {
        info = amdroid.playlistCurrent.get(amdroid.playingIndex).extraString();
    } catch(Exception e) {
        info = "Show current playlist";
    }

    si.setText(info);
}*/
