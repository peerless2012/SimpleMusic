package com.peerless2012.simplemusic;

import com.peerless2012.simplemusic.utils.FastBlur;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
//www.eoeandroid.com/thread-330350-1-1.html?_dsign=4dbaeb89
public class BitmapUtils {

    public static Bitmap blur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 1;
        float radius = 20;
//        if (downScale.isChecked()) {
            scaleFactor = 8;
            radius = 2;
//        }

        Bitmap overlay = Bitmap.createBitmap((int) (bkg.getWidth()/scaleFactor),
                (int) (bkg.getHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-bkg.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int)radius, true);
        Log.i("BitmapUtils", "耗时 " + (System.currentTimeMillis() - startMs));
        return overlay;
    }
}
