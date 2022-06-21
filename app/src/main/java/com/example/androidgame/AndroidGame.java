package com.example.androidgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.LogRecord;

public class AndroidGame extends View {
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    public static MediaPlayer shoot;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 3;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    Player player;
    Enemy enemy;
    Random random;
    ArrayList<Missile> enemyMissile, playerMissile;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyMissileAction = false;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * @param context public method for the main function
     */
    public AndroidGame(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        shoot = MediaPlayer.create(this.getContext(),R.raw.shoot);
        enemyMissile = new ArrayList<>();
        playerMissile = new ArrayList<>();
        explosions = new ArrayList<>();
        player = new Player(context);
        enemy = new Enemy(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);



    }

    @Override
    protected void onDraw(Canvas canvas) {//draws background, points and life on canvas
        String lv = "1";

        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("pt: "+ points, 0, TEXT_SIZE, scorePaint);
        canvas.drawText("lv: "+ lv,0,170,scorePaint);
        for (int i=life; i>=1; i--){
            canvas.drawBitmap(lifeImage, screenWidth -lifeImage.getWidth()*i, 0,null);

        }
        //when life becomes 0, stop game and launch gameover activity within paint
        if (life == 0){
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            //memory optimisation
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();

        }
        // Move enemySpaceship
        enemy.ex += enemy.enemyVelocity;
        // If enemySpaceship collides with right wall, reverse enemyVelocity
        if(enemy.ex + enemy.getEnemyWidth() >= screenWidth){
            enemy.enemyVelocity *= -1;
        }
        // If enemySpaceship collides with left wall, again reverse enemyVelocity
        if(enemy.ex <=0){
            enemy.enemyVelocity *= -1;
        }
        // Till enemyShotAction is false, enemy should fire shots from random travelled distance
        if(enemyMissileAction == false){
            if(enemy.ex >= 200 + random.nextInt(400)){
                Missile enemyShot = new Missile(context, enemy.ex + enemy.getEnemyWidth() / 2, enemy.ey );
                enemyMissile.add(enemyShot);
                shoot.start();
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyMissileAction = true;
            }
            if(enemy.ex >= 400 + random.nextInt(800)){
                Missile enemyShot = new Missile(context, enemy.ex + enemy.getEnemyWidth() / 2, enemy.ey );
                enemyMissile.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyMissileAction = true;
            }
            else{
                Missile enemyShot = new Missile(context, enemy.ex + enemy.getEnemyWidth() / 2, enemy.ey );
                enemyMissile.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyMissileAction = true;
            }
        }
        // Draw the enemy Spaceship
        canvas.drawBitmap(enemy.getEnemy(), enemy.ex, enemy.ey, null);
        // Draw our spaceship between the left and right edge of the screen
        if(player.px > screenWidth - player.getPlayerWidth()){
            player.px = screenWidth - player.getPlayerWidth();
        }else if(player.px < 0){
            player.px = 0;
        }
        // Draw our Spaceship
        canvas.drawBitmap(player.getPlayer(), player.px, player.py, null);
        // Draw the enemy shot downwards our spaceship and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.
        for(int i=0; i < enemyMissile.size(); i++){
            enemyMissile.get(i).my += 15;
            canvas.drawBitmap(enemyMissile.get(i).getMissile(), enemyMissile.get(i).mx, enemyMissile.get(i).my, null);
            if((enemyMissile.get(i).mx >= player.px)
                    && enemyMissile.get(i).mx <= player.px + player.getPlayerWidth()
                    && enemyMissile.get(i).my >= player.py
                    && enemyMissile.get(i).my <= screenHeight){
                enemyMissile.remove(i);
                life--;

                explosion = new Explosion(context, player.px, player.py);
                explosions.add(explosion);

            }else if(enemyMissile.get(i).my >= screenHeight){
                enemyMissile.remove(i);
            }
            if(enemyMissile.size() < 1){
                enemyMissileAction = false;
            }
        }
        // Draw our spaceship shots towards the enemy. If there is a collision between our shot and enemy
        // spaceship, increment points, remove the shot from ourShots and create a new Explosion object.
        // Else if, our shot goes away through the top edge of the screen also remove
        // the shot object from enemyShots ArrayList.
        for(int i=0; i < playerMissile.size(); i++){
            playerMissile.get(i).my -= 15;
            canvas.drawBitmap(playerMissile.get(i).getMissile(), playerMissile.get(i).mx, playerMissile.get(i).my, null);
            if((playerMissile.get(i).mx >= enemy.ex)
                    && playerMissile.get(i).mx <= enemy.ex + enemy.getEnemyWidth()
                    && playerMissile.get(i).my <= enemy.getEnemyWidth()
                    && playerMissile.get(i).my >= enemy.ey){
                points++;
                playerMissile.remove(i);
                explosion = new Explosion(context, enemy.ex, enemy.ey);
                explosions.add(explosion);

            }else if(playerMissile.get(i).my <=0){
                playerMissile.remove(i);
            }
        }
        // Do the explosion
        for(int i=0; i < explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 8){
                explosions.remove(i);
            }
        }
        // If not paused, we’ll call the postDelayed() method on handler object which will cause the
        // run method inside Runnable to be executed after 30 milliseconds, that is the value inside
        // UPDATE_MILLIS.
        if(!paused)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)event.getX();
        // When event.getAction() is MotionEvent.ACTION_UP, if ourShots arraylist size < 1,
        // create a new Shot.
        // This way we restrict ourselves of making just one shot at a time, on the screen.
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(playerMissile.size() < 1){
                Missile ourShot = new Missile(context, player.px + player.getPlayerWidth() / 2, player.py);
                playerMissile.add(ourShot);
                shoot.start();
            }
        }
        // When event.getAction() is MotionEvent.ACTION_DOWN, control ourSpaceship
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            player.px = touchX;
        }
        // When event.getAction() is MotionEvent.ACTION_MOVE, control ourSpaceship
        // along with the touch.
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            player.px = touchX;
        }
        // Returning true in an onTouchEvent() tells Android system that you already handled
        // the touch event and no further handling is required.
        return true;
    }

    //user level creation (not working)
     /*
         @Override public int numberOfNeededComponents() {
        return 2;
    }

    @Override public BigDecimal magnitude(Object value) {


        if (narrowed.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal keysMagnitude =
            narrowed.keySet().stream()
                .map(e -> componentGenerators().get(0).magnitude(e))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valuesMagnitude =
            narrowed.values().stream()
                .map(e -> componentGenerators().get(1).magnitude(e))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BigDecimal.valueOf(narrowed.size())
            .multiply(keysMagnitude)
            .add(valuesMagnitude);
    }

    protected final T empty() {
        return instantiate(findConstructor(types().get(0)));
    }

    protected boolean okToAdd(Object key, Object value) {
        return true;
    }

    private boolean inSizeRange(T target) {
        return sizeRange == null
            || (target.size() >= sizeRange.min() && target.size() <= sizeRange.max());
    }

    private int size(SourceOfRandomness random, GenerationStatus status) {
        return sizeRange != null
            ? random.nextInt(sizeRange.min(), sizeRange.max())
            : status.size();
    }

    private List<T> removals(List<Entry<?, ?>> items) {
        return stream(halving(items.size()).spliterator(), false)
            .map(i -> removeFrom(items, i))
            .flatMap(Collection::stream)
            .map(this::convert)
            .filter(this::inSizeRange)
            .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private T convert(List<?> entries) {
        T converted = empty();

        for (Object each : entries) {
            Entry<?, ?> entry = (Entry<?, ?>) each;
            converted.put(entry.getKey(), entry.getValue());
        }

        return converted;
    }

    private Shrink<Entry<?, ?>> entryShrinker(
        Shrink<Object> keyShrinker,
        Shrink<Object> valueShrinker) {

        return (r, e) -> {
            @SuppressWarnings("unchecked")
            Entry<Object, Object> entry = (Entry<Object, Object>) e;

            List<Object> keyShrinks = keyShrinker.shrink(r, entry.getKey());
            List<Object> valueShrinks = valueShrinker.shrink(r, entry.getValue());
            List<Entry<?, ?>> shrinks = new ArrayList<>();
            shrinks.addAll(
                keyShrinks.stream()
                    .map(k -> new SimpleEntry<>(k, entry.getValue()))
                    .collect(toList()));
            shrinks.addAll(
                valueShrinks.stream()
                    .map(v -> new SimpleEntry<>(entry.getKey(), v))
                    .collect(toList()));

            return shrinks;
        };
    }
     **/

}



