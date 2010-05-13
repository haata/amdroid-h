package com.sound.ampache;

import java.util.ArrayList;

import com.sound.ampache.objects.ampacheObject;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class DataHandler extends Handler {
    
    public Boolean stop = false;
    
    protected collectionAdapter ca;
    protected String[] directive;
    
    public DataHandler(collectionAdapter ca, String[] directive){
        super();
        this.ca=ca;
        this.directive=directive;
    }

    public void handleMessage(Message msg) {
        
        if (stop)
            return;
        switch (msg.what) {
        case (0x1336):
            /* Handle incremental updates */
            ca.setNotifyOnChange(false);
            ArrayList<ampacheObject> a = (ArrayList) msg.obj;
            for (int i = 0; i < a.size(); i++)
                ca.add(a.get(i));
            ca.notifyDataSetChanged();
            //list.addAll((ArrayList) msg.obj);
            
            /* queue up the next inc */
            if (msg.arg1 < msg.arg2) {
                Message requestMsg = new Message();
                requestMsg.obj = directive;
                requestMsg.what = 0x1336;
                requestMsg.arg1 = msg.arg1 + 100;
                requestMsg.arg2 = msg.arg2;
                requestMsg.replyTo = new Messenger(this);
                amdroid.requestHandler.incomingRequestHandler.sendMessage(requestMsg);
                //ca.notifyDataSetChanged();
                //collectionActivity.this.setProgress((10000 * msg.arg1) / msg.arg2);
            } else {
                /* we've completed incremental fetch, cache it baby! */
                //ca.notifyDataSetChanged();
                //amdroid.cache.putParcelableArrayList(directive[0], list);
                //collectionActivity.this.setProgress(10000);
                //getListView().setTextFilterEnabled(true);
                //isFetching = false;
            }
            break;
        case (0x1337):
            /* Handle primary updates */
            //list.addAll((ArrayList) msg.obj);
            ca.setNotifyOnChange(false);
            a = (ArrayList) msg.obj;
            for (int i = 0; i < a.size(); i++)
                ca.add(a.get(i));
            //setProgressBarIndeterminateVisibility(false);
            ca.notifyDataSetChanged();
            /*if (dlog != null) {
                if (dlog.isShowing()){
                    dlog.dismiss();
                }
            }*/
            //isFetching = false;
            break;
        case (0x1338):
            /* handle an error */
            //setProgressBarIndeterminateVisibility(false);
            //Toast.makeText(collectionActivity.this, "Communicator error:" + (String) msg.obj, Toast.LENGTH_LONG).show();
            //isFetching = false;
            break;
        case (0x1339):
            /* handle playlist enqueues */
            amdroid.addAllPlaylistCurrent((ArrayList) msg.obj);
            break;
        }
    }
}
