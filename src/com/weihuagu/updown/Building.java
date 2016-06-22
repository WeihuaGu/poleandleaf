package com.weihuagu.updown;

import android.content.Context;

public class Building extends Scenery {
	   public Building(Context context) {
           super(context);

           w = Util.irand(LLand.PARAMS.BUILDING_WIDTH_MIN, LLand.PARAMS.BUILDING_WIDTH_MAX);
           h = 0; // will be setup later, along with z

           //setTranslationZ(PARAMS.SCENERY_Z);
       }
}
