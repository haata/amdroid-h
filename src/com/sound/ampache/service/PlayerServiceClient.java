package com.sound.ampache.service;

/* Copyright (c) 2010 Jacob Alexander   < haata@users.sf.net >
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.sound.ampache.service.IPlayerService;

public class PlayerServiceClient {

	private static final String LOG_TAG = "Amdroid_PlayerServiceClient";
	private IPlayerService playerService;
	private PlayerServiceConnection conn;
	private Context mContext;

	public PlayerServiceClient() {
	}

	protected void finalize() {
		releaseService();
	}

	public void initService( Context context ) {
		if ( conn == null ) {
			mContext = context;
			conn = new PlayerServiceConnection();

			Intent intent = new Intent();
			intent.setClassName( "com.sound.ampache", "com.sound.ampache.service.PlayerService" );
			try {
				if ( !mContext.bindService( intent, conn, Context.BIND_AUTO_CREATE ) )
					Log.d( LOG_TAG, "Failed: bindService()" );
			}
			catch ( SecurityException exp ) {
				Log.d( LOG_TAG, "Failed (Security): bindService()" );
			}
			Log.d( LOG_TAG, "bindService()" );
			// TODO Ready
		}
		else {
			// TODO Warn user that init binding failed
		}
	}

	public void releaseService() {
		if ( conn != null ) {
			mContext.unbindService( conn );
			conn = null;
			Log.d( LOG_TAG, "unbindService()" );
		}
		else {
			// TODO Warn user that service was already unbound
		}
	}

	public IPlayerService serviceInterface() {
		// Make sure to check for DeadObjectException when calling interface functions
		if ( conn != null ) {
			if ( playerService != null ) {
				Log.d( LOG_TAG, "Accessing PlayerService interface." );
				return playerService;
			}

			Log.d( LOG_TAG, "Tried accessing PlayerService interface, but it's null..." );
			return null;
		}

		Log.d( LOG_TAG, "Tried accessing PlayerService interface, but connection does not exist..." );
		// TODO Warn user that the service is not bound/connected
		return null;
	}


	class PlayerServiceConnection implements ServiceConnection {
		public void onServiceConnected( ComponentName className, IBinder boundService ) {
			playerService = IPlayerService.Stub.asInterface( (IBinder)boundService );
			Log.d( LOG_TAG, "onServiceConnected" );
		}

		public void onServiceDisconnected( ComponentName className ) {
			playerService = null;
			Log.d( LOG_TAG, "onServiceDisconnected" );
		}
	};
}

