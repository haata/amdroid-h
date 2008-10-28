package com.sound.ampache.objects;

public class Song extends ampacheObject {
    public String artist = "";
    public String art = "";
    public String url = "";
    public String album = "";
    public String genre = "";

    public Song() {
        type = "song";
    }
}
