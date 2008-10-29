package com.sound.ampache;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.ArrayList;
import com.sound.ampache.objects.*;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;
import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.lang.Integer;
import java.lang.Long;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

public class ampacheCommunicator
{

    public String authToken = "";
    private int artists;
    private String update;
    private Context mCtxt;

    private XMLReader reader;

    private SharedPreferences prefs;

    ampacheCommunicator(SharedPreferences preferences, Context context) throws Exception {
        prefs = preferences;
        mCtxt = context;
        System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
        reader = XMLReaderFactory.createXMLReader();
    }

    public void perform_auth_request() throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        /* Get the current time, and convert it to a string */
        String time = Long.toString((new Date()).getTime() / 1000);
        
        /* build our passphrase hash */
        md.reset();
        String preHash = time + prefs.getString("server_password_preference", "");
        md.update(preHash.getBytes(), 0, preHash.length());
        String hash = asHex(md.digest());
        
        /* request server auth */
        ampacheAuthParser hand = new ampacheAuthParser();
        reader.setContentHandler(hand);
        String user = prefs.getString("server_username_preference", "");
        if (!user.equals("")) {
            reader.parse(new InputSource(fetchFromServer("action=handshake&auth="+hash+"&timestamp="+time+"&version=350001"+"&user="+user)));
        } else {
            reader.parse(new InputSource(fetchFromServer("action=handshake&auth="+hash+"&timestamp="+time+"&version=350001")));
        }

