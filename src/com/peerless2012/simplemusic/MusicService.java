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
	
	private MusicInfo musicInfo = null;

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
			//服务停止的时候回收资源
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
				//重置，减少new操作
				mediaPlayer.reset();
				mediaPlayer.setDataSource(resourceFd.getFileDescriptor());
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						//异步准备完成以后，开始播放
						mp.start();
						//每次返回的都是同一个对象，减少内存开销
						musicInfo = new MusicInfo();
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
				if (musicInfo != null) musicInfo.setStatus(MusicInfo.STATUS_PAUSE);
			}
		}

		@Override
		public void stop() {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				if (musicInfo != null) musicInfo.setStatus(MusicInfo.STATUS_STOP);
			}
		}

		@Override
		public void continuePlay() {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.seekTo(pauseProgress);
				mediaPlayer.start();
				if (musicInfo != null) musicInfo.setStatus(MusicInfo.STATUS_PLAYING);
			}
		}

		@Override
		public MusicInfo getMusicInfo() {
			if (mediaPlayer!= null) {
				if (mediaPlayer.isPlaying() && musicInfo != null) {
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
