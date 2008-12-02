package com.sound.ampache.objects;
import android.os.Parcelable; 
import android.os.Parcel;
import java.util.ArrayList;

public abstract class ampacheObject implements Parcelable {
    public String id = "";
    public String name = "";
    
    public String getId() {
        return id;
    }
    
    public String toString() {
        return name;
    }

    abstract public String getType();

    abstract public String childString();

    abstract public ArrayList allChildren();

    abstract public boolean hasChildren();

    /* for parcelable*/
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
    }
}
