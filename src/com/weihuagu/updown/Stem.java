package com.weihuagu.updown;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;

public class Stem extends Obstacle {
	Paint mPaint = new Paint();
    Path mShadow = new Path();
    boolean mDrawShadow;

	public Stem(Context context, float h, boolean drawShadow) {
        super(context, h);
        mDrawShadow = drawShadow;
        mPaint.setColor(0xFFAAAAAA);
        setBackground(null);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setWillNotDraw(false);
    }
    @Override
    public void onDraw(Canvas c) {
        final int w = c.getWidth();
        final int h = c.getHeight();
        final GradientDrawable g = new GradientDrawable();
        g.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        g.setGradientCenter(w * 0.85f, 0);
        g.setColors(new int[] { 0xFFFFFFFF, 0xFFAAAAAA });
        g.setBounds(0, 0, w, h);
        g.draw(c);
        if (!mDrawShadow) return;
        mShadow.reset();
        mShadow.moveTo(0,0);
        mShadow.lineTo(w, 0);
        mShadow.lineTo(w, LLand.PARAMS.OBSTACLE_WIDTH/2+w*1.5f);
        mShadow.lineTo(0, LLand.PARAMS.OBSTACLE_WIDTH/2);
        mShadow.close();
        c.drawPath(mShadow, mPaint);
    }
    

    public void step(long t_ms, long dt_ms, float t, float dt) {
    	super.step(t_ms, dt_ms, t, dt);
        setTranslationY(getTranslationY()+50*dt);
       
    }

}
