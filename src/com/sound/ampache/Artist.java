package com.sound.ampache.objects;
import java.util.ArrayList;

public class Artist extends ampacheObject {
    public boolean hasChildren() {
	return true;
    }

    public String getType() {
	return "artist";
    }

    public ArrayList allChildren() {
        try {
            ArrayList<ampacheObject> theList = new ArrayList();
            ArrayList<ampacheObject> sublist = com.sound.ampache.amdroid.comm.fetch("artist_albums", this.id);
            for (int i = 0; i < sublist.size(); i++) {
                theList.addAll(sublist.get(i).allChildren());
            }
            return theList;
        } catch (Exception poo) {
            return new ArrayList();
        }
    }
}

