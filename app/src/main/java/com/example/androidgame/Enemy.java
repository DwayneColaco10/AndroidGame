package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Enemy {
    Context context;
    Bitmap enemy;
    int ex, ey;
    int enemyVelocity;
    Random random;

    public Enemy(Context context) {
        this.context = context;
        enemy = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket2);
        random = new Random();
        ex = 200 + random.nextInt(400);
        ey = 0;
        enemyVelocity = 14 + random.nextInt(10);
    }

    public Bitmap getEnemy(){
        return enemy;
    }

    int getEnemyWidth(){
        return enemy.getWidth();
    }

    int getEnemyHeight(){
        return enemy.getHeight();
    }
}
