package com.sound.ampache.objects;

public class Song extends ampacheObject {
    public String artist = "";
    public String art = "";
    public String url = "";

    public Song() {
        type = "song";
    }
}
