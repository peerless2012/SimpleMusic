package com.peerless2012.simplemusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

@SuppressLint("NewApi")
public class MusicCoverView extends View {
	public final static int STATUS_IDELL = 0;
	public final static int STATUS_ANIMTING_START = 1;
	public final static int STATUS_ANIMTING_STOP = 2;
	public final static int STATUS_PLAYING = 3;

	private final int DEGREE_TOP = 50;
	private final int DEGREE_BOTTOM = 00;
	
	private final int INCREASE_STEP = 5;
	
	private int pointStatus = STATUS_IDELL;
	private RectF musicRectF = new RectF();
	private RectF musicPointRectF = new RectF();

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private int musicEdge = 0;

	// 背景
	private Bitmap musicBitmap;
	// 唱针
	private Bitmap musicPointBitmap;

	private MusicAnimListener musicAnimListener;

	public MusicCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public MusicCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public MusicCoverView(Context context) {
		super(context);
	}

	private void init(Context context, AttributeSet attrs) {
		setWillNotDraw(false);
		musicEdge = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
				context.getResources().getDisplayMetrics());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
		int min = Math.min(width, height);
		if (min == width) {
			musicRectF.set(getPaddingLeft() + musicEdge, height / 2 - min / 2, width - getPaddingRight() - musicEdge,
					height / 2 + min / 2);
		} else {
			musicRectF.set(width / 2 - min / 2, getPaddingTop() + musicEdge, width / 2 + min / 2,
					height - getPaddingBottom() - musicEdge);
		}
		// 167 * 227
		float pointHeight = min / 3;
		float pointWidth = (pointHeight * 167) / 227f;
		musicPointRectF.set(musicRectF.centerX() / 2 - pointWidth / 2, musicRectF.top,
				musicRectF.centerX() / 2 + pointWidth / 2, musicRectF.top + pointHeight);
		setMusicPic(R.drawable.love_in_morden_times);
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
		} else {
			srcRectF.set(srcWidth / 2 - radius2 / 2, 0, srcWidth / 2 + radius2 / 2, srcHeight);
		}
		destRectF.set(0, 0, musicRectF.width(), musicRectF.height());
		musicBitmap = Bitmap.createBitmap((int) musicRectF.width(), (int) musicRectF.height(), Config.ARGB_8888);
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
	private float pointDegree = DEGREE_TOP;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.reset();
		if (musicBitmap != null) {
			canvas.save();
			canvas.rotate(degree, musicRectF.centerX(), musicRectF.centerY());
			if (pointStatus == STATUS_PLAYING) {
				degree = (++degree % 360);
			}
			canvas.drawBitmap(musicBitmap, null, musicRectF, mPaint);
			canvas.restore();

			// 绘制唱片指针
			if (musicPointBitmap == null) {
				Bitmap tempMusicPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.music_point);
				musicPointBitmap = Bitmap.createBitmap((int) musicPointRectF.width(), (int) musicPointRectF.height(),
						Config.ARGB_8888);
				Canvas pointCanvas = new Canvas(musicPointBitmap);
				Rect pRect = new Rect(0, 0, tempMusicPointBitmap.getWidth(), tempMusicPointBitmap.getHeight());
				RectF cRectF = new RectF(0, 0, musicPointRectF.width(), musicPointRectF.height());
				pointCanvas.drawBitmap(tempMusicPointBitmap, pRect, cRectF, mPaint);
			}
			// 根据状态绘制唱针
			canvas.save();
			if (pointStatus == STATUS_IDELL) {
				canvas.rotate(DEGREE_TOP, musicPointRectF.left, musicPointRectF.top);
			} else if (pointStatus == STATUS_ANIMTING_START) {
				if (pointDegree <= DEGREE_BOTTOM) {
					pointDegree = DEGREE_BOTTOM;
					pointStatus = STATUS_PLAYING;
					if (musicAnimListener != null) {
						musicAnimListener.onStartAnimFinish();
					}
					isAnimting = false;
				}else {
					pointDegree -=  INCREASE_STEP;
				}
				canvas.rotate(pointDegree, musicPointRectF.left, musicPointRectF.top);
			} else if (pointStatus == STATUS_ANIMTING_STOP) {
				if (pointDegree >=  DEGREE_TOP) {
					pointDegree = DEGREE_TOP;
					pointStatus = STATUS_IDELL;
					isRuning = false;
					isAnimting = false;
				}else {
					pointDegree += INCREASE_STEP;
				}
				canvas.rotate(pointDegree, musicPointRectF.left, musicPointRectF.top);
			} else {
				canvas.rotate(DEGREE_BOTTOM, musicPointRectF.left, musicPointRectF.top);
			}
			canvas.drawBitmap(musicPointBitmap, null, musicPointRectF, mPaint);
			canvas.restore();

			if (isRuning) {
				postInvalidateDelayed(15);
			}
		}
	}

	public void setStatus(int status) {
		if (status == MusicInfo.STATUS_PLAYING) {
			pointStatus = STATUS_PLAYING;
			pointDegree = DEGREE_TOP;
			isRuning = true;
		}else if (status == MusicInfo.STATUS_PAUSE) {
			pointStatus = STATUS_PLAYING;
			pointDegree = DEGREE_BOTTOM;
			isRuning = false;
		}else {
			pointStatus = STATUS_PLAYING;
			isRuning = true;
		}
		invalidate();
	}
	
	public boolean isAnimating() {
		return isAnimting;
	}
	
	public boolean isRotating() {
		return isRuning;
	}
	
	private boolean isRuning = false;
	
	public void startRotate() {
		isRuning = true;
		isAnimting = true;
		pointStatus = STATUS_ANIMTING_START;
		invalidate();
	}

	public void stopRotate() {
		isRuning = true;
		isAnimting = true;
		pointStatus = STATUS_ANIMTING_STOP;
		invalidate();
	}

	public void pause() {
		pointStatus = STATUS_PLAYING;
		pointDegree = DEGREE_BOTTOM;
		isRuning = false;
		invalidate();
	}

	public void continueRotate() {
		pointStatus = STATUS_PLAYING;
		pointDegree = DEGREE_BOTTOM;
		isRuning = true;
		invalidate();
	}

	private boolean isAnimting = false;

	public void setOnStartAnimListener(MusicAnimListener l) {
		musicAnimListener = l;
	}

	public interface MusicAnimListener {
		public void onStartAnimFinish();
	}

}