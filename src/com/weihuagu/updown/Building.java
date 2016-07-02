package com.weihuagu.updown;

import com.techjun.lland.R;

import android.content.Context;
import android.graphics.Color;

public class Building extends Scenery {
	    private int BUILDING_WIDTH_MIN;
	    private int BUILDING_WIDTH_MAX;
	    private int BUILDING_HEIGHT_MIN;
	    final float hsv[] = {0, 0, 0};
	    public Building(Context context) {
           super(context);
           this.BUILDING_WIDTH_MIN=this.getResources().getDimensionPixelSize(R.dimen.building_width_min);
           this.BUILDING_WIDTH_MAX=this.getResources().getDimensionPixelSize(R.dimen.building_width_max);
           this.BUILDING_HEIGHT_MIN=this.getResources().getDimensionPixelSize(R.dimen.building_height_min);
           this.h = Util.irand(BUILDING_HEIGHT_MIN, getHeight()/6);
           this.w = Util.irand(BUILDING_WIDTH_MIN, BUILDING_WIDTH_MAX);
       }
	   public void setBackColor(){
	    	 hsv[0] = 175;
	         hsv[1] = 0.25f;
	         hsv[2] = 1 * z;
	         this.setBackgroundColor(Color.HSVToColor(hsv)); 
	     }
}
