package com.sound.ampache;

/* Copyright (c) 2008-2009 	Kevin James Purdy <purdyk@gmail.com>                                              
 * Copyright (c) 2010 		Krisopher Heijari <iif.ftw@gmail.com>
 *                                                                                                           
 * +------------------------------------------------------------------------+                                
 * | This program is free software; you can redistribute it and/or          |                                
 * | modify it under the terms of the GNU General Public License            |                                
 * | as published by the Free Software Foundation; either version 2         |
 * | of the License, or (at your option) any later version.                 |
 * |                                                                        |
 * | This program is distributed in the hope that it will be useful,        |
 * | but WITHOUT ANY WARRANTY; without even the implied warranty of         |                                
 * | MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          |                                
 * | GNU General Public License for more details.                           |                                
 * |                                                                        |                                
 * | You should have received a copy of the GNU General Public License      |                                
 * | along with this program; if not, write to the Free Software            |                                
 * | Foundation, Inc., 59 Temple Place - Suite 330,                         |                                
 * | Boston, MA  02111-1307, USA.                                           |                                
 * +------------------------------------------------------------------------+                                
 */

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import android.widget.ListView;
import com.sound.ampache.objects.*;
import java.util.ArrayList;
import java.net.URLEncoder;

// The songSearch activity catches searches made via the search button. It only acts as a wrapper 
// for the collectionACtivity. 
public final class songSearch extends Activity {
    
    public void onCreate( Bundle savedInstanceState ) 
    {
    	// We set visibility to false since we never want this activity to actually be shown
    	setVisible(false);
    	super.onCreate(savedInstanceState);
    	String[] directive;
        Intent searchIntent = getIntent();
    	final String searchAction = searchIntent.getAction();
    	
    	if ( Intent.ACTION_SEARCH.equals(searchAction) ) 
    	{
    		directive = new String[2];
            directive[0] = "search_songs";
	        try {
	            directive[1] = URLEncoder.encode(searchIntent.getStringExtra(SearchManager.QUERY), "UTF-8");
	        } catch (Exception poo) {
	            return;
	        }
	        
	        Intent intentNew = new Intent().setClass(this, collectionActivity.class);
	    	intentNew = intentNew.putExtra("directive", directive).putExtra("title", "Song Search");
	    	
	    	startActivity(intentNew);
	    	
	    	// We finish this activity after starting the new one. This way the back button will skip past
	    	// this activity so we avoid a black screen when pressing back after searching. 
	    	finish();
    	}
    }
}
