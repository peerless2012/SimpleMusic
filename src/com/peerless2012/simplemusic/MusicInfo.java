package com.peerless2012.simplemusic;

public class MusicInfo {

	public static final int STATUS_IDLE = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSE = 2;
	public static final int STATUS_STOP = 3;
	
	private int status = STATUS_IDLE;
	
	private long changeId;
	
	private int progress;
	
	private int duration;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getChangeId() {
		return changeId;
	}

	public void setChangeId(long changeId) {
		this.changeId = changeId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isUseful() {
		return this.status != STATUS_IDLE && this.status != STATUS_STOP;
	}
	
	@Override
	public String toString() {
		return "MusicInfo [progress=" + progress + ", duration=" + duration
				+ "]";
	}
}
