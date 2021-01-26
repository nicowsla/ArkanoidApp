package com.example.android.arkanoid;

public class Ball {

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
    protected void createSpeed(int level) {
        int minX = 6;
        int minY = -10;
        xSpeed = (level/2)+ minX;
        ySpeed = (level/2)+ minY;
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
    protected void changeDirection(String wall) {
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
    private boolean isNear(float ax, float ay, float bx, float by, int screenWidth, int screenHeight) {
        bx += 2;
        by += 1;
        if ((Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow(ay - by, 2))) < 80) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 100) - bx, 2) + Math.pow(ay - by, 2))) < 80) {
            return true;
        } else if ((Math.sqrt(Math.pow((ax + 150) - bx, 2) + Math.pow(ay - by, 2))) < 80) {
            return true;
        }
        return false;
    }

    //find out if the ball is close to a brick
    private boolean isCloseToBrick(float ax, float ay, float bx, float by, int screenWidth, int screenHeight) {
        bx += 2;
        by += 1;
        double d = Math.sqrt(Math.pow((ax + 50) - bx, 2) + Math.pow((ay + 40) - by, 2));
        return d < 80;
    }

    //if the ball collides with the fall, it will change direction
    protected void suddentlyPaddle(float xPaddle, float yPaddle, int screenWidth, int screenHeight) {
        if (isNear(xPaddle, yPaddle, getX(), getY(),screenWidth,screenHeight)) changeDirection();
    }

    //if the ball collides with a brick, it changes direction
    protected boolean suddentlyBrick(float xBrick, float yBrick,int screenWidth, int screenHeight) {
        if (isCloseToBrick(xBrick, yBrick, getX(), getY(),screenWidth,screenHeight)) {
            changeDirection();
            return true;
        } else return false;
    }

    //moves by the specified speed
    protected void hurryUp() {
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
