package com.weihuagu.updown;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;



public class BuildingFactory {
	private  List<Building> groupsbuildings = new ArrayList<Building>();
	private final int N = 7;
	public List<Building> getGroupBuildings(Context context){
		  for (int i=0; i<N; i++) {
	            final float r1 = Util.frand();
	            final Building s;
	            s = new Building(context);
	            s.z = (float)i/N;
	            s.setBackColor();
	            this.groupsbuildings.add(s);
	        }
		
		return this.groupsbuildings;
		
	}

}
