package com.sound.ampache;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import com.sound.ampache.objects.Song;

public class GlobalMediaPlayerControl {

    public Boolean prepared = true;
    
    // Shuffle and repeat variables
    public ArrayList shuffleHistory = new ArrayList();
    public boolean shuffleEnabled = false;
    public boolean repeatEnabled = false;

	public GlobalMediaPlayerControl()
	{
		amdroid.playListVisible = true;
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

	public void start()
	{
		if ( !amdroid.mediaplayerInitialized )
			play();

		else
			amdroid.mp.start();
	}

    public void play() {
        
        // set to show that our mediaplayer has been initialized. 
        amdroid.mediaplayerInitialized = true;
        
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
        try {
            Log.i("Amdroid", "Song URL     - " + chosen.url );
            Log.i("Amdroid", "Song URL (C) - " + chosen.liveUrl() );
            amdroid.mp.setDataSource(chosen.liveUrl());
            amdroid.mp.prepareAsync();
            prepared = false;
        } catch (Exception blah) {
            Log.i("Amdroid", "Tried to get the song but couldn't...sorry D:");
            return;
        }
    }
    
    public void doPauseResume() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }
   
    public void nextInPlaylist() {
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

    public void prevInPlaylist() {
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

}
