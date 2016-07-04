package com.weihuagu.poleandleaf;

import com.weihuagu.poleandleaf.Player;
import com.weihuagu.poleandleaf.R;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class Obstacle extends View implements GameView{
	  public float h;

      public final Rect hitRect = new Rect();
      protected int TRANSLATION_PER_SEC;

      public Obstacle(Context context, float h) {
          super(context);
          TRANSLATION_PER_SEC = this.getResources().getDimensionPixelSize(R.dimen.translation_per_sec);
          setBackgroundColor(0xFFFF0000);
          this.h = h;
      }
      public Obstacle(Context context) {
          super(context);
          TRANSLATION_PER_SEC = this.getResources().getDimensionPixelSize(R.dimen.translation_per_sec);
      }
      
      public boolean intersects(Player p) {
          final int N = p.corners.length/2;
          for (int i=0; i<N; i++) {
              final int x = (int) p.corners[i*2];
              final int y = (int) p.corners[i*2+1];
              if (hitRect.contains(x, y)) return true;
          }
          return false;
      }

      public boolean cleared(Player p) {
          final int N = p.corners.length/2;
          for (int i=0; i<N; i++) {
              final int x = (int) p.corners[i*2];
              if (hitRect.right >= x) return false;
          }
          return true;
      }

      @Override
      public void step(long t_ms, long dt_ms, float t, float dt) {
          setTranslationX(getTranslationX()-TRANSLATION_PER_SEC*dt);
          getHitRect(hitRect);
      }

}
