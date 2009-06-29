package com.sound.ampache.objects;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcel;

public class Tag extends ampacheObject {
    public String artists = "";
    public String albums = "";
    public String extra = null;

    public String getType() {
        return "Tag";
    }

    public String extraString() {
	if (extra == null) {
	    extra = artists + " artists - " + albums + " albums";
	}
	return extra;
    }

    public boolean hasChildren() {
        return true;
    }

    public String[] allChildren() {
        String[] dir = {"tag_songs", this.id};
        return dir;
    }

    public String childString() {
        return "tag_artists";
    }
    
    public Tag() {
    }

    public Tag(Parcel in) {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR
        = new Parcelable.Creator() {
                public Tag createFromParcel(Parcel in) {
                    return new Tag(in);
                }

                public Tag[] newArray(int size) {
                    return new Tag[size];
                }
            };
}
