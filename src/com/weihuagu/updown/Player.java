package com.weihuagu.updown;

import com.techjun.lland.R;
import com.weihuagu.updown.GameView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;

public class Player extends ImageView implements GameView{
	public float dv;
    private int PLAYER_SIZE;
    private int PLAYER_HIT_SIZE;
    private boolean mBoosting;
     private int BOOST_DV;
    private final float[] sHull = new float[] {
            0.3f,  0f,    // left antenna
            0.7f,  0f,    // right antenna
            0.92f, 0.33f, // off the right shoulder of Orion
            0.92f, 0.75f, // right hand (our right, not his right)
            0.6f,  1f,    // right foot
            0.4f,  1f,    // left foot BLUE!
            0.08f, 0.75f, // sinistram
            0.08f, 0.33f,  // cold shoulder
    };
    public final float[] corners = new float[sHull.length];

	
	public Player(Context context) {
        super(context);
        this.PLAYER_SIZE=this.getResources().getDimensionPixelSize(R.dimen.player_size);
        this.PLAYER_HIT_SIZE = this.getResources().getDimensionPixelSize(R.dimen.player_hit_size);
        this.BOOST_DV=this.getResources().getDimensionPixelSize(R.dimen.boost_dv);
        setBackgroundResource(R.drawable.android);
    }

    public void prepareCheckIntersections() {
        final int inset = (PLAYER_SIZE - PLAYER_HIT_SIZE)/2;
        final int scale = PLAYER_HIT_SIZE;
        final int N = sHull.length/2;
        for (int i=0; i<N; i++) {
            corners[i*2]   = scale * sHull[i*2]   + inset;
            corners[i*2+1] = scale * sHull[i*2+1] + inset;
        }
        final Matrix m = getMatrix();
        m.mapPoints(corners);
    }

    public boolean below(int h) {
        final int N = corners.length/2;
        for (int i=0; i<N; i++) {
            final int y = (int) corners[i*2+1];
            if (y >= h) return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
	public void step(long t_ms, long dt_ms, float t, float dt) {
        if (getVisibility() != View.VISIBLE) return; // not playing yet

        if (mBoosting) {
            dv = -BOOST_DV;
        } else {
            dv += LLand.PARAMS.G;
        }
        if (dv < -LLand.PARAMS.MAX_V) dv = -LLand.PARAMS.MAX_V;
        else if (dv > LLand.PARAMS.MAX_V) dv = LLand.PARAMS.MAX_V;

        final float y = getTranslationY() + dv * dt+5;
        setTranslationY(y < 0 ? 0 : y);
        setRotation(
                90 + Util.lerp(Util.clamp(Util.rlerp(dv, LLand.PARAMS.MAX_V, -1 * LLand.PARAMS.MAX_V)), 90, -90));

        prepareCheckIntersections();
    }

    @SuppressLint("NewApi")
	public void boost() {
        mBoosting = true;
        dv = -BOOST_DV;

        animate().cancel();
        animate()
                .scaleX(1.25f)
                .scaleY(1.25f)
//                .translationZ(LLand.PARAMS.PLAYER_Z_BOOST)
                .setDuration(100);
        setScaleX(1.25f);
        setScaleY(1.25f);
    }

    public void unboost() {
        mBoosting = false;

        animate().cancel();
        animate()
                .scaleX(1f)
                .scaleY(1f)
//                .translationZ(LLand.PARAMS.PLAYER_Z)
                .setDuration(200);
    }
    public int getPLAYER_SIZE(){
    	return this.PLAYER_SIZE;
    	
    }
    public int getPLAYER_HIT_SIZE(){
    	return this.PLAYER_HIT_SIZE;
    }

}
