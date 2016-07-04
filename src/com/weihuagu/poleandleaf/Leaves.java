package com.weihuagu.poleandleaf;

import com.techjun.lland.R;

import android.content.Context;

public class Leaves extends Obstacle {
	private int LEAVE_SIZE;
    private int LEAVE_HIT_SIZE;
    int cx, cy, r;
	public Leaves(Context context, float h) {
		super(context, h);
		// TODO Auto-generated constructor stub
	}
	public Leaves(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		  this.LEAVE_SIZE=this.getResources().getDimensionPixelSize(R.dimen.player_size);
	        this.LEAVE_HIT_SIZE = this.getResources().getDimensionPixelSize(R.dimen.player_hit_size);
		 setBackgroundResource(R.drawable.leave);

	}
	
	  public int getLEAVE_SIZE(){
	    	return this.LEAVE_SIZE;
	    	
	    }
	    public int getLEAVE_HIT_SIZE(){
	    	return this.LEAVE_HIT_SIZE;
	    }
	 public void step(long t_ms, long dt_ms, float t, float dt) {
         setTranslationX(getTranslationX()-TRANSLATION_PER_SEC*2*dt);
         getHitRect(hitRect);
         cx = (hitRect.left + hitRect.right)/2;
         cy = (hitRect.top + hitRect.bottom)/2;
         r = getWidth()/2;
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
	

}
