package com.weihuagu.poleandleaf;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.weihuagu.poleandleaf.IAdView;
import com.weihuagu.poleandleaf.R;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Advertising extends  AdListener{
	private InterstitialAdAble adable;
	private InterstitialAd mInterstitialAd;
	private Context context;
	private AdView view=null;
	public Advertising(Context context, InterstitialAdAble able){
		this.adable=able;
		this.context=context;
		MobileAds.initialize(context, "ca-app-pub-4720896488551810~3556115884");
		this.mInterstitialAd = new InterstitialAd(context);
		this.mInterstitialAd.setAdUnitId("ca-app-pub-4720896488551810/3556115884");
		this.mInterstitialAd.setAdListener(this);
		AdRequest adRequest = new AdRequest.Builder().build();
	    this.mInterstitialAd.loadAd(adRequest);
	    Toast.makeText(context, "Ad will load", Toast.LENGTH_SHORT).show();
		
	}
	public  Advertising(){
		
	}
	public void loadBannerAd(IAdView adview){
		this.view=adview.getAdView();
		AdRequest adRequest = new AdRequest.Builder().build();
		view.setAdListener(this);
		view.loadAd(adRequest);
	}
	public void showInterstitial(){
		 if (mInterstitialAd.isLoaded()) {
			   Log.v("admob", "Ad will show"+this.mInterstitialAd.getAdUnitId());
	            this.mInterstitialAd.show();
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