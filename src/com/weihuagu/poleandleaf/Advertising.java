package com.weihuagu.poleandleaf;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.techjun.lland.R;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Advertising extends  AdListener{
	private InterstitialAdAble adable;
	private InterstitialAd mInterstitialAd;
	private Context context;
	public Advertising(Context context, InterstitialAdAble able){
		this.adable=able;
		this.context=context;
		mInterstitialAd = new InterstitialAd(context);
		mInterstitialAd.setAdUnitId(context.getString(R.string.interstitial_ad_unit_id));
		
		
		
	
	}
	public void showInterstitial(){
		 if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
	            mInterstitialAd.show();
	        } else {
	            Toast.makeText(this.context, "Ad did not load", Toast.LENGTH_SHORT).show();
	            this.adable.startGame();
	}
	}
	
	
	@Override  
    public void onAdClosed() {  
        super.onAdClosed();  
        adable.startGame();
        Log.v("admob", "onAdClosed");  
    }  

    @Override  
    public void onAdFailedToLoad(int errorCode) {  
        super.onAdFailedToLoad(errorCode);  
        Log.v("admob", "onAdFailedToLoad");  
    }  

    @Override  
    public void onAdLeftApplication() {  
        super.onAdLeftApplication();  
        Log.v("admob", "onAdLeftApplication");  
    }  

    @Override  
    public void onAdLoaded() {  
        super.onAdLoaded();  
        Log.v("admob", "onAdLoaded");  
    }  

    @Override  
    public void onAdOpened() {  
        super.onAdOpened();  
        Log.v("admob", "onAdOpened");  
    }  
    
    
	
	
}