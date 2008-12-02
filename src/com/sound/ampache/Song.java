package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.lang.ClassNotFoundException;

public class Song extends ampacheObject implements Externalizable {
    public String artist = "";
    public String art = "";
    public String url = "";
    public String album = "";
    public String genre = "";

    public String getType() {
        return "Song";
    }

    public String childString() {
        return "";
    }

    /* Replace the old session id with our current one */
    public String liveUrl() {
        return url.replaceAll("sid=[^&]+","sid=" + com.sound.ampache.amdroid.comm.authToken);
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

    /* for external */

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();
        name = (String) in.readObject();
        artist = (String) in.readObject();
        art = (String) in.readObject();
        url = (String) in.readObject();
        album = (String) in.readObject();
        genre = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(id);
        out.writeObject(name);
        out.writeObject(artist);
        out.writeObject(art);
        out.writeObject(url);
        out.writeObject(album);
        out.writeObject(genre);
    }

}
