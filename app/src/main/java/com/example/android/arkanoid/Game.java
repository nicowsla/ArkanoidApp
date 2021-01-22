package com.example.android.arkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Game extends View implements SensorEventListener, View.OnTouchListener {

    private Bitmap background;
    private Bitmap redBall;
    private Bitmap stretchedOut;
    private Bitmap paddle_p;




    private Display display;
    private Point size;
    private Paint paint;

    private Ball ball;
    private ArrayList<Brick> list;
    private Paddle paddle;

    private RectF r;

    private SensorManager sManager;
    private Sensor accelerometer;

    private int lifes;
    private int score;
    private int level;
    private boolean start;
    private boolean gameOver;
    private Context context;




    private int screenWidth;
    private int screenHeight;

    private boolean accelerometro = false;
    private boolean touch = false;



    public Game(Context context, int lifes, int score, int level, int screenWidth, int screenHeight ) {
        super(context);
        paint = new Paint();
        // continue context, lifes, score a level
        this.context = context;
        this.lifes = lifes;
        this.score = score;
        this.level = level;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // start a gameOver to see if the game continues or the player has lost all the lives
        start = false;
        gameOver = false;

        readBackground(context);
        //creates an accelerometer and a SensorManager
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        // create a bitmap for the ball and paddle
        redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        paddle_p = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);


        // creates a new ball, paddle, and list of bricks
        ball = new Ball(size.x / 2, size.y - 480, level);
        paddle = new Paddle(size.x / 2, size.y - 400);
        list = new ArrayList<Brick>();

        generateBricks(context,level);
        this.setOnTouchListener(this);

    }




    //fills the list with bricks
    private void generateBricks(Context context, int level) {

        int numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
        //System.out.println(NumeroLivello);

        for (int i = 3; i < level+3; i++) {
            for (int j = 1; j < 9; j++) {

                 list.add(new Brick(context, (j * 100 * screenWidth) / 1080, (i * 70 * screenHeight) / 2340, numero));

            }
        }
    }

    // set background
    private void readBackground(Context context) {
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score));
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    protected void onDraw(Canvas canvas) {
        // creates a background only once
        if (stretchedOut == null) {
            stretchedOut = Bitmap.createScaledBitmap(background, size.x, size.y, false);
        }
        canvas.drawBitmap(stretchedOut, 0, 0, paint);

        // draw the ball
        paint.setColor(Color.RED);
        canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint);


        // draw fell
        paint.setColor(Color.WHITE);
        r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + 200, paddle.getY() + 40);
        canvas.drawBitmap(paddle_p, null, r, paint);

        // draw bricks
        paint.setColor(Color.GREEN);
        for (int i = 0; i < list.size(); i++) {
            Brick b = list.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/1080, b.getY() + (70*screenHeight)/2340) ;
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        // draw text
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);

        float velocitaX = ball.xSpeed;
        float velocitaY = ball.ySpeed;

        canvas.drawText("" + lifes, 400, 100, paint);
        canvas.drawText("" + score, 700, 100, paint);
        canvas.drawText("livello:"+level,50,50, paint );
        canvas.drawText("velocitaX:"+velocitaX,50,100, paint );
        canvas.drawText("velocitaY:"+velocitaY,50,150, paint );

        //in case of loss draw "Game over!"
        if (gameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game over!", size.x / 4, size.y / 2, paint);
        }
    }

    //check that the ball has not touched the edge
    private void chechEdges() {
        if (ball.getX() + ball.getxSpeed() >= size.x - 60) {
            ball.changeDirection("rights");
        } else if (ball.getX() + ball.getxSpeed() <= 0) {
            ball.changeDirection("left");
        } else if (ball.getY() + ball.getySpeed() <= 150) {
            ball.changeDirection("up");
        } else if (ball.getY() + ball.getySpeed() >= size.y - 200) {
            checkLives();
        }
    }

    //checks the status of the game. whether my lives or whether the game is over
    private void checkLives() {
        if (lifes == 1) {
            gameOver = true;
            start = false;
            invalidate();
        } else {
            lifes--;
            ball.setX(size.x / 2);
            ball.setY(size.y - 480);
            ball.createSpeed(level);
           // ball.increaseSpeed(level);
            start = false;
        }
    }

    //each step checks whether there is a collision, a loss or a win, etc.
    public void update() {
        if (start) {
            win();
            chechEdges();
            ball.suddentlyPaddle(paddle.getX(), paddle.getY());
            for (int i = 0; i < list.size(); i++) {
                Brick b = list.get(i);
                if (ball.suddentlyBrick(b.getX(), b.getY())) {
                    list.remove(i);
                    score = score + 80;
                }
            }
            ball.hurryUp();
        }
    }

    public void flagRemoval() {
        sManager.unregisterListener(this);
    }

    public void lowerShooting() {
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //change accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(accelerometro) {                                                     //se il flag accelerometro è true vuol dire che si sta giocando con l'accereometro
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                paddle.setX(paddle.getX() - event.values[0] - event.values[0]);

                if (paddle.getX() + event.values[0] > size.x - 240) {
                    paddle.setX(size.x - 240);
                } else if (paddle.getX() - event.values[0] <= 20) {
                    paddle.setX(20);
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //serves to suspend the game in case of a new game
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gameOver && !start) {
            score = 0;
            lifes = 3;    //se cambi vite cambia anche qui
            resetLevel(level);
            gameOver = false;
        }else if(start && !gameOver && !accelerometro && touch) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    paddle.setX(event.getRawX());
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    paddle.setX(event.getRawX()-100); //posiziona il paddle in modo centrato!!
                    invalidate();
                    return true;
                case MotionEvent.ACTION_DOWN:
                    paddle.setX(event.getRawX());
                    return true;
            }
        }else if(start && !gameOver && !accelerometro && !touch){
            float x = event.getRawX();
            float x_paddle = paddle.getX();
            if(x<(screenWidth/2) && x_paddle>90){
                paddle.setX(paddle.getX()-100);
            }else if(x>(screenWidth/2) && x_paddle<(screenWidth-280)){
                paddle.setX(paddle.getX()+100);
            }
        }
        else {
            start = true;
        }
        return false;
    }

    // sets the game to start
    private void resetLevel(int level) {
        ball.setX(size.x / 2);
        ball.setY(size.y - 480);
        ball.createSpeed(level);
        list = new ArrayList<Brick>();
        generateBricks(context,level);
    }


    // find out if the player won or not
    private void win() {
        if (list.isEmpty()) {
            ++level;
            resetLevel(level);
           // ball.increaseSpeed(level);
            start = false;
        }
    }
}
