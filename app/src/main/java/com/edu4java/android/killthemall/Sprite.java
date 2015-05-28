package com.edu4java.android.killthemall;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Natalia on 2015-05-25.
 */
public class Sprite {

    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 back, 1 left, 0 front, 2 right
    int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
    private static final int BMP_ROWS = 4;
    private static final int BMP_COLUMNS = 3;
    private static final int MAX_SPEED = 5;
    private int x = 0;
    private int y = 0;
    private int xSpeed;
    private int ySpeed;
    private GameView gameView;
    private Bitmap bmp;
    private int currentFrame = 0;
    private int width;
    private int height;
    public boolean goodOne;
    private int XSpeed;
    private int YSpeed;

    public Sprite(GameView gameView, Bitmap bmp, boolean goodOne) {
        this.gameView = gameView;
        this.bmp = bmp;
        this.width = bmp.getWidth()/BMP_COLUMNS;
        this.height = bmp.getHeight()/BMP_ROWS;
        this.goodOne = goodOne;

        Random rnd = new Random(System.currentTimeMillis());
        xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
        ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;

        if(goodOne){
            x = 100;
            y = 100;
        }
    }

    private void update() {
        if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) {
            xSpeed = -xSpeed;
        }
        x = x + xSpeed;
        if (y >= gameView.getHeight() - height -ySpeed || y + ySpeed <= 0) {
            ySpeed = -ySpeed;
        }
        y = y + ySpeed;
        currentFrame = ++currentFrame % BMP_COLUMNS;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame * width;
        int srcY = getAnimationRow() * height;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src , dst, null);
    }

    private int getAnimationRow() {
        // atan2 > Returns the closest double approximation of the arc tangent of y/x within the range [-pi..pi].
        double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % BMP_ROWS;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }

    public boolean isCollision(float x, float y) {
        return (x >this.x && x < this.x + width && y >this.y &&y<this.y + height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSpeed(float xDirection, float yDirection) {
        xSpeed = (int) (((xDirection - x)/gameView.getWidth())*MAX_SPEED*2);
        ySpeed = (int) (((yDirection - y)/gameView.getHeight())*MAX_SPEED*2);
    }

    public int getXSpeed() {
        return XSpeed;
    }


    public int getYSpeed() {
        return YSpeed;
    }
}