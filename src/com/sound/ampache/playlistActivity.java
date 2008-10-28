package com.sound.ampache;


import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import com.sound.ampache.objects.Song;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

public final class playlistActivity extends ListActivity implements MediaPlayerControl, OnBufferingUpdateListener, OnCompletionListener
{

    private MediaPlayer mp;
    private MediaController mc;

    private int playingIndex;
    private int bufferPC;
    private Boolean playing;

    private prevList prev = new prevList();

    private nextList next = new nextList();

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        mp = new MediaPlayer();
        mp.setOnBufferingUpdateListener(this);
        
        mc = new MediaController(this, false);

        mc.setAnchorView(this.getListView());
        mc.setEnabled(true);
        mc.setPrevNextListeners(next, prev);

        mc.setMediaPlayer(this);

        setListAdapter(new ArrayAdapter<Song> (this, android.R.layout.simple_list_item_1, amdroid.playlistCurrent));

        //mc.show();
    }

    private class prevList implements OnClickListener
    {
        public void onClick(View v) {
            playingIndex--;
            play();
        }
    }

    private class nextList implements OnClickListener
    {
        public void onClick(View v) {
            playingIndex++;
            play();
        }
    }
    
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPC = percent;
    }

    public int getBufferPercentage() {
        return bufferPC;
    }

    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public void pause() {
        mp.pause();
    }

    public void seekTo(int pos) {
        mp.seekTo(pos);
    }

    public void start() {
        mp.start();
    }

    public void play() {
        if (playingIndex >= amdroid.playlistCurrent.size()) {
            playingIndex = amdroid.playlistCurrent.size();
            mc.hide();
            return;
        }
        
        if (playingIndex < 0) {
            playingIndex = 0;
            return;
        }

        Song chosen = (Song) amdroid.playlistCurrent.get(playingIndex);

        if (mp.isPlaying()) {
            mp.stop();
        }
        
        mp.reset();
        try {
            mp.setDataSource(chosen.url);
            mp.prepare();
        } catch (java.io.IOException blah) {
            return;
        }
        mp.start();
    }

    public void onCompletion(MediaPlayer media) {
        playingIndex++;
        play();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        playingIndex = position;
        play();
        mc.show(0);
    }

}
