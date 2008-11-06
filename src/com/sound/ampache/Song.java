package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Song extends ampacheObject {
    public String artist = "";
    public String art = "";
    public String url = "";
    public String album = "";
    public String genre = "";

    public String getType() {
        return "song";
    }

    public String liveUrl() {
        return url.replaceAll("auth=[^&]+","auth=" + com.sound.ampache.amdroid.comm.authToken);
    }
    
    public boolean hasChildren() {
        return false;
    }

    public ArrayList allChildren() {
        return new ArrayList();
    }

    public Song() {
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(artist);
        out.writeString(art);
        out.writeString(url);
        out.writeString(album);
        out.writeString(genre);
    }

    public Song(Parcel in) {
        super.readFromParcel(in);
        artist = in.readString();
        art = in.readString();
        url = in.readString();
        album = in.readString();
        genre = in.readString();
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Song createFromParcel(Parcel in) {
                    return new Song(in);
                }

                public Song[] newArray(int size) {
                    return new Song[size];
                }
            };

}
