package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Genre extends ampacheObject {
    public String getType() {
        return "Genre";
    }

    public boolean hasChildren() {
        return true;
    }

    public ArrayList allChildren() {
        try {
            return com.sound.ampache.amdroid.comm.fetch("genre_songs", this.id);
        } catch (Exception poo) {
            return new ArrayList();
        }
    }

    public String childString() {
        return "genre_artists";
    }

    public Genre() {
    }

    public Genre(Parcel in) {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Genre createFromParcel(Parcel in) {
                    return new Genre(in);
                }

                public Genre[] newArray(int size) {
                    return new Genre[size];
                }
            };
}
