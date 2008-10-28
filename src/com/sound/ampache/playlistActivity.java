package com.sound.ampache;


import android.app.ListActivity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import com.sound.ampache.objects.Song;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;
import android.net.Uri;

public final class playlistActivity extends ListActivity implements MediaPlayerControl, OnBufferingUpdateListener, OnCompletionListener
{

    private MediaPlayer mp;
    private MediaController mc;

    private int playingIndex;
    private int bufferPC;
    private Boolean playing;
    private playlistAdapter pla;

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

        pla = new playlistAdapter(this);

        setListAdapter(pla);

        //getListView().addFooterView(mc);

        //setListAdapter(new ArrayAdapter<Song> (this, android.R.layout.simple_list_item_1, amdroid.playlistCurrent));

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
        getListView().invalidateViews();
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

    private class playlistAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        public playlistAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return amdroid.playlistCurrent.size();
        }

        public Object getItem(int position) {
            return amdroid.playlistCurrent.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            plI holder;
            Song cur = amdroid.playlistCurrent.get(position);
            
            /* we don't reuse */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.playlist_item, null);
                holder = new plI();

                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.other = (TextView) convertView.findViewById(R.id.other);
                holder.art = (ImageView) convertView.findViewById(R.id.art);
                //holder.isplaying = (ImageView) convertView.findViewById(R.id.isplaying);
                
                convertView.setTag(holder);
            } else {
                holder = (plI) convertView.getTag();
            }
            
            holder.title.setText(cur.name);
            holder.other.setText(cur.artist + " - " + cur.album);
            //holder.art.setImageURI(Uri.parse(cur.art));
            if (mp.isPlaying() && playingIndex == position) {
                holder.art.setImageResource(android.R.drawable.ic_media_play);
            }
            return convertView;
        }
    }

    static class plI {
        TextView title;
        TextView other;
        ImageView art;
        //ImageView isplaying;
    }

}
