package com.sound.ampache;

/* Copyright (c) 2008-2009 Kevin James Purdy <purdyk@gmail.com>                                              
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

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
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

public final class songSearch extends ListActivity {
    
    public searchHandler searchCompleteHandler;
    private String[] directive;
    private ArrayList<ampacheObject> list = null;
    private ProgressDialog dlog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        amdroid.comm.ping();

        final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();

        setTitle("Music Search");

        searchCompleteHandler = new searchHandler();

        // Are we being 're-created' ?
        list = savedInstanceState != null ? (ArrayList) savedInstanceState.getParcelableArrayList("list") : null;

        if (Intent.ACTION_SEARCH.equals(queryAction) && list == null) {
            directive = new String[2];
            directive[0] = "search_songs";

            try {
                directive[1] = URLEncoder.encode(queryIntent.getStringExtra(SearchManager.QUERY), "UTF-8");
            } catch (Exception poo) {
                return;
            }
            
            Message searchMsg = new Message();
            searchMsg.arg1 = 0;
            searchMsg.what = 0x1337;
            searchMsg.obj = directive;

            dlog = ProgressDialog.show(this, "", "Searching...", true);
            
            searchMsg.replyTo = new Messenger(searchCompleteHandler);
            list = new ArrayList();
            searchCompleteHandler.ca = new collectionAdapter(this, R.layout.browsable_item, list);
            
            setListAdapter(searchCompleteHandler.ca);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(searchMsg);
        } else {
            setListAdapter(new collectionAdapter(this, R.layout.browsable_item, list));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Add All").setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            amdroid.playlistCurrent.addAll((ArrayList)list);
            break;
            
        default:
            return false;
        }
        return true;
    }
    
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList("list", list);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        ampacheObject val = (ampacheObject) l.getItemAtPosition(position);
        Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
        amdroid.playlistCurrent.add((Song) val);
        return;
    }
    
    private class searchHandler extends Handler {
        public collectionAdapter ca;
        
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case (0x1337):
                list.addAll((ArrayList) msg.obj);
                dlog.dismiss();
                ca.notifyDataSetChanged();
                break;
            }
        }
        
    }
}
