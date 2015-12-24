package com.peerless2012.simplemusic;

import android.app.Activity;
import android.view.View;

public class BaseActivity extends Activity{
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T getView(Activity activity,int viewResId) {
		return (T)activity.findViewById(viewResId);
	}
	protected <T extends View> T getView(int viewResId) {
		return getView(this,viewResId);
	}
	@SuppressWarnings("unchecked")
	protected <T extends View> T getView(View parent,int viewResId) {
		return (T)parent.findViewById(viewResId);
	}

}
