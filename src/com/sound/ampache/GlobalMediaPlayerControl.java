package com.sound.ampache;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;
import com.sound.ampache.objects.Song;

public class GlobalMediaPlayerControl {

    public Boolean prepared = true;
    
    // Shuffle and repeat variables
    public ArrayList<Integer> shuffleHistory = new ArrayList<Integer>();
    public boolean shuffleEnabled = false;
    public boolean repeatEnabled = false;
    
    // Playlist variables
    private ArrayList<Song> playlistCurrent;
    private PlaylistCurrentListener playlistCurrentListener;
    private int playingIndex;
    private PlayingIndexListener playingIndexListener;

	public GlobalMediaPlayerControl()
	{
		amdroid.playListVisible = true;
		playlistCurrent = new ArrayList<Song>();
		playingIndex = 0;
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
        
        if (playingIndex >= playlistCurrent.size()) {
            setPlayingIndex( playlistCurrent.size() );
            //mc.setEnabled(false);
            return;
        }

        if (playingIndex < 0) {
            setPlayingIndex(0);
            //mc.setEnabled(false);
            return;
        }

        Song chosen = (Song) playlistCurrent.get(playingIndex);

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
            if ( !shuffleHistory.contains( playingIndex ) )
                shuffleHistory.add( playingIndex );

            // Just played the last song, repeat if repeat is enabled, stop otherwise
            if ( shuffleHistory.size() >= playlistCurrent.size() && repeatEnabled )
                shuffleHistory.clear();
            else
                setPlayingIndex(playlistCurrent.size());

            int next = 0;
            Random rand = new Random();

            // Try random numbers until finding one that is not used
            do {
                next = rand.nextInt( playlistCurrent.size() );
            } while ( shuffleHistory.contains( next ) );

            // Set next playing index
            setPlayingIndex(next);
        } else {
            setPlayingIndex(playingIndex+1);

            // Reset playlist to beginning if repeat is enabled
            if ( playingIndex >= playlistCurrent.size() && repeatEnabled )
                setPlayingIndex(0);
        }
    }

    public void prevInPlaylist() {
        if ( shuffleEnabled ) {
            int currIndex = shuffleHistory.indexOf( playingIndex );

            // Call a random next song if this is the first song
            if ( shuffleHistory.size() < 1 ) {
                nextInPlaylist();

                return;
            }

            // Previous (Current item is not in the shuffle history)
            if ( currIndex == -1 ) {
                // Set previous song
                setPlayingIndex( (Integer)shuffleHistory.get( shuffleHistory.size() - 1 ) );

                // Remove item, I consider Previous like an undo
                shuffleHistory.remove( shuffleHistory.size() - 1 );
            }
            // This shouldn't be possible, but...
            else if ( currIndex > 0 ) {
                setPlayingIndex( (Integer)shuffleHistory.get( currIndex - 1 ) );

                shuffleHistory.remove( currIndex );
            }
        }
        // Do not call previous if it is the first song
        else if ( playingIndex > 0 )
            setPlayingIndex( playingIndex -1 );
    }
    
    // Functions used to modify playingIndex and notify about changes
    public void setPlayingIndex(int i){
        playingIndex=i;
        if (playingIndexListener !=null)
            playingIndexListener.onPlayingIndexChange();
    }
    
    public int getPlayingIndex(){
        return playingIndex;
    }
    
    public void setPlayingIndexListener(PlayingIndexListener listener){
    	playingIndexListener = listener;
    }
    
    // Functions used to modify playlistCurrent and notify about changes
    public void setPlaylistCurrentListener(PlaylistCurrentListener listener){
        playlistCurrentListener=listener;
    }
    public void addAllPlaylistCurrent(ArrayList<Song> songList){
        playlistCurrent.addAll(songList);
        if ( playlistCurrentListener != null )
        	playlistCurrentListener.onPlaylistCurrentChange();
    }
    public void addPlaylistCurrent(Song song){
        playlistCurrent.add(song);
        if ( playlistCurrentListener != null )
        	playlistCurrentListener.onPlaylistCurrentChange();
    }
    public void clearPlaylistCurrent(){
        playlistCurrent.clear();
        setPlayingIndex(0);
        if ( playlistCurrentListener != null )
        	playlistCurrentListener.onPlaylistCurrentChange();
    }

	/*
	 * Changes should never be made directly on the returned object but instead via the functions
	 * supplied in this class. Unless we want to make changes without calling the notifying
	 * functions.
	 */
    public ArrayList<Song> getPlaylistCurrent(){
    	return playlistCurrent;
    }
    
    
    /*
     * Listener Interfaces
     */
	public interface PlayingIndexListener {
		public void onPlayingIndexChange();
	}

	public interface PlaylistCurrentListener {
		public void onPlaylistCurrentChange();
	}

}
