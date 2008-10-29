package com.sound.ampache.objects;

import java.util.ArrayList;

public class Song extends ampacheObject {
    public String artist = "";
    public String art = "";
    public String url = "";
    public String album = "";
    public String genre = "";

    public Song() {
        type = "song";
    }
    
    public boolean hasChildren() {
	return false;
    }

    public ArrayList allChildren() {
	return new ArrayList();
    }

}
