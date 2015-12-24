package com.peerless2012.simplemusic;

import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {

	private int pauseProgress;
	
	private MusicInfo musicInfo = new MusicInfo();

	private MediaPlayer mediaPlayer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaPlayer.stop();
				musicInfo.setStatus(MusicInfo.STATUS_STOP);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}
	
	class MusicBinder extends Binder implements IMusic{

		@Override
		public void play() {
			AssetFileDescriptor resourceFd = getResources().openRawResourceFd(R.raw.little_frog);
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(resourceFd.getFileDescriptor());
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						mp.start();
						musicInfo.setDuration(mp.getDuration());
						musicInfo.setChangeId(System.currentTimeMillis());
						musicInfo.setStatus(MusicInfo.STATUS_PLAYING);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void pause() {
			if (mediaPlayer.isPlaying()) {
				pauseProgress = mediaPlayer.getCurrentPosition();
				mediaPlayer.pause();
				musicInfo.setStatus(MusicInfo.STATUS_PAUSE);
			}
		}

		@Override
		public void stop() {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				musicInfo.setStatus(MusicInfo.STATUS_STOP);
			}
		}

		@Override
		public void continuePlay() {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.seekTo(pauseProgress);
				mediaPlayer.start();
				musicInfo.setStatus(MusicInfo.STATUS_PLAYING);
			}
		}

		@Override
		public MusicInfo getMusicInfo() {
			if (mediaPlayer!= null) {
				if (mediaPlayer.isPlaying()) {
					musicInfo.setProgress(mediaPlayer.getCurrentPosition());
				}
				return musicInfo;
			}
			return null;
		}

		@Override
		public void seekTo(int progress) {
			mediaPlayer.seekTo(progress);
			mediaPlayer.start();
		}
		
	}
}
