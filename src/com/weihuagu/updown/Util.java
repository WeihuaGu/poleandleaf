package com.weihuagu.updown;

public class Util {
	 public static final float lerp(float x, float a, float b) {
	        return (b - a) * x + a;
	    }

	    public static final float rlerp(float v, float a, float b) {
	        return (v - a) / (b - a);
	    }

	    public static final float clamp(float f) {
	        return f < 0f ? 0f : f > 1f ? 1f : f;
	    }

	    public static final float frand() {
	        return (float) Math.random();
	    }

	    public static final float frand(float a, float b) {
	        return lerp(frand(), a, b);
	    }

	    public static final int irand(int a, int b) {
	        return (int) lerp(frand(), (float) a, (float) b);
	    }

}
