import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.ArrayList;
import com.sound.ampache.objects.*;

public class ampacheCommunicator
{

    private fetch(String type, String filter) {

    }
    
    
    private class ampacheSongParser extends DefaultHandler {
	private ArrayList<Song> data = new ArrayList();
	private Song current;
	private CharArrayWriter contents = new CharArrayWriter();

	public void ampacheBrowsableParser(String baseElement) {

	}
	
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
		current.title = contents.toString();
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
	}
	
	public void characters( char[] ch, int start, int length )throws SAXException {
	    contents.write( ch, start, length );
	}
    }
}