package com.weihuagu.updown;

import com.techjun.lland.R;

import android.content.Context;

public class Building extends Scenery {
	   private int BUILDING_WIDTH_MIN;
	    private int BUILDING_WIDTH_MAX;
	   public Building(Context context) {
           super(context);
           this.BUILDING_WIDTH_MIN=this.getResources().getDimensionPixelSize(R.dimen.building_width_min);
           this.BUILDING_WIDTH_MAX=this.getResources().getDimensionPixelSize(R.dimen.building_width_max);
           w = Util.irand(BUILDING_WIDTH_MIN, BUILDING_WIDTH_MAX);
           h = 0; // will be setup later, along with z

           //setTranslationZ(PARAMS.SCENERY_Z);
       }
}
