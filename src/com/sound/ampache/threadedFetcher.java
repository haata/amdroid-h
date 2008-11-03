package com.sound.ampache;

import java.net.*;
import java.io.*;

public class threadedFetcher extends Thread 
{
    private URL url;
    private fetchReceiver recv = null;

    public void setUrl(URL inurl) {
        url = inurl;
    }
    
    public void setDataListener(fetchReceiver inrecv) {
        recv = inrecv;
    }        
    
    public void run() {
        if (recv != null) {
            try {
                recv.receiveData(url.openStream());
            } catch (Exception poo) {
            }
        }
    }
    
    public interface fetchReceiver
    {
        public void receiveData(InputStream data);
    }
}
