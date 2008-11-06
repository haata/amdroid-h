package com.sound.ampache.objects;

import java.util.ArrayList;

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

}
