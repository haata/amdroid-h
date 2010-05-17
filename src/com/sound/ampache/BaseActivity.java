package com.sound.ampache;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sound.ampache.objects.Song;
import com.sound.ampache.objects.ampacheObject;

public abstract class BaseActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {

    // List to keep track of local history. Used to override back button
    // behavior
    protected LinkedList<String[]> history = new LinkedList<String[]>();

    // This needs to be instantiated in the implementing subclasses
    protected ListView lv;
    protected String[] directive;
    protected Boolean isFetching = false;
    protected ProgressDialog dlog;
    protected Spinner searchCriteria;
    protected EditText searchString;

    protected DataHandler dataHandler;

    // ArrayList and adapter to display dynamic content fetched from the ampache server
    protected ArrayList<ampacheObject> list;
    protected collectionAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHandler = new DataHandler();
        list = new ArrayList<ampacheObject>();
        listAdapter = new collectionAdapter(this, R.layout.browsable_item, list);
    }
    
    public void onDestroy() {
        super.onDestroy();
        if (isFetching) {
            dataHandler.removeMessages(0x1336);
            dataHandler.removeMessages(0x1337);
            dataHandler.stop=true;
            amdroid.requestHandler.stop = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        ampacheObject val = null;

        val = (ampacheObject) adapterView.getItemAtPosition(position);
        if (val == null)
            return;
        if (val.getType().equals("Song")) {
            Toast.makeText(this, "Enqueue " + val.getType() + ": " + val.toString(), Toast.LENGTH_LONG).show();
            amdroid.addPlaylistCurrent((Song) val);
            return;
        }
        directive[0] = val.childString();
        directive[1] = val.id;

        updateList(true);
    }
    
    public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {

        ampacheObject cur = (ampacheObject) l.getItemAtPosition(position);
        Toast.makeText(this, "Enqueue " + cur.getType() + ": " + cur.toString(), Toast.LENGTH_LONG).show();
        if (cur.hasChildren()) {
            Message requestMsg = new Message();
            requestMsg.obj = cur.allChildren();
            requestMsg.what = 0x1339;
            // tell it how to handle the stuff
            requestMsg.replyTo = new Messenger(dataHandler);
            amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
        } else {
            amdroid.addPlaylistCurrent((Song) cur);
        }
        return true;
    }

    public void updateList(boolean setHistory) {

        dataHandler.stop = true;
        dataHandler.removeMessages(0x1337);
        dataHandler.removeMessages(0x1336);
        dataHandler = new DataHandler();

        Message requestMsg = new Message();

        requestMsg.arg1 = 0;
        requestMsg.what = 0x1337;

        listAdapter.clear();
        if (lv.getAdapter() != listAdapter)
            lv.setAdapter(listAdapter);


        requestMsg.obj = directive;

        requestMsg.replyTo = new Messenger(dataHandler);
        amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);

        if (setHistory)
            history.add(directive.clone());

    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
   
    /*
     * Override "back button" behavior on android 1.6
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /*
     * Override "back button" behavior on android 2.0 and later
     */
    public void onBackPressed() {
        if (history.size() > 1) {
            history.removeLast();
            directive=history.getLast().clone();
            updateList(false);
        } else
            ((AmdroidActivityGroup) getParent()).setActivity(AmdroidActivityGroup.GOTO_HOME);
    }

    protected class DataHandler extends Handler {

        public boolean stop = false;

        public void handleMessage(Message msg) {
            if (stop)
                return;
            switch (msg.what) {
            case (0x1336):
                /* Handle incremental updates */
                list.addAll((ArrayList) msg.obj);
                listAdapter.notifyDataSetChanged();

                /* queue up the next inc */
                if (msg.arg1 < msg.arg2) {
                    Message requestMsg = new Message();
                    requestMsg.obj = directive;
                    requestMsg.what = 0x1336;
                    requestMsg.arg1 = msg.arg1 + 100;
                    requestMsg.arg2 = msg.arg2;
                    requestMsg.replyTo = new Messenger(dataHandler);
                    amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
                    // listAdapter.notifyDataSetChanged();
                    // collectionActivity.this.setProgress((10000 * msg.arg1) /
                    // msg.arg2);
                } else {
                    /* we've completed incremental fetch, cache it baby! */
                    //amdroid.cache.putParcelableArrayList(directive[0], list);
                    // collectionActivity.this.setProgress(10000);
                    // getListView().setTextFilterEnabled(true);
                    // isFetching = false;
                }
                break;
            case (0x1337):
                /* Handle primary updates */
                list.addAll((ArrayList) msg.obj);
                // setProgressBarIndeterminateVisibility(false);
                listAdapter.notifyDataSetChanged();
                /*
                 * if (dlog != null) { if (dlog.isShowing()){ dlog.dismiss(); }
                 * }
                 */
                // isFetching = false;
                break;
            case (0x1338):
                /* handle an error */
                // setProgressBarIndeterminateVisibility(false);
                Toast.makeText(BaseActivity.this, "Communicator error:" + (String) msg.obj, Toast.LENGTH_LONG).show();
                // isFetching = false;
                break;
            case (0x1339):
                /* handle playlist enqueues */
                amdroid.addAllPlaylistCurrent((ArrayList) msg.obj);
                break;
            }

        }
    }

}
