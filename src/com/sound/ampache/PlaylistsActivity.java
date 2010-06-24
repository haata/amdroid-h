package com.sound.ampache;

import java.util.LinkedList;
import java.util.ListIterator;

import com.sound.ampache.AmpacheListView.IsFetchingListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PlaylistsActivity extends Activity implements IsFetchingListener {
   
	private AmpacheListView ampacheListView;
	private TextView emptyTextView;

	private ProgressBar progressBar;
	private TextView headerTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists_activity);       
        
        emptyTextView = (TextView)findViewById( android.R.id.empty );
		emptyTextView.setText( "<No playlists found>" );

		ampacheListView = (AmpacheListView)findViewById( android.R.id.list );
		ampacheListView.setFastScrollEnabled( true );
		ampacheListView.setEmptyView( emptyTextView );
		ampacheListView.setHeaderDividersEnabled( true );
		ampacheListView.setIsFetchingListener( this );
        
		progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		progressBar.setIndeterminate( true );
		progressBar.setVisibility( View.INVISIBLE );
		headerTextView = (TextView)findViewById(R.id.text_view);
		headerTextView.setText( "Playlists" );
		
        String[] directive = new String[3];
        directive[0] = "playlists";
        directive[1] = "";
        directive[2] = "";
     
        ampacheListView.mDataHandler.enqueMessage( 0x1336, directive, 0, true );  
        
        ampacheListView.backOffset=1;
    }
    
    @Override
	public void onIsFetchingChange( boolean isFetching )
	{
		if ( isFetching )
		{
			progressBar.setVisibility( View.VISIBLE );
		} else
		{
			progressBar.setVisibility( View.INVISIBLE );
		}
		updateHeaderTextView();
	}
    
    private void updateHeaderTextView()
	{
		String append = "Playlists";
		LinkedList<String[]> history = ampacheListView.getHistory();

		ListIterator<String[]> itr = history.listIterator();
		//Increment once to remove the empty history field
		itr.next();
	    while(itr.hasNext())
	    {
	      append += "/"+itr.next()[2];
	    }
	    
	    headerTextView.setText( append );
	}
    
    /*
     * Override "back button" behavior on android 1.6
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            return ampacheListView.backPressed();
        }

        return false;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

}
