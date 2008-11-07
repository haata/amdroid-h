package com.sound.ampache;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import com.sound.ampache.objects.Song;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;
import android.net.Uri;

public final class playlistActivity extends Activity implements MediaPlayerControl, OnBufferingUpdateListener, OnCompletionListener, OnItemClickListener
{
    private MediaController mc;
    private ListView lv;

    private playlistAdapter pla;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playlist);

        amdroid.mp.setOnBufferingUpdateListener(this);
        amdroid.mp.setOnCompletionListener(this);
        TextView menuText = (TextView) findViewById(R.id.menutext);
        mc = new MediaController(this, false);
     
        menuText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mc.show();
                }});

        amdroid.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    amdroid.mp.start();
                    if (amdroid.playListVisible) {
                        mc.show();
                        mc.setEnabled(true);
                    }
                }});

        lv = (ListView) findViewById(R.id.list);
        lv.setOnItemClickListener(this);

        mc.setAnchorView(menuText);
        mc.setPrevNextListeners(new nextList(), new prevList());

        if (amdroid.mp.isPlaying()) {
            mc.setEnabled(true);
        } else {
            mc.setEnabled(true);
        }
        mc.setMediaPlayer(this);

        pla = new playlistAdapter(this);

        lv.setAdapter(pla);

    }

    @Override
    protected void onResume() {
        super.onResume();
        amdroid.playListVisible = true;
    }
    
    @Override
    protected void onPause() {
        super.onResume();
        amdroid.playListVisible = false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.pl_clear:
            if (isPlaying())
                amdroid.mp.stop();
            amdroid.playingIndex = 0;
            pla.clearItems();
            mc.setEnabled(false);
            return true;
        }
        return true;
    }


    private class prevList implements OnClickListener
    {
        public void onClick(View v) {
            turnOffPlayingView();
            amdroid.playingIndex--;
            play();
        }
    }

    private class nextList implements OnClickListener
    {
        public void onClick(View v) {
            turnOffPlayingView();
            amdroid.playingIndex++;
            play();
        }
    }

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
        amdroid.mp.start();
    }

    public void play() {
        if (amdroid.playingIndex >= amdroid.playlistCurrent.size()) {
            amdroid.playingIndex = amdroid.playlistCurrent.size();
            mc.setEnabled(false);;
            return;
        }

        if (amdroid.playingIndex < 0) {
            amdroid.playingIndex = 0;
            return;
        }

        Song chosen = (Song) amdroid.playlistCurrent.get(amdroid.playingIndex);

        if (amdroid.mp.isPlaying()) {
            amdroid.mp.stop();
        }

        amdroid.mp.reset();
        try {
            amdroid.mp.setDataSource(chosen.liveUrl());
            amdroid.mp.prepareAsync();
        } catch (java.io.IOException blah) {
            return;
        }
        turnOnPlayingView();
    }

    private void turnOffPlayingView() {
        if (amdroid.playingIndex >= lv.getFirstVisiblePosition() && amdroid.playingIndex <= lv.getLastVisiblePosition()) {
            View holder = lv.getChildAt(amdroid.playingIndex - lv.getFirstVisiblePosition());
            if (holder != null) {
                ImageView img = (ImageView) holder.findViewById(R.id.art);
                img.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void turnOnPlayingView() {
        if (amdroid.playingIndex >= lv.getFirstVisiblePosition() && amdroid.playingIndex <= lv.getLastVisiblePosition()) {
            View holder = lv.getChildAt(amdroid.playingIndex - lv.getFirstVisiblePosition());
            if (holder != null) {
                ImageView img = (ImageView) holder.findViewById(R.id.art);
                img.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onCompletion(MediaPlayer media) {
        turnOffPlayingView();
        amdroid.playingIndex++;
        play();
    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        turnOffPlayingView();
        amdroid.playingIndex = position;
        play();
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

        public void clearItems() {
            amdroid.playlistCurrent.clear();
            notifyDataSetChanged();
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

                convertView.setTag(holder);
            } else {
                holder = (plI) convertView.getTag();
            }

            holder.title.setText(cur.name);
            holder.other.setText(cur.artist + " - " + cur.album);
            if (amdroid.mp.isPlaying() && amdroid.playingIndex == position) {
                holder.art.setVisibility(View.VISIBLE);
            } else {
                holder.art.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    static class plI {
        TextView title;
        TextView other;
        ImageView art;
    }

}
