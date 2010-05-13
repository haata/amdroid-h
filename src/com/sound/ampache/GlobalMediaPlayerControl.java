package com.sound.ampache;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import com.sound.ampache.objects.Song;
import com.sound.ampache.staticMedia.MediaPlayerControl;

public class GlobalMediaPlayerControl extends SlidingDrawer implements MediaPlayerControl, 
    OnBufferingUpdateListener, OnCompletionListener, OnDrawerCloseListener, OnDrawerOpenListener, 
    OnClickListener {
    
    private staticMedia mc;
    public Boolean prepared = true;
    private static GlobalMediaPlayerControl mpc = null;
    
    
    // Shuffle and repeat variables
    private ArrayList shuffleHistory = new ArrayList();
    private boolean shuffleEnabled = false;
    private boolean repeatEnabled = false;

    public GlobalMediaPlayerControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void onFinishInflate(){
        super.onFinishInflate();
        /*  Populate the drawer_view with our staticMedia class. */
        
        mc = new staticMedia(getContext());

        amdroid.mp.setOnBufferingUpdateListener(this);
        amdroid.mp.setOnCompletionListener(this);
        amdroid.playListVisible = true;
        
        amdroid.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                amdroid.mp.start();
                if (amdroid.playListVisible) {
                    mc.setEnabled(true);
                    mc.show();
                    prepared = true;
                }
            }});
        
        LinearLayout l = (LinearLayout) this.getContent();
        l.addView(mc.getController());
        mc.setMediaPlayer(this);
        
        mc.setEnabled(true);
        mc.show();
        
        mc.setPrevNextListeners(this, this, this, this);
        
        setOnDrawerOpenListener(this);
        setOnDrawerCloseListener(this);
        
        mpc=this;
    }
    
    public static GlobalMediaPlayerControl getMpc(){
        return mpc;
    }
    
    /*  callbacks for the MediaPlayer and MediaController */
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        amdroid.bufferPC = percent;
    }

    public int getBufferPercentage() {
        return amdroid.bufferPC;
    }

    public int getCurrentPosition() {
        if (amdroid.mp.isPlaying()) {
            return amdroid.mp.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (amdroid.mp.isPlaying()) {
            return amdroid.mp.getDuration();
        }
        return 0;
    }

    public boolean isPlaying() {
        return amdroid.mp.isPlaying();
    }

    public void pause() {
        if (amdroid.mp.isPlaying()) {
            amdroid.mp.pause();
        }
    }

    public void seekTo(int pos) {
        if (amdroid.mp.isPlaying()) {
            amdroid.mp.seekTo(pos);
        }
    }

    public void start() {
        // If current position i s 0 or below we have never played a song and play should be run
        if (amdroid.mp.getCurrentPosition()<=0)
            play();
        amdroid.mp.start();
    }

    public void play() {
        if (amdroid.getPlayingIndex() >= amdroid.playlistCurrent.size()) {
            amdroid.setPlayingIndex( amdroid.playlistCurrent.size() );
            //mc.setEnabled(false);
            return;
        }

        if (amdroid.getPlayingIndex() < 0) {
            amdroid.setPlayingIndex(0);
            //mc.setEnabled(false);
            return;
        }

        Song chosen = (Song) amdroid.playlistCurrent.get(amdroid.getPlayingIndex());

        if (amdroid.mp.isPlaying()) {
            amdroid.mp.stop();
        }

        amdroid.mp.reset();
        /*  Disabling mediacontroller while preparing */
        mc.setEnabled(false);
        try {
            Log.i("Amdroid", "Song URL     - " + chosen.url );
            Log.i("Amdroid", "Song URL (C) - " + chosen.liveUrl() );
            amdroid.mp.setDataSource(chosen.liveUrl());
            amdroid.mp.prepareAsync();
            prepared = false;
        } catch (Exception blah) {
            /*  Enabling staticMedia so we don't lock for ever incase of exception */
            mc.setEnabled(true);
            Log.i("Amdroid", "Tried to get the song but couldn't...sorry D:");
            return;
        }
        //turnOnPlayingView();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextInPlaylist();
        play();       
    }
    
    private void nextInPlaylist() {
        if ( shuffleEnabled ) {
            // So we don't play a song more than once
            if ( !shuffleHistory.contains( amdroid.getPlayingIndex() ) )
                shuffleHistory.add( amdroid.getPlayingIndex() );

            // Just played the last song, repeat if repeat is enabled, stop otherwise
            if ( shuffleHistory.size() >= amdroid.playlistCurrent.size() && repeatEnabled )
                shuffleHistory.clear();
            else
                amdroid.setPlayingIndex(amdroid.playlistCurrent.size());

            int next = 0;
            Random rand = new Random();

            // Try random numbers until finding one that is not used
            do {
                next = rand.nextInt( amdroid.playlistCurrent.size() );
            } while ( shuffleHistory.contains( next ) );

            // Set next playing index
            amdroid.setPlayingIndex(next);
        } else {
            amdroid.setPlayingIndex(amdroid.getPlayingIndex()+1);

            // Reset playlist to beginning if repeat is enabled
            if ( amdroid.getPlayingIndex() >= amdroid.playlistCurrent.size() && repeatEnabled )
                amdroid.setPlayingIndex(0);
        }
    }

    private void prevInPlaylist() {
        if ( shuffleEnabled ) {
            int currIndex = shuffleHistory.indexOf( amdroid.getPlayingIndex() );

            // Call a random next song if this is the first song
            if ( shuffleHistory.size() < 1 ) {
                nextInPlaylist();

                return;
            }

            // Previous (Current item is not in the shuffle history)
            if ( currIndex == -1 ) {
                // Set previous song
                amdroid.setPlayingIndex( (Integer)shuffleHistory.get( shuffleHistory.size() - 1 ) );

                // Remove item, I consider Previous like an undo
                shuffleHistory.remove( shuffleHistory.size() - 1 );
            }
            // This shouldn't be possible, but...
            else if ( currIndex > 0 ) {
                amdroid.setPlayingIndex( (Integer)shuffleHistory.get( currIndex - 1 ) );

                shuffleHistory.remove( currIndex );
            }
        }
        // Do not call previous if it is the first song
        else if ( amdroid.getPlayingIndex() > 0 )
            amdroid.setPlayingIndex( amdroid.getPlayingIndex() -1 );
    }

    @Override
    public void onDrawerClosed() {
        //amdroid.playListVisible=false;
    }

    @Override
    public void onDrawerOpened() {
        //amdroid.playListVisible=true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case (R.id.next):
            nextInPlaylist();
            play();
            break;
        case (R.id.prev):
            prevInPlaylist();
            play();
            break;
        case (R.id.repeat):
            if (repeatEnabled) {
                ((ImageButton) v).setImageResource(R.drawable.ic_menu_revert_disabled);
                // Disable Repeat
                repeatEnabled = false;
                Toast.makeText(getContext(), "Repeat Disabled", Toast.LENGTH_SHORT).show();
            } else {
                ((ImageButton) v).setImageResource(R.drawable.ic_menu_revert);
                // Enable Repeat
                repeatEnabled = true;
                Toast.makeText(getContext(), "Repeat Enabled", Toast.LENGTH_SHORT).show();
            }
            break;
        case (R.id.shuffle):
            if (shuffleEnabled) {
                // Clean Shuffle History
                shuffleHistory.clear();
                ((ImageButton) v).setImageResource(R.drawable.ic_menu_shuffle_disabled);
                // Disable Shuffle
                shuffleEnabled = false;
                Toast.makeText(getContext(), "Shuffle Disabled", Toast.LENGTH_SHORT).show();
            } else {
                ((ImageButton) v).setImageResource(R.drawable.ic_menu_shuffle);

                // Enable Shuffle
                shuffleEnabled = true;
                Toast.makeText(getContext(), "Shuffle Enabled", Toast.LENGTH_SHORT).show();
            }
            break;
        }

    }
}
