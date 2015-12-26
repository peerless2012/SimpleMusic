package com.peerless2012.simplemusic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class MusicCoverView extends View {

	private RectF musicBitmapRectF = new RectF();
	
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	
	private Bitmap musicBitmap;
	
	public MusicCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setWillNotDraw(false);
//		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	public MusicCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
//		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	public MusicCoverView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
		int min = Math.min(width, height);
		if (min == width) {
			musicBitmapRectF.set(0, height / 2 - min / 2, width, height / 2 + min / 2);
		}else {
			musicBitmapRectF.set(width / 2 - min / 2, 0, width / 2 + min / 2, height);
		}
//		setMusicPic(R.drawable.love_in_morden_times);
	}
	
	public void setMusicPic(int picRes) {
		mPaint.reset();
		Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), picRes);
		int srcWidth = tempBitmap.getWidth();
		int srcHeight = tempBitmap.getHeight();
		int radius2 = Math.min(srcWidth, srcHeight);
		Rect srcRectF = new Rect();
		RectF destRectF = new RectF();
		if (radius2 == srcWidth) {
			srcRectF.set(0, srcHeight / 2 - radius2 / 2, srcWidth, srcHeight / 2 + radius2 / 2);
		}else {
			srcRectF.set(srcWidth / 2 - radius2 / 2, 0, srcWidth / 2 + radius2 / 2, srcHeight);
		}
		destRectF.set(0, 0, musicBitmapRectF.width(), musicBitmapRectF.height());
		musicBitmap = Bitmap.createBitmap((int)musicBitmapRectF.width(), (int)musicBitmapRectF.height(), Config.ARGB_8888);
		mPaint.setColor(Color.RED);
		Canvas canvas = new Canvas(musicBitmap);
		canvas.save();
		mPaint.setColor(Color.RED);
		canvas.drawCircle(destRectF.centerX(), destRectF.centerY(), destRectF.width() / 2, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawCircle(destRectF.centerX(), destRectF.centerY(), 20, mPaint);
		canvas.restore();
		mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(tempBitmap, srcRectF, destRectF, mPaint);
		mPaint.setXfermode(null);
		
		Bitmap bgBitmap = BitmapUtils.blur(tempBitmap);
		setBackgroundDrawable(new BitmapDrawable(bgBitmap));
		
		tempBitmap.recycle();
	}
	
	private float degree;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.reset();
		if (musicBitmap != null) {
			canvas.save();
			canvas.rotate(degree, musicBitmapRectF.centerX(), musicBitmapRectF.centerY());
			degree = (++ degree % 360);
			canvas.drawBitmap(musicBitmap, null,musicBitmapRectF,  mPaint);
			canvas.restore();
			if (isRuning) {
				postInvalidateDelayed(15);
			}
		}
	}
	
	private boolean isRuning = false;
	public void startRotate() {
		isRuning = true;
		invalidate();
	}
	
	public void stopRotate() {
		isRuning = false;
	}
	
	
}
