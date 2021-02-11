package com.example.android.arkanoid.entity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.constraintlayout.solver.widgets.Rectangle;

public class Ball{

    protected float xSpeed;
    protected float ySpeed;
    private float x;
    private float y;

    public Ball(float x, float y, int level) {
        this.x = x;
        this.y = y;
        createSpeed(level);
    }

    // creates a random speed ball
    public void createSpeed(int level) {
        int minX;
        int minY = -10;
        if(Math.random()*100>50){ //la pallina va a destra o sinistra in modo casuale alla partenza
            minX = 6;
            xSpeed = (level)+ minX;
        }else{
            minX = -6;
            xSpeed = minX - level;
        }
        System.out.println(Math.random()*100);
        ySpeed = minY - level;
    }

    // changes direction according to speed
    protected void changeDirection() {
        if (xSpeed > 0 && ySpeed < 0) {
            otocXSpeed();
        } else if (xSpeed < 0 && ySpeed < 0) {
            otocYSpeed();
        } else if (xSpeed < 0 && ySpeed > 0) {
            otocXSpeed();
        } else if (xSpeed > 0 && ySpeed > 0) {
            otocYSpeed();
        }
    }

    //increase speed based on level
    protected void increaseSpeed(int level) {
        xSpeed = xSpeed + (2 * level);
        ySpeed = ySpeed - (2 * level);
    }

    // changes direction depending on which wall it touched and speed
    public void changeDirection(String wall) {
        if (xSpeed > 0 && ySpeed < 0 && wall.equals("rights")) {
            otocXSpeed();
        } else if (xSpeed > 0 && ySpeed < 0 && wall.equals("up")) {
            otocYSpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && wall.equals("up")) {
            otocYSpeed();
        } else if (xSpeed < 0 && ySpeed < 0 && wall.equals("left")) {
            otocXSpeed();
        } else if (xSpeed < 0 && ySpeed > 0 && wall.equals("left")) {
            otocXSpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && wall.equals("down")) {
            otocYSpeed();
        } else if (xSpeed > 0 && ySpeed > 0 && wall.equals("rights")) {
            otocXSpeed();
        }
    }

    // zisti ci je lopticka blizko
    //clean ci is a ball close traduttore di merda che minchia vuol dire
    private boolean isNear(float xPaddle, float yPaddle, float xBall, float yBall, int screenWidth, int screenHeight) {
        // temporary variables to set edges for testing
        int raggio = 60;
        float rect_width = (200*screenWidth)/1080;
        float rect_height = (40*screenHeight)/1920;
        float testX = xBall;
        float testY = yBall;

        // which edge is closest?
        if (xBall < xPaddle) {testX = xPaddle;}      // test left edge
        else if (xBall > xPaddle+rect_width) {testX = xPaddle+rect_width;}   // right edge
        if (yBall < yPaddle) {testY = yPaddle;}      // top edge
        else if (yBall > yPaddle+rect_height) {testY = yPaddle+rect_height;}   // bottom edge

        // get distance from closest edges
        float distX = xBall-testX;
        float distY = yBall-testY;
        double distance = Math.sqrt((distX*distX) + (distY*distY));

        // if the distance is less than the radius, collision!
        if (distance <= raggio) {
            return true;
        }
        return false;

    }

    public static boolean isCollisionDetected(Bitmap bitmap1, int x1, int y1,
                                              Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }


    //find out if the ball is close to a brick
    private boolean isCloseToBrick(float ax, float ay, float bx, float by, int screenWidth, int screenHeight) {
        /*bx += 2;
        by += 1;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;*/
        int raggio = 60;
        float rect_width = (100*screenWidth)/1080;
        float rect_height = (70*screenHeight)/1920;
        float testX = bx;
        float testY = by;

        // which edge is closest?
        if (bx < ax) {testX = ax;}      // test left edge
        else if (bx > ax+rect_width) {testX = ax+rect_width;}   // right edge
        if (by < ay) {testY = ay;}      // top edge
        else if (by > ay+rect_height) {testY = ay+rect_height;}   // bottom edge

        // get distance from closest edges
        float distX = bx-testX;
        float distY = by-testY;
        double distance = Math.sqrt((distX*distX) + (distY*distY));

        // if the distance is less than the radius, collision!
        if (distance <= raggio) {
            return true;
        }
        return false;
    }

    //if the ball collides with the fall, it will change direction
    public void suddentlyPaddle(float xPaddle, float yPaddle, Bitmap paddle, Bitmap ball) {
        if (isCollisionDetected(paddle, (int)xPaddle,(int) yPaddle, ball,(int) getX(),(int) getY())) changeDirection();
    }

    //if the ball collides with a brick, it changes direction
    public boolean suddentlyBrick(float xBrick, float yBrick, Bitmap brick, Bitmap ball) {
     //   if (isCloseToBrick(xBrick, yBrick, getX(), getY(),screenWidth,screenHeight)) {
        if(isCollisionDetected(brick, (int)xBrick,(int) yBrick, ball,(int) getX(),(int) getY())){
            changeDirection();
            return true;
        } else return false;
    }

    //moves by the specified speed
    public void hurryUp() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public void otocXSpeed() {
        xSpeed = -xSpeed;
    }

    public void otocYSpeed() {
        ySpeed = -ySpeed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }
}
