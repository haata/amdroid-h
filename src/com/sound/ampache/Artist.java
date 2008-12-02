package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Artist extends ampacheObject {
    public boolean hasChildren() {
        return true;
    }
    
    public String getType() {
        return "Artist";
    }

    public String childString() {
        return "artist_albums";
    }
    
    public String[] allChildren() {
        String[] dir = {"artist_songs", this.id};
        return dir;
    }

    public Artist() {
    }

    public Artist(Parcel in) {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Artist createFromParcel(Parcel in) {
                    return new Artist(in);
                }
                
                public Artist[] newArray(int size) {
                    return new Artist[size];
                }
            };
}

