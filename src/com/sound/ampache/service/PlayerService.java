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

import java.util.Arrays;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sound.ampache.objects.*;
import com.sound.ampache.service.IPlayerService;
import com.sound.ampache.utility.Player;
import com.sound.ampache.utility.Playlist;

public class PlayerService extends Service {
	private static final String LOG_TAG = "Amdroid_PlayerService";

	private Player mediaPlayer;
	private Playlist playlist;

	@Override
	public IBinder onBind( Intent intent ) {
		Log.d( LOG_TAG, "onBind" );

		// Make sure it's a valid request
		if ( IPlayerService.class.getName().equals( intent.getAction() ) )
			return mBinder;

			return mBinder;
	}

	@Override
	public void onRebind( Intent intent ) {
		// All clients disconnected, and another connection is made
		Log.d( LOG_TAG, "onRebind" );
	}

	@Override
	public boolean onUnbind( Intent intent ) {
		Log.d( LOG_TAG, "onUnbind" );
		return true;
	}

	@Override
	public void onCreate() {
		Log.d( LOG_TAG, "onCreate" );
		mediaPlayer = new Player( this );
		playlist = new Playlist();
	}

	@Override
	public void onDestroy() {
		Log.d( LOG_TAG, "onDestroy" );
		mediaPlayer.quit(); // Cleanup the telephony handler
	}

	@Override
	protected void finalize() {
		Log.d( LOG_TAG, "Android hath slain me :(killed):" );
		// TODO Warn the user (text) that the VM is killing the service
	}


	// Interface **********************************************************

	private final IPlayerService.Stub mBinder = new IPlayerService.Stub() {
		// Player Status
		public boolean isPlaying() {
			return mediaPlayer.isPlaying();
		}
		public boolean isSeekable() {
			return mediaPlayer.isSeekable();
		}
		public int getBuffer() {
			return mediaPlayer.getBuffer();
		}
		public int getCurrentPosition() {
			return mediaPlayer.getCurrentPosition();
		}
		public int getDuration() {
			return mediaPlayer.getDuration();
		}

		// Player Controls
		public void playMedia( Media media ) {
			mediaPlayer.playMedia( media );
		}
		public void playPause() {
			mediaPlayer.doPauseResume();
		}
		public void stop() {
			mediaPlayer.stop();
		}
		public void next() {
			mediaPlayer.playMedia( playlist.next() );
		}
		public void prev() {
			mediaPlayer.playMedia( playlist.prev() );
		}
		public void seek( int msec ) {
			mediaPlayer.seekTo( msec );
		}

		// Playlist Controls
		public Media nextItem() {
			return playlist.next();
		}
		public Media prevItem() {
			return playlist.prev();
		}

		// Playlist List Modifiers
		public Media[] currentPlaylist() {
			Media[] tmp = new Media[ playlist.size() ];

			for ( int c = 0; c < playlist.size(); c++ ) {
				tmp[c] = playlist.get( c );
			}

			return tmp;
		}
		public boolean add( Media media ) {
			return playlist.add( media );
		}
		public boolean enqueue( Media[] media ) {
			// Adds the given list of media items to the playlist
			return playlist.addAll( Arrays.asList( media ) );
		}
		public boolean replace( Media[] media ) {
			// Clears the playlist and replaces it with the given one
			playlist.clearPlaylist();
			playlist.clearShuffleHistory();
			return playlist.addAll( Arrays.asList( media ) );
		}
		public void clearPlaylist() {
			playlist.clearPlaylist();
		}
		
		// Playlist Item
		public int getCurrentIndex() {
			return playlist.getCurrentIndex();
		}
		public int getPlaylistSize() {
			return playlist.size();
		}
		public Media getCurrentMedia() {
			return playlist.getCurrentMedia();
		}
		public Media setCurrentIndex( int index ) {
			return playlist.setCurrentIndex( index );
		}
		
		// Shuffle/Repeat
		public boolean getShufflePlay() {
			return playlist.getShufflePlay();
		}
		public boolean getRepeatPlay() {
			return playlist.getRepeatPlay();
		}
		public void setShufflePlay( boolean randomize ) {
			playlist.setShufflePlay( randomize );
		}
		public void setRepeatPlay( boolean loop ) {
			playlist.setRepeatPlay( loop );
		}
		public void clearShuffleHistory() {
			playlist.clearShuffleHistory();
		}

		// Misc
		public void closeService() {
		}
	};
}

