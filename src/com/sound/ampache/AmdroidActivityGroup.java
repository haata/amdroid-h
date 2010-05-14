package com.sound.ampache;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
        setActivity(GOTO_HOME);
        
        amdroid.comm.ping();
        
        /*  We've tried to login, and failed, so present the user with the preferences pane */
        if (amdroid.comm.authToken.equals("") || amdroid.comm.authToken == null) {
            Toast.makeText(this, "Login Failed: " + amdroid.comm.lastErr, Toast.LENGTH_LONG).show();
            Intent prefsIntent = new Intent().setClass(this, prefsActivity.class);
            startActivity(prefsIntent);
            return;
        }       
        
    }
    
    public void setActivity(String id){
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
            setActivity(GOTO_HOME);
            break;
        
        case (R.id.goto_music):
            intent.setClass(this, BrowseActivity.class);
            setActivity(GOTO_MUSIC);
            break;

        case (R.id.goto_playlists):
            intent.setClass(this, PlaylistsActivity.class);
            setActivity(GOTO_PLAYLISTS);
            break;

        case (R.id.goto_playing):
            intent.setClass(this, playlistActivity.class);
            setActivity(GOTO_PLAYING);
            break;

        case (R.id.goto_search):
            intent.setClass(this, songSearch.class);
            setActivity(GOTO_SEARCH);
            break;

        default:
            break;
        }
        
    }
    
    /*  
     *  Call our active activities eventHandlers 
     *  This is done incase the subactivity does not have a focusbale view. 
     *  In that case we still want to, for example, be able to display/use the menu.  
     */
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
        if (getCurrentActivity().getClass()!=dashActivity.class)
            setActivity(GOTO_HOME);
        else
            finish();
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
