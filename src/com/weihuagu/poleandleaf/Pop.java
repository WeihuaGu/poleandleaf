package com.weihuagu.poleandleaf;

import com.weihuagu.poleandleaf.Player;

import android.content.Context;

public class Pop extends Obstacle {
	int mRotate;
    int cx, cy, r;
    public Pop(Context context, float h) {
        super(context, h);
        int idx = 2*Util.irand(0, GameLayout.POPS.length/2);
        setBackgroundResource(GameLayout.POPS[idx]);
        setScaleX(Util.frand() < 0.5f ? -1 : 1);
        mRotate = GameLayout.POPS[idx+1] == 0 ? 0 : (Util.frand() < 0.5f ? -1 : 1);
    }

    public boolean intersects(Player p) {
        final int N = p.corners.length/2;
        for (int i=0; i<N; i++) {
            final int x = (int) p.corners[i*2];
            final int y = (int) p.corners[i*2+1];
            if (Math.hypot(x-cx, y-cy) <= r) return true;
        }
        return false;
    }

    @Override
    public void step(long t_ms, long dt_ms, float t, float dt) {
        super.step(t_ms, dt_ms, t, dt);
        if (mRotate != 0) {
            setRotation(getRotation() + dt * 45 * mRotate);
        }

        cx = (hitRect.left + hitRect.right)/2;
        cy = (hitRect.top + hitRect.bottom)/2;
        r = getWidth()/2;
    }

}
