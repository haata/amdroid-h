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

import java.net.URLEncoder;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

public final class songSearch extends BaseActivity implements OnClickListener {

    private Spinner searchCriteria;
    private EditText searchString;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity_layout);

        searchCriteria = (Spinner) findViewById(R.id.search_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.search, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCriteria.setAdapter(adapter);
        searchString = (EditText) findViewById(R.id.search_text);

        lv = (ListView) findViewById(R.id.search_list);
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        // Bind clicklistener for our search button
        ((ImageButton) findViewById(R.id.search_button)).setOnClickListener(this);

        lv.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        String s = searchString.getText().toString();
        if (s.length() <= 0)
            return;

        directive = new String[2];

        String c = (String) searchCriteria.getSelectedItem();
        if (c.equals("All"))
            directive[0] = "search_songs";
        else if (c.equals("Artists"))
            directive[0] = "artists";
        else if (c.equals("Albums"))
            directive[0] = "albums";
        else if (c.equals("Tags"))
            directive[0] = "tags";
        else if (c.equals("Songs"))
            directive[0] = "songs";
        else
            return;

        try {
            directive[1] = URLEncoder.encode(s, "UTF-8");
        } catch (Exception poo) {
            return;
        }

        // Clear history when searching, we should only be able to go back if a search result has been clicked.
        history.clear();
        updateList(true);

    }

}
