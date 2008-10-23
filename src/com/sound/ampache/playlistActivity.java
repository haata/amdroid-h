package com.sound.ampache;


import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.MediaController;

public final class playlistActivity extends ListActivity
{

    private MediaPlayer mp;
    private MediaController mc;

    private int playingIndex;
    private Boolean playing;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        mp = new MediaPlayer();
        
        mc = new MediaController(this);

        mc.setAnchorView(this.getListView());
        mc.setEnabled(true);

        setListAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, songs));

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        playingIndex = position;
        if (mp.isPlaying()) {
            mp.stop();
        }

        mp.reset();
        try {
            mp.setDataSource(urls[position]);
            mp.prepare();
        } catch (java.io.IOException blah) {
            return;
        }
        mp.start();
    }

    private String[] songs = {"The Rapture - Don Gon Do It", "The Rapture - Pieces Of The People We Love"};
    private String[] urls = {"http://lt-dan.sandbenders.org/ampache/play/index.php?song=18549&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Don%20Gon%20Do%20It.mp3", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18540&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Pieces%20Of%20The%20People%20We%20Love.mp3"};

    /*
    songs = {
        {"The Rapture - Don Gon Do It", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18549&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Don%20Gon%20Do%20It.mp3"},
        {"The Rapture - Pieces Of The People We Love", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18540&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Pieces%20Of%20The%20People%20We%20Love.mp3"},
        {"The Rapture - Get Myself Into It", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18547&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Get%20Myself%20Into%20It.mp3"},
        {"The Rapture - First Gear", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18548&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20First%20Gear.mp3"},
        {"The Rapture - The Devil", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18545&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20The%20Devil.mp3"},
        {"The Rapture - Whoo! Alright - Yeah...Uh Huh", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18542&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Whoo%21%20Alright%20-%20Yeah...Uh%20Huh.mp3"},
        {"The Rapture - Calling Me", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18544&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Calling%20Me.mp3"},
        {"The Rapture - Down For So Long", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18543&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Down%20For%20So%20Long.mp3"},
        {"The Rapture - The Sound", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18541&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20The%20Sound.mp3"},
        {"The Rapture - Live In Sunshine", "http://lt-dan.sandbenders.org/ampache/play/index.php?song=18546&uid=2&sid=4d75504d2ed715a136065e7bb99c4962&name=/The%20%20Rapture%20-%20Live%20In%20Sunshine.mp3"}}
    */
}
