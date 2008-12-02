package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Album extends ampacheObject {
    public String getType() {
        return "Album";
    }

    public String childString() {
        return "album_songs";
    }

    public boolean hasChildren() {
	return true;
    }

    public String[] allChildren() {
        String[] dir = {"album_songs", this.id};
        return dir;
    }

    public Album() {
    }

    public Album(Parcel in) {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Album createFromParcel(Parcel in) {
                    return new Album(in);
                }

                public Album[] newArray(int size) {
                    return new Album[size];
                }
            };
}
