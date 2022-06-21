package com.example.androidgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Space;

public class MainActivity extends AppCompatActivity {

    private Thread thread;
    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new AndroidGame(this));
    }

    @Override
    protected void onDestroy() {
        thread = new Thread((Runnable) this);
        thread.start();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {

            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void sleep () {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () {

        isPlaying = true;
        thread = new Thread((Runnable) this);
        thread.start();

    }

}