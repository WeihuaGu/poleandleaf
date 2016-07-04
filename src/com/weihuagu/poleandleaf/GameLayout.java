/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weihuagu.poleandleaf;

import android.animation.TimeAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

//import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;

import com.weihuagu.poleandleaf.R;

@SuppressLint("NewApi")
public class GameLayout extends FrameLayout implements InterstitialAdAble{
	public Advertising ad=null;
	public static final String TAG = "poleandleaf";
    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    public static final boolean DEBUG_DRAW = false; // DEBUG
    public static final boolean AUTOSTART = true;
    public static final boolean HAVE_STARS = true;
    public static final float DEBUG_SPEED_MULTIPLIER = 1f; // 0.1f;
    public static final boolean DEBUG_IDDQD = false;
    final float hsv[] = {0, 0, 0};
    static final Rect sTmpRect = new Rect();
    final static int[] POPS = {
            // resid                // spinny!
            R.drawable.pop_belt,    0,
            R.drawable.pop_droid,   0,
            R.drawable.pop_pizza,   1,
            R.drawable.pop_stripes, 0,
            R.drawable.pop_swirl,   1,
            R.drawable.pop_vortex,  1,
            R.drawable.pop_vortex2, 1,
    };
    static class Params {
        public float TRANSLATION_PER_SEC;
        public int OBSTACLE_SPACING, OBSTACLE_PERIOD;
        public int OBSTACLE_WIDTH, OBSTACLE_STEM_WIDTH;
        public int OBSTACLE_GAP;
        public int OBSTACLE_MIN;
        public int BUILDING_HEIGHT_MIN;
        public int CLOUD_SIZE_MIN, CLOUD_SIZE_MAX;
        public int STAR_SIZE_MIN, STAR_SIZE_MAX;
        public int G;
        public int MAX_V;
        public float SCENERY_Z, OBSTACLE_Z, PLAYER_Z, PLAYER_Z_BOOST, HUD_Z;
        public Params(Resources res) {
            TRANSLATION_PER_SEC = res.getDimension(R.dimen.translation_per_sec);
            OBSTACLE_SPACING = res.getDimensionPixelSize(R.dimen.obstacle_spacing);
            OBSTACLE_PERIOD = (int) (OBSTACLE_SPACING / TRANSLATION_PER_SEC);
            OBSTACLE_WIDTH = res.getDimensionPixelSize(R.dimen.obstacle_width);
            OBSTACLE_STEM_WIDTH = res.getDimensionPixelSize(R.dimen.obstacle_stem_width);
            OBSTACLE_GAP = res.getDimensionPixelSize(R.dimen.obstacle_gap);
            OBSTACLE_MIN = res.getDimensionPixelSize(R.dimen.obstacle_height_min);
            BUILDING_HEIGHT_MIN = res.getDimensionPixelSize(R.dimen.building_height_min);
            CLOUD_SIZE_MIN = res.getDimensionPixelSize(R.dimen.cloud_size_min);
            CLOUD_SIZE_MAX = res.getDimensionPixelSize(R.dimen.cloud_size_max);
            STAR_SIZE_MIN = res.getDimensionPixelSize(R.dimen.star_size_min);
            STAR_SIZE_MAX = res.getDimensionPixelSize(R.dimen.star_size_max);
            G = res.getDimensionPixelSize(R.dimen.G);
            MAX_V = res.getDimensionPixelSize(R.dimen.max_v);
            SCENERY_Z = res.getDimensionPixelSize(R.dimen.scenery_z);
            OBSTACLE_Z = res.getDimensionPixelSize(R.dimen.obstacle_z);
            PLAYER_Z = res.getDimensionPixelSize(R.dimen.player_z);
            PLAYER_Z_BOOST = res.getDimensionPixelSize(R.dimen.player_z_boost);
            HUD_Z = res.getDimensionPixelSize(R.dimen.hud_z);
        }
    }

