package com.weihuagu.poleandleaf;

import com.weihuagu.poleandleaf.R;

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
           this.w = Util.irand(BUILDING_WIDTH_MIN, BUILDING_WIDTH_MAX);
       }
	   public void setHeight(int height){
		   this.h=Util.irand(BUILDING_HEIGHT_MIN, height);
	   }
	   public void setBackColor(){
	    	 hsv[0] = 175;
	         hsv[1] = 0.25f;
	         hsv[2] = 1 * z;
	         this.setBackgroundColor(Color.HSVToColor(hsv)); 
	     }
}
