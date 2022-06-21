package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Missile {
    Bitmap Missile;
    Context context;
    int mx, my;

    public Missile(Context context, int mx, int my) {
        this.context = context;
        Missile = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.shot);
        this.mx = mx;
        this.my = my;
    }
    public Bitmap getMissile(){
        return Missile;
    }
    public int getMissileWidth() {
        return Missile.getWidth();
    }
    public int getMissileHeight() {
        return Missile.getHeight();
    }
}