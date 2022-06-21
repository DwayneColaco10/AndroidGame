package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;
public class Player {
    Context context;
    Bitmap Player;
    int px, py;
    Random random;

    public Player(Context context) {
        this.context = context;
        Player = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket1);
        random = new Random();
        px = random.nextInt(AndroidGame.screenWidth);
        py = AndroidGame.screenHeight - Player.getHeight();
    }

    public Bitmap getPlayer(){
        return Player;
    }

    int getPlayerWidth(){
        return Player.getWidth();
    }
}