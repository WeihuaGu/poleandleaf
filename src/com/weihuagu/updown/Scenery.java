package com.weihuagu.updown;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

public class Scenery extends FrameLayout implements GameView {

	 public float z;
     public float v;
     public int h, w; 

     public Scenery(Context context) {
         super(context);
     }
     public void setBackColor(){
   	  
     }
     @Override
     public void step(long t_ms, long dt_ms, float t, float dt) {
         setTranslationX(getTranslationX() - LLand.PARAMS.TRANSLATION_PER_SEC * dt * v);
     }

}
