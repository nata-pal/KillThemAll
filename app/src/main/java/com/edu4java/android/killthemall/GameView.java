package com.edu4java.android.killthemall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Natalia on 2015-05-25.
 */
public class GameView extends SurfaceView {
    private Bitmap bmpBlood;
    private GameLoopThread gameLoopThread;
    private List<Sprite> sprites = new ArrayList<Sprite>();
    private List<TempSprite> temps = new ArrayList<TempSprite>();
    private List<Star> stars = new ArrayList<Star>();
    private long lastClick;
    private float xClicked;
    private float yClicked;
    private boolean isOnClick;



    public GameView(Context context) {
        super(context);
        gameLoopThread = new GameLoopThread(this);
        getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                createSprites();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        bmpBlood = BitmapFactory.decodeResource(getResources(), R.drawable.blood1);
    }

    private void createSprites() {
        sprites.add(createSprite(R.drawable.bad1, false));
        sprites.add(createSprite(R.drawable.bad2, false));
        sprites.add(createSprite(R.drawable.bad3, false));
        sprites.add(createSprite(R.drawable.bad4, false));
        sprites.add(createSprite(R.drawable.bad5, false));
        sprites.add(createSprite(R.drawable.bad6, false));
//        sprites.add(createSprite(R.drawable.good1, true));
//        sprites.add(createSprite(R.drawable.good2, true));
//        sprites.add(createSprite(R.drawable.good3, true));
//        sprites.add(createSprite(R.drawable.good4, true));
//        sprites.add(createSprite(R.drawable.good5, true));
        sprites.add(createSprite(R.drawable.good6, true));
    }

    private Sprite createSprite(int res, boolean good) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), res);
        return new Sprite(this, bmp, good);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLACK);

        List<Sprite> spritesToRemove = new ArrayList<Sprite>();
        for (Sprite spriteA : sprites) {
            if (spriteA.goodOne) {
                for (Sprite spriteB : sprites) {
                    if (!spriteB.goodOne) {
                        if (spriteA.isCollision(spriteB.getX(), spriteB.getY())) {
                            spritesToRemove.add(spriteA);
                            spritesToRemove.add(spriteB);
                        }
                    }
                }
            }
        }

        for (Star star : stars) {
            if (star != null && star.getVisible()) {
                for (Sprite spriteB : sprites) {
                    if (!spriteB.goodOne) {
                        if (spriteB.isCollision(star.getX(), star.getY())) {
                            spritesToRemove.add(spriteB);
                            star = null;
                            break;
                        }
                    }
                }
            }
            if (star != null && star.getVisible()){
                star.onDraw(canvas);
            }
        }



        for (Sprite sprite : spritesToRemove) {
            temps.add(new TempSprite(temps, this, sprite.getX(), sprite.getY(),
                    bmpBlood));
            sprites.remove(sprite);
        }

        Boolean gameOver = getWinner(canvas);


        if(!gameOver){
            for (int i = temps.size() - 1; i >= 0; i--) {
                temps.get(i).onDraw(canvas);
            }
            for (Sprite s : sprites) {
                s.onDraw(canvas);
            }
        }
    }

    private Boolean getWinner(Canvas canvas) {
        String msg = "";
        int goodSpritesNo = 0, badSpritesNo = 0;
        for (Sprite s : sprites){
            if(s.goodOne){
                goodSpritesNo++;
            } else {
                badSpritesNo++;
            }
        }

        if (goodSpritesNo == 0){
            msg = "The winner are Bad Guys";
        } else if (badSpritesNo == 0){
            msg = "The winner are Good Guys";
        } else {
            return false;
        }

        gameLoopThread.setRunning(false);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setTypeface(tf);
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(40);
        canvas.drawText(msg, 50, canvas.getHeight()/2, paint);
        return true;
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev){

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isOnClick = true;
                xClicked = ev.getX();
                yClicked = ev.getY();
                lastClick = System.currentTimeMillis();

                if(isOnClick && System.currentTimeMillis() - lastClick > 500){
                    synchronized (getHolder()){
                        for (int i = sprites.size() -1; i >=0; i--){
                            Sprite s = sprites.get(i);
                            if(s.goodOne){
                                s.setSpeed(xClicked, yClicked);
                                break;
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                isOnClick = false;
                if (System.currentTimeMillis() - lastClick < 500){
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.star1);
                    for (Sprite sprite : sprites) {
                        if (sprite.goodOne) {
                            Star s = new Star(this, bmp, sprite.getX(), sprite.getY(),
                                    sprite.getXSpeed()*3, sprite.getYSpeed()*3);

                        }
                    }
                }
                break;
            default:
                break;
        }


//            synchronized (getHolder()){
//                for (int i = sprites.size() -1; i >=0; i--){
//                    Sprite s = sprites.get(i);
//                    if(s.isCollision(event.getX(), event.getY())){
//                        sprites.remove(s);
//                        temps.add(new TempSprite(temps, this, x, y, bmpBlood));
//                        break;
//                    }
//                }
//            }

        return true;
    }

}