    private TimeAnimator mAnim;
    private TextView mScoreField;
    private View mSplash;
    private Player mPlayer;
    private ArrayList<Obstacle> mObstaclesInPlay = new ArrayList<Obstacle>();
    private float t, dt;
    private int mScore;
    private float mLastPipeTime; // in sec
    private int mWidth, mHeight;
    private boolean mAnimating, mPlaying;
    private boolean mFrozen; // after death, a short backoff
    private boolean mFlipped;
    private int mTimeOfDay;
    private static final int DAY = 0, NIGHT = 1, TWILIGHT = 2, SUNSET = 3;
    private static final int[][] SKIES = {
            { 0xFFc0c0FF, 0xFFa0a0FF }, // DAY
            { 0xFF000010, 0xFF000000 }, // NIGHT
            { 0xFF000040, 0xFF000010 }, // TWILIGHT
            { 0xFFa08020, 0xFF204080 }, // SUNSET
    };
    public static Params PARAMS;
    public GameLayout(Context context) {
        this(context, null);
    }
    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public GameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        PARAMS = new Params(getResources());
        mTimeOfDay = Util.irand(0, SKIES.length);
        this.ad=new Advertising(context,this);
    }

    @Override
    public boolean willNotDraw() {
        return !DEBUG;
    }

    public int getGameWidth() { return mWidth; }
    public int getGameHeight() { return mHeight; }
    public float getGameTime() { return t; }
    public float getLastTimeStep() { return dt; }

    public void setScoreField(TextView tv) {
        mScoreField = tv;
        if (tv != null) {
            //tv.setTranslationZ(PARAMS.HUD_Z);
            if (!(mAnimating && mPlaying)) {
                tv.setTranslationY(-500);
            }
        }
    }

    public void setSplash(View v) {
        mSplash = v;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        stop();
        reset();
        if (AUTOSTART) {
            start(false);
        }
    }

    

	@SuppressLint("NewApi")
	private void reset() {
        L("reset");
    
        /**
        final Drawable sky = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                SKIES[mTimeOfDay]
        );
        sky.setDither(true);
        setBackground(sky);
        **/

    //    mFlipped = Util.frand() > 0.5f;
   //     setScaleX(mFlipped ? -1 : 1);
        setScore(0);

        int i = getChildCount();
        while (i-->0) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                removeViewAt(i);
            }
        }
        mObstaclesInPlay.clear();

       
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        List<Building> groupsbuildings = new BuildingFactory().getGroupBuildings(getContext());
        for (i=0; i<groupsbuildings.size(); i++) {
            final float r1 = Util.frand();
            Building building=groupsbuildings.get(i);
            building.setHeight(getHeight()/6);
            final LayoutParams lp = new LayoutParams(building.w, building.h);
            if (building instanceof Building) {
                lp.gravity = Gravity.BOTTOM;
            } else {
                lp.gravity = Gravity.TOP;
                final float r = Util.frand();
                    lp.topMargin = (int) (1 - r*r * this.mHeight/2) + this.mWidth/2;
            }
            addView(building, lp);
            building.setTranslationX(Util.frand(-lp.width, this.mWidth+ lp.width));
        }


        mPlayer = new Player(getContext());
        mPlayer.setX(this.mWidth/ 2);
        mPlayer.setY(this.mHeight/ 2);
        addView(mPlayer, new LayoutParams(mPlayer.getPLAYER_SIZE(), mPlayer.getPLAYER_SIZE()));
        
        mAnim = new TimeAnimator();
        mAnim.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator timeAnimator, long t, long dt) {
                step(t, dt);
            }
        });
        
    }

    private void setScore(int score) {
        mScore = score;
        if (mScoreField != null) mScoreField.setText(String.valueOf(score));
    }

    private void addScore(int incr) {
        setScore(mScore + incr);
    }

    @SuppressLint("NewApi")
	private void start(boolean startPlaying) {
        L("start(startPlaying=%s)", startPlaying?"true":"false");
        if (startPlaying) {
            mPlaying = true;

            t = 0;
            // there's a sucker born every OBSTACLE_PERIOD
            mLastPipeTime = getGameTime() - PARAMS.OBSTACLE_PERIOD;

            if (mSplash != null && mSplash.getAlpha() > 0f) {
                //mSplash.setTranslationZ(PARAMS.HUD_Z);
//                mSplash.animate().alpha(0).translationZ(0).setDuration(400);

                mScoreField.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(1500);
            }

            mScoreField.setTextColor(0xFFAAAAAA);
            mScoreField.setBackgroundResource(R.drawable.scorecard);
            mPlayer.setVisibility(View.VISIBLE);
            mPlayer.setX(mWidth / 2);
            mPlayer.setY(mHeight / 2);
        } else {
            mPlayer.setVisibility(View.GONE);
        }
        if (!mAnimating) {
            mAnim.start();
            mAnimating = true;
        }
    }

    private void stop() {
        if (mAnimating) {
            mAnim.cancel();
            mAnim = null;
            mAnimating = false;
            mScoreField.setTextColor(0xFFFFFFFF);
            mScoreField.setBackgroundResource(R.drawable.scorecard_gameover);
            mTimeOfDay = Util.irand(0, SKIES.length); // for next reset
            mFrozen = true;
            postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFrozen = false;
                    }
                }, 250);
        }
        this.ad.showInterstitial();
        L("game stop");
    }

   

    private void step(long t_ms, long dt_ms) {
    	
        t = t_ms / 1000f; // seconds
        dt = dt_ms / 1000f;

        if (DEBUG) {
            t *= DEBUG_SPEED_MULTIPLIER;
            dt *= DEBUG_SPEED_MULTIPLIER;
        }

        // 1. Move all objects and update bounds
        final int N = getChildCount();
        int i = 0;
        for (; i<N; i++) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                ((GameView) v).step(t_ms, dt_ms, t, dt);
            }
        }

        // 2. Check for altitude
        if (mPlaying && mPlayer.below(mHeight)) {
            if (DEBUG_IDDQD) {
                poke();
            } else {
                L("player hit the floor");
                stop();
            }
        }

        // 3. Check for obstacles
        boolean passedBarrier = false;
        for (int j = mObstaclesInPlay.size(); j-->0;) {
            final Obstacle ob = mObstaclesInPlay.get(j);
            if (mPlaying && ob.intersects(mPlayer) && !DEBUG_IDDQD) {
                L("player hit an obstacle");
                stop();
            } else if (ob.cleared(mPlayer)) {
                if (ob instanceof Stem) passedBarrier = true;
                
                
                mObstaclesInPlay.remove(j);
            }
        }

        if (mPlaying && passedBarrier) {
            addScore(1);
        }

        // 4. Handle edge of screen
        // Walk backwards to make sure removal is safe
        while (i-->0) {
            final View v = getChildAt(i);
            if (v instanceof Obstacle) {
                if (v.getTranslationX() + v.getWidth() < 0) {
                    removeViewAt(i);
                }
            } else if (v instanceof Scenery) {
                final Scenery s = (Scenery) v;
                if (v.getTranslationX() + s.w < 0) {
                    v.setTranslationX(getWidth());
                }
            }
        }

        // 3. Time for more obstacles!
        if (mPlaying && (t - mLastPipeTime) > PARAMS.OBSTACLE_PERIOD) {
            mLastPipeTime = t;
            final int obstacley = (int) (Math.random()
                    * (mHeight - 2*PARAMS.OBSTACLE_MIN - PARAMS.OBSTACLE_GAP)) + PARAMS.OBSTACLE_MIN;

            final int inset = (PARAMS.OBSTACLE_WIDTH - PARAMS.OBSTACLE_STEM_WIDTH) / 2;
            final int yinset = PARAMS.OBSTACLE_WIDTH/2;

            final int d1 = Util.irand(0,250);
            final Obstacle s1 = new Stem(getContext(), obstacley - yinset+mHeight/6, true);
            addView(s1, new LayoutParams(
                    PARAMS.OBSTACLE_STEM_WIDTH,
                    (int) s1.h,
                    Gravity.TOP|Gravity.LEFT));
            s1.setTranslationX(mWidth+inset);
            s1.setTranslationY(-s1.h-yinset);
            //s1.setTranslationZ(PARAMS.OBSTACLE_Z*0.75f);
            s1.animate()
                    .translationY(0)
                    .setStartDelay(d1)
                    .setDuration(250);
            mObstaclesInPlay.add(s1);

            final int d2 = Util.irand(0,250);
            final Obstacle s2 = new Stem(getContext(),
                    mHeight - obstacley - PARAMS.OBSTACLE_GAP - yinset,
                    true);
            addView(s2, new LayoutParams(
                    PARAMS.OBSTACLE_STEM_WIDTH,
                    (int) s2.h,
                    Gravity.TOP|Gravity.LEFT));
            s2.setTranslationX(mWidth+inset);
            s2.setTranslationY(mHeight+yinset);
            //s2.setTranslationZ(PARAMS.OBSTACLE_Z*0.75f);
            s2.animate()
                    .translationY(mHeight-s2.h)
                    .setStartDelay(d2)
                    .setDuration(400);
            mObstaclesInPlay.add(s2);
            
            final int l1 = Util.irand(0,250);
            Leaves mleave=new Leaves(getContext());
            mleave.setX(this.mWidth);
            mleave.setY(this.mHeight*0.2f);
            addView(mleave, new LayoutParams(mleave.getLEAVE_SIZE(),mleave.getLEAVE_HIT_SIZE()));
            mleave.setTranslationX(mWidth+inset);
            mleave.animate()
            .translationY(mHeight-s2.h)
            .setStartDelay(d2)
            .setDuration(700);
            mObstaclesInPlay.add(mleave);
            
            
            final int l2 = Util.irand(0,250);
            Leaves mleave2=new Leaves(getContext());
            mleave2.setX(this.mWidth);
            mleave2.setY(this.mHeight*0.2f);
            addView(mleave2, new LayoutParams(mleave2.getLEAVE_SIZE(),mleave2.getLEAVE_HIT_SIZE()));
            mleave2.animate()
            .translationY(mHeight-s2.h-mHeight/6)
            .setStartDelay(d2)
            .setDuration(600);
            mObstaclesInPlay.add(mleave2);

        }

        if (DEBUG_DRAW) invalidate();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (DEBUG) L("touch: %s", ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                poke();
                return true;
            case MotionEvent.ACTION_UP:
                unpoke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (DEBUG) L("trackball: %s", ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                poke();
                return true;
            case MotionEvent.ACTION_UP:
                unpoke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ev) {
        if (DEBUG) L("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                poke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent ev) {
        if (DEBUG) L("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                unpoke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent (MotionEvent ev) {
        if (DEBUG) L("generic: %s", ev);
        return false;
    }

    private void poke() {
        L("poke");
        if (mFrozen) return;
        if (!mAnimating) {
            reset();
            start(true);
        } else if (!mPlaying) {
            start(true);
        }
        mPlayer.boost();
        if (DEBUG) {
            mPlayer.dv *= DEBUG_SPEED_MULTIPLIER;
            mPlayer.animate().setDuration((long) (200/DEBUG_SPEED_MULTIPLIER));
        }
    }

    private void unpoke() {
        L("unboost");
        if (mFrozen) return;
        if (!mAnimating) return;
        mPlayer.unboost();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        if (!DEBUG_DRAW) return;

        final Paint pt = new Paint();
        pt.setColor(0xFFFFFFFF);
        final int L = mPlayer.corners.length;
        final int N = L/2;
        for (int i=0; i<N; i++) {
            final int x = (int) mPlayer.corners[i*2];
            final int y = (int) mPlayer.corners[i*2+1];
            c.drawCircle(x, y, 4, pt);
            c.drawLine(x, y,
                    mPlayer.corners[(i*2+2)%L],
                    mPlayer.corners[(i*2+3)%L],
                    pt);
        }

        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(getResources().getDisplayMetrics().density);

        final int M = getChildCount();
        pt.setColor(0x8000FF00);
        for (int i=0; i<M; i++) {
            final View v = getChildAt(i);
            if (v == mPlayer) continue;
            if (!(v instanceof GameView)) continue;
            if (v instanceof Pop) {
                final Pop p = (Pop) v;
                c.drawCircle(p.cx, p.cy, p.r, pt);
            } else {
                final Rect r = new Rect();
                v.getHitRect(r);
                c.drawRect(r, pt);
            }
        }

        pt.setColor(Color.BLACK);
        final StringBuilder sb = new StringBuilder("obstacles: ");
        for (Obstacle ob : mObstaclesInPlay) {
            sb.append(ob.hitRect.toShortString());
            sb.append(" ");
        }
        pt.setTextSize(20f);
        c.drawText(sb.toString(), 20, 100, pt);
    }


    
    //static method
    
    public static final void L(String s, Object ... objects) {
       // if (DEBUG) {
            Log.d(TAG, String.format(s, objects));
      //  }
    }
	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		reset();
		
	}

    
   
}
