package com.sound.ampache.objects;
import java.util.ArrayList;

public class Album extends ampacheObject {
    public Album() {
        type = "album";
    }

    public boolean hasChildren() {
	return true;
    }

    public ArrayList allChildren() {
	try {
	    return com.sound.ampache.amdroid.comm.fetch("album_songs", this.id);
	} catch (Exception poo) {
	    return new ArrayList();
	}
    }
}
