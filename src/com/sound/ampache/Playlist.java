package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Playlist extends ampacheObject {
    public String getType() {
        return "Playlist";
    }
    
    public boolean hasChildren() {
        return true;
    }
    
    public String childString() {
        return "playlist_songs";
    }

    public ArrayList allChildren() {
        try {
            return com.sound.ampache.amdroid.comm.fetch("playlist_songs", this.id);
        } catch (Exception poo) {
            return new ArrayList();
        }
    }

    public Playlist() {
    }

    public Playlist(Parcel in) {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Playlist createFromParcel(Parcel in) {
                    return new Playlist(in);
                }

                public Playlist[] newArray(int size) {
                    return new Playlist[size];
                }
            };
}
