package com.peerless2012.simplemusic;

import com.peerless2012.simplemusic.MusicCoverView.MusicAnimListener;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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
	
	private MusicCoverView musicCoverView;
	
	private class UpdateHandle extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_MUSIC_INFO://更新进度以及音乐信息
				MusicInfo newInfo = (MusicInfo) msg.obj;
				if (newInfo != null && newInfo.isUseful()) {//有音乐数据
					if (preMusicInfo == null ||(preMusicInfo != null &&preMusicInfo.getChangeId() != newInfo.getChangeId())) {//如果是一个新的音乐，则更新SeekBar的max和总时间
						musicSeekBar.setMax(newInfo.getDuration());
						musicDuration.setText(DateUtils.changSecondsToTime(newInfo.getDuration() / 1000));
						preMusicInfo = newInfo;
					}
					//更新进度和时间
					currentTime.setText(DateUtils.changSecondsToTime(newInfo.getProgress() / 1000));
					musicSeekBar.setProgress(newInfo.getProgress());
				}
				if (newInfo != null && newInfo.getStatus() == MusicInfo.STATUS_STOP && musicCoverView.isRotating()) {
					musicCoverView.stopRotate();
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
				//绑定服务后开启子线程轮询查询当前播放的状态等信息
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
		
		musicCoverView.setOnStartAnimListener(new MusicAnimListener() {
			
			@Override
			public void onStartAnimFinish() {
				iMusic.play();
//				Log.i("MainActivity", "动画执行完成");
			}
		});
	}

	private void initView() {
		musicSeekBar = getView(R.id.music_seek_bar);
		currentTime = getView(R.id.current_time);
		musicDuration = getView(R.id.music_duration);
		musicCoverView = getView(R.id.music_cover);
	}
	
	@Override
	protected void onDestroy() {
		//界面销毁时要关闭子线程，防止内存泄漏
		if (progressThread != null) {
			progressThread.stopProgressThread();
		}
		
		//界面销毁的时候清除掉次Handle发送的所有message和callback，防止内存泄漏
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
				musicCoverView.setMusicPic(R.drawable.love_in_morden_times);
				musicCoverView.startRotate();
				break;
			case R.id.pasue:
				musicCoverView.pause();
				iMusic.pause();
				break;
			case R.id.continuePlay:
				musicCoverView.continueRotate();
				iMusic.continuePlay();
				break;
			case R.id.stop:
				musicCoverView.stopRotate();;
				iMusic.stop();
				break;
	
			default:
				break;
			}
	}
	
	
	
	class ProgressThread extends Thread{
		
		private boolean isRunning = true;
		
		/**
		 * 关闭死循环的子线程
		 * <h2>子线程是用来获取当前的进度，更新界面的，界面关闭后，这个线程也没必要存在了，当界面重新打开的时候从新开始线程即可</h2>
		 */
		public void stopProgressThread() {
			isRunning = false;
		}
		
		@Override
		public void run() {
			super.run();
			while (isRunning) {
				if (iMusic != null) {
					//获取当前信息
					MusicInfo musicInfo = iMusic.getMusicInfo();
					Message msg = updateHandle.obtainMessage();
					msg.what = UPDATE_MUSIC_INFO;
					msg.obj = musicInfo;
					//发送到主线程更新界面
					updateHandle.sendMessage(msg);
				}
				SystemClock.sleep(1000);
			}
		}
	}
}