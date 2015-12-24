package com.peerless2012.simplemusic;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener{

	private final static int UPDATE_MUSIC_INFO = 1;
	
	private SeekBar musicSeekBar;
	
	private TextView currentTime,musicDuration;
	
	private ProgressThread progressThread;
	
	private ServiceConnection musicConnection;
	
	private Intent musicIntent;
	
	private IMusic iMusic;
	
	private MusicInfo preMusicInfo;
	private class UpdateHandle extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_MUSIC_INFO:
				MusicInfo newInfo = (MusicInfo) msg.obj;
				if (newInfo != null && newInfo.isUseful()) {
					if (preMusicInfo == null ||(preMusicInfo != null &&preMusicInfo.getChangeId() != newInfo.getChangeId())) {
						musicSeekBar.setMax(newInfo.getDuration());
						musicDuration.setText(DateUtils.changSecondsToTime(newInfo.getDuration() / 1000));
						preMusicInfo = newInfo;
					}
					currentTime.setText(DateUtils.changSecondsToTime(newInfo.getProgress() / 1000));
					musicSeekBar.setProgress(newInfo.getProgress());
				}
				break;

			default:
				break;
			}
		}
	}
	
	private UpdateHandle updateHandle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListener();
		initData();
	}

	private void initData() {
		musicIntent = new Intent(this, MusicService.class);
		startService(musicIntent);
		musicConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				iMusic = (IMusic) service;
				progressThread = new ProgressThread();
				progressThread.start();
			}
		};
		bindService(musicIntent, musicConnection, Service.BIND_AUTO_CREATE);
		updateHandle = new UpdateHandle();
	}

	private void initListener() {
		findViewById(R.id.play).setOnClickListener(this);
		findViewById(R.id.pasue).setOnClickListener(this);
		findViewById(R.id.continuePlay).setOnClickListener(this);
		findViewById(R.id.stop).setOnClickListener(this);
		musicSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (iMusic != null) {
					iMusic.seekTo(seekBar.getProgress());
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
	}

	private void initView() {
		musicSeekBar = getView(R.id.music_seek_bar);
		currentTime = getView(R.id.current_time);
		musicDuration = getView(R.id.music_duration);
	}
	
	@Override
	protected void onDestroy() {
		if (progressThread != null) {
			progressThread.stopProgressThread();
		}
		if (updateHandle != null) {
			updateHandle.removeCallbacksAndMessages(null);
		}
		unbindService(musicConnection);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		
		if (iMusic == null) return;
		
		switch (v.getId()) {
			case R.id.play:
				iMusic.play();
				break;
			case R.id.pasue:
				iMusic.pause();
				break;
			case R.id.continuePlay:
				iMusic.continuePlay();
				break;
			case R.id.stop:
				iMusic.stop();
				break;
	
			default:
				break;
			}
	}
	
	
	
	class ProgressThread extends Thread{
		
		private boolean isRunning = true;
		
		public void stopProgressThread() {
			isRunning = false;
		}
		
		@Override
		public void run() {
			super.run();
			while (isRunning) {
				if (iMusic != null) {
					MusicInfo musicInfo = iMusic.getMusicInfo();
					Message msg = updateHandle.obtainMessage();
					msg.what = UPDATE_MUSIC_INFO;
					msg.obj = musicInfo;
					updateHandle.sendMessage(msg);
				}
				SystemClock.sleep(1000);
			}
		}
	}
}
