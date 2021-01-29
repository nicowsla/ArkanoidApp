package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class Brick extends View {

    private Bitmap brick;
    private float x;
    private float y;

    public Brick(Context context, float x, float y, int a) {
        super(context);
        this.x = x;
        this.y = y;
        skin(a);
    }


    //assigns a random image to the brick
    private void skin(int a) {
        switch (a) {
            case 15:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_aqua);
                break;
            case 1:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_blue);
                break;
            case 2:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green);
                break;
            case 3:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_orange);
                break;
            case 4:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_pink);
                break;
            case 5:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_purple);
                break;
            case 6:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_red);
                break;
            case 7:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_yellow);
                break;
            case 8:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_black);
                break;
            case 9:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_bordeaux);
                break;
            case 10:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_green2);
                break;
            case 11:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_white);
                break;
            case 12:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_bluscuro);
                break;
            case 13:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_carne);
                break;
            case 14:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_brown);
                break;
            case 16:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_grey);
                break;
            case 17:
                brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick_lite_grey);
                break;
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getBrick() {
        return brick;
    }
}
