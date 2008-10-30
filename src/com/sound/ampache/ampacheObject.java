package com.sound.ampache.objects;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class ampacheObject implements Serializable {
    public String id = "";
    public String name = "";
    
    public String getId() {
        return id;
    }
    
    public String toString() {
        return name;
    }

    abstract public String getType();

    abstract public ArrayList allChildren();

    abstract public boolean hasChildren();
}
