package com.peerless2012.simplemusic;

public interface IMusic {

	public void play();
	public void pause();
	public void stop();
	public void continuePlay();
	public void seekTo(int progress);
	public MusicInfo getMusicInfo();
}