        authToken = hand.token;
        artists = hand.artists;
        update = hand.update;
    }

    public ArrayList fetch(String type, String filter) throws Exception{
        dataHandler hand;
        String append = "";

        if (type.equals("artists")) {
            boolean goodcache = true;

            /*
            try {
                FileInputStream din = mCtxt.openFileInput("date");
                FileInputStream cin = mCtxt.openFileInput("cache");
                ObjectInputStream doi = new ObjectInputStream(din);
                String cDate = (String) doi.readObject();
                doi.close();
                if (cDate.equals(update)) {
                    ObjectInputStream coi = new ObjectInputStream(cin);
                    ArrayList<ampacheObject> goods = (ArrayList) coi.readObject();
                    coi.close();
                    return goods;
                }
            } catch (Exception poo) {
                /* cache load failed for some reason 
            }
            */
            append = "action=artists&auth=" + authToken + "&limit=10";
            hand = new ampacheArtistParser();
        } else if (type.equals("artist_albums")) {
            append = "action=artist_albums&filter=" + filter + "&auth=" + authToken;
            hand = new ampacheAlbumParser();
        } else if (type.equals("album_songs")) {
            append = "action=album_songs&filter=" + filter + "&auth=" + authToken;
            hand = new ampacheSongParser();
        } else {
            return new ArrayList();
        }
        
        reader.setContentHandler(hand);
        reader.parse(new InputSource(fetchFromServer(append)));

        /*
        if (type.equals("artists")) {
            try {
                /* we just did a full fetch, write the cache! 
                FileOutputStream dot = mCtxt.openFileOutput("date", 0);
                FileOutputStream cot = mCtxt.openFileOutput("cache", 0);
                ObjectOutputStream dos = new ObjectOutputStream(dot);
                dos.writeObject(update);
                dos.close();
                ObjectOutputStream cos = new ObjectOutputStream(cot);
                cos.writeObject(hand.data);
                cos.close();
            } catch (Exception poo) {
                /* so much for the cache 
            }
        } */

        return hand.data;
    }
    
    public InputStream fetchFromServer(String append) throws Exception {
        URL fullUrl = new URL(prefs.getString("server_url_preference", "") + "/server/xml.server.php?" + append);
        return fullUrl.openStream();
    }

    private class dataHandler extends DefaultHandler {
        public ArrayList data = new ArrayList();
    }
    private class ampacheAuthParser extends DefaultHandler {
        public String token = "";
        public int artists= 0;
        public String update = "";
        private CharArrayWriter contents = new CharArrayWriter();

        public void startDocument() throws SAXException {
            
        }

        public void endDocument() throws SAXException {

        }

        public void startElement( String namespaceURI,
                                  String localName,
                                  String qName,
                                  Attributes attr) throws SAXException {

            contents.reset();
        }

        public void endElement( String namespaceURI,
                                String localName,
                                String qName) throws SAXException {

            if (localName.equals("auth")) {
                token = contents.toString();
            }

            if (localName.equals("artists")) {
                artists = Integer.parseInt(contents.toString());
            }

            if (localName.equals("update")) {
                update = contents.toString();
            }
        }
        
        public void characters( char[] ch, int start, int length )throws SAXException {
            contents.write( ch, start, length );
        }
    }
    
    private class ampacheArtistParser extends dataHandler {
        private Artist current;
        private CharArrayWriter contents = new CharArrayWriter();

        public void startDocument() throws SAXException {
            
        }
        
        public void endDocument() throws SAXException {

        }
        
        public void startElement( String namespaceURI,
                                  String localName,
                                  String qName,
                                  Attributes attr) throws SAXException {
            
            if (localName.equals("artist")) {
                current = new Artist();
                current.id = attr.getValue("id");
            }
            
            contents.reset();
        }
        
        public void endElement( String namespaceURI,
                                String localName,
                                String qName) throws SAXException {
            
            if (localName.equals("name")) {
                current.name = contents.toString();
            }

            if (localName.equals("artist")) {
                data.add(current);
            }

        }
        
        public void characters( char[] ch, int start, int length ) throws SAXException {
            contents.write( ch, start, length );
        }
    }
    
    private class ampacheAlbumParser extends dataHandler {
        private Album current;
        private CharArrayWriter contents = new CharArrayWriter();
        
        public void startDocument() throws SAXException {
            
        }
        
        public void endDocument() throws SAXException {
            
        }
        
        public void startElement( String namespaceURI,
                                  String localName,
                                  String qName,
                                  Attributes attr) throws SAXException {
            
            if (localName.equals("album")) {
                current = new Album();
                current.id = attr.getValue("id");
            }
            
            contents.reset();
        }
        
        public void endElement( String namespaceURI,
                                String localName,
                                String qName) throws SAXException {
            
            if (localName.equals("name")) {
                current.name = contents.toString();
            }
            if (localName.equals("album")) {
                data.add(current);
            }
        }
        
        public void characters( char[] ch, int start, int length )throws SAXException {
            contents.write( ch, start, length );
        }
    }
    

    private class ampacheSongParser extends dataHandler {
        private Song current;
        private CharArrayWriter contents = new CharArrayWriter();
        
        public void startDocument() throws SAXException {
            
        }
        
        public void endDocument() throws SAXException {
            
        }
        
        public void startElement( String namespaceURI,
                                  String localName,
                                  String qName,
                                  Attributes attr) throws SAXException {
            
            if (localName.equals("song")) {
                current = new Song();
                current.id = attr.getValue("id");
            }
            
            contents.reset();
        }
        
        public void endElement( String namespaceURI,
                                String localName,
                                String qName) throws SAXException {
            
            if (localName.equals("song")) {
                data.add(current);
            }
            
            if (localName.equals("title")) {
                current.name = contents.toString();
            }
            
            if (localName.equals("artist")) {
                current.artist = contents.toString();
            }
            
            if (localName.equals("art")) {
                current.art = contents.toString();
            }
            
            if (localName.equals("url")) {
                current.url = contents.toString();
            }

            if (localName.equals("album")) {
                current.album = contents.toString();
            }

            if (localName.equals("genre")) {
                current.genre = contents.toString();
            }
        }
        
        public void characters( char[] ch, int start, int length )throws SAXException {
            contents.write( ch, start, length );
        }
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
            {
                chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
                chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
            }
        return new String(chars);
    }
}
