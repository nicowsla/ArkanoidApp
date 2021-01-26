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

public class Game extends View implements SensorEventListener, View.OnTouchListener, Levels {

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
    private int storia;
    private int classificata;
    private int buttonValue;

    private boolean accelerometro = false;
    private boolean touch = false;



    public Game(Context context, int lifes, int score, int level, int screenWidth, int screenHeight, int storia, int classificata) {
        super(context);
        paint = new Paint();

        // continue context, lifes, score a level
        this.context = context;
        this.lifes = lifes;
        this.score = score;
        this.level = level;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.storia = storia;
        this.classificata = classificata;

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
        ball = new Ball((size.x / 2) - (30*screenWidth)/1080, size.y - (470*screenHeight)/1920, level);
        paddle = new Paddle((size.x / 2) - (100*screenWidth)/1080, size.y - (400*screenHeight)/1920);
        list = new ArrayList<Brick>();

        if(storia == 1){
            buttonValue = 1;
            generateBricks(context,level,buttonValue);
        }else if(classificata == 2){
            buttonValue = 2;
            generateBricks(context,level,buttonValue);
        }

        this.setOnTouchListener(this);

    }

    //fills the list with bricks
    private void generateBricks(Context context, int level, int button) {

        if(button == 2){
            //In questo modo genero una serie di righe
            int numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
            //System.out.println(NumeroLivello);
            for (int i = 3; i < level+3; i++) {
                for (int j = 1; j < 10; j++) {
                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * size.y) / screenHeight, numero));
                }
            }

        }else {
            //In questo modo genero una serie di righe
            //int numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
            //System.out.println(NumeroLivello);
            //Parto da 3 perchè mi abbasso
            for (int i = 3; i < 20; i++) {
                for (int j = 1; j < 10; j++) {
                    switch (level) {
                        case 1:
                            if (Levels.Livello1MARIO[i][j] != 0) {
                                list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello1MARIO[i][j]));
                            }
                            break;
                        case 2:
                            if (Levels.Livello2PIKACHU[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello2PIKACHU[i][j]));
                            }
                            break;
                        case 3:
                            if (Levels.Livello3ZELDA[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello3ZELDA[i][j]));
                            }
                            break;
                        case 4:
                            if (Levels.Livello4IRONMAN[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello4IRONMAN[i][j]));
                            }
                            break;
                        case 5:
                            if (Levels.Livello5FANTASMINO[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello5FANTASMINO[i][j]));
                            }
                            break;
                        case 6:
                            if (Levels.Livello6PACMAN[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello6PACMAN[i][j]));
                            }
                            break;
                        case 7:
                            if (Levels.Livello7BATMAN[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello7BATMAN[i][j]));
                            }
                            break;
                        case 8:
                            if (Levels.Livello8SFERA[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello8SFERA[i][j]));
                            }
                            break;
                        case 9:
                            if (Levels.Livello9FIORE[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello9FIORE[i][j]));
                            }
                            break;
                        case 10:
                            if (Levels.Livello10CREEPER[i][j] != 0) {
                                list.add(new Brick(context, (j * 100 * screenWidth) / screenWidth, (i * 70 * screenHeight) / screenHeight, Levels.Livello10CREEPER[i][j]));
                            }
                            break;
                    }

                }
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
            stretchedOut = Bitmap.createScaledBitmap(background, size.x, size.y, true);
        }
        //Posiziona lo sfondo nello schermo
        canvas.drawBitmap(stretchedOut, 0, 0, null);

        // draw the ball
        paint.setColor(Color.RED);
        canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint);

        // draw fell

        // draw fell, disegna rettangolo cioè barra
        paint.setColor(Color.WHITE);
        //La riga sotto era +200 + 40
        r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + (200*screenWidth)/1080, paddle.getY() + (40*screenHeight)/1920);
        canvas.drawBitmap(paddle_p, null, r, paint);

        // draw bricks
        paint.setColor(Color.GREEN);
        for (int i = 0; i < list.size(); i++) {
            Brick b = list.get(i);
            //r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/screenWidth, b.getY() + (70*screenHeight)/screenHeight) ;
            r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/1080, b.getY() + (70*screenHeight)/1920) ;
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        // draw text
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);

        float velocitaX = ball.xSpeed;
        float velocitaY = ball.ySpeed;

        canvas.drawText("" + lifes, (size.x/4), 100, paint);
        canvas.drawText("" + score, (size.x/4)*2, 100, paint);
        canvas.drawText("" + level,(size.x/4)*3,100, paint );
        //canvas.drawText("ballX:"+ball.getX(),50,150, paint );
        //canvas.drawText("ballY:"+ball.getY(),50,200, paint );
        //canvas.drawText("xpaddle:"+paddle.getX(),50,250, paint );
        //canvas.drawText("xpaddle:"+paddle.getY(),50,300, paint );
        //canvas.drawText("size:"+size.x,50,200, paint );
        //canvas.drawText("size:"+size.y,50,250, paint );
        //canvas.drawText("size1:"+screenWidth,50,300, paint );
        //canvas.drawText("size1:"+screenHeight,50,350, paint );

        //in case of loss draw "Game over!"
        if (gameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game over!", size.x / 3, size.y / 2, paint);
        }
    }

    //check that the ball has not touched the edge
    private void chechEdges() {
        if (ball.getX() + ball.getxSpeed() >= size.x - (60*screenWidth)/1080) {
            ball.changeDirection("rights");
        } else if (ball.getX() + ball.getxSpeed() <= (0*screenWidth)/1080) {
            ball.changeDirection("left");
        } else if (ball.getY() + ball.getySpeed() <= (150*screenHeight)/1920) {
            ball.changeDirection("up");
        } else if (ball.getY() + ball.getySpeed() >= size.y - (200*screenHeight)/1920) {
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
            ball.setX((size.x / 2) - (30*screenWidth)/1080);
            ball.setY(size.y - (470*screenHeight)/1920);
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
            ball.suddentlyPaddle(paddle.getX(), paddle.getY(),screenWidth,screenHeight);
            for (int i = 0; i < list.size(); i++) {
                Brick b = list.get(i);
                if (ball.suddentlyBrick(b.getX(), b.getY(),screenWidth,screenHeight)) {
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
        if(accelerometro) { //se il flag accelerometro è true vuol dire che si sta giocando con l'accereometro
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                paddle.setX(paddle.getX() - event.values[0] - event.values[0]);

                if (paddle.getX() + event.values[0] > size.x - (235*screenWidth)/1080) {
                    paddle.setX(size.x - (235*screenWidth)/1080);
                } else if (paddle.getX() - event.values[0] <= (35*screenWidth)/1080) {
                    paddle.setX((35*screenWidth)/1080);
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
            resetLevel(level,buttonValue);
            gameOver = false;

            //LA DIMNSIONE DELLO SCHERMO IN LARGHEZZA VA DA 35 A 235
        }else if(start && !gameOver && !accelerometro) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    paddle.setX(event.getRawX());
                    if (event.getRawX() > size.x - (235*screenWidth)/1080) {
                        paddle.setX(size.x - (235*screenWidth)/1080);
                    } else if (event.getRawX() <= (35*screenWidth)/1080) {
                        paddle.setX((35*screenWidth)/1080);
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    paddle.setX(event.getRawX());
                    if (event.getRawX() > size.x - (235*screenWidth)/1080) {
                        paddle.setX(size.x - (235*screenWidth)/1080);
                    } else if (event.getRawX() <= (35*screenWidth)/1080) {
                        paddle.setX((35*screenWidth)/1080);
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_DOWN:
                    paddle.setX(event.getRawX());
                    if (event.getRawX() > size.x - (235*screenWidth)/1080) {
                        paddle.setX(size.x - (235*screenWidth)/1080);
                    } else if (event.getRawX() <= (35*screenWidth)/1080) {
                        paddle.setX((35*screenWidth)/1080);
                    }
                    return true;
            }
        }else {
            start = true;
        }
        return false;
    }

    // sets the game to start
    private void resetLevel(int level, int buttonValue) {
        ball.setX((size.x / 2) - (30*screenWidth)/1080);
        ball.setY(size.y - (470*screenHeight)/1920);
        ball.createSpeed(level);
        list = new ArrayList<Brick>();
        generateBricks(context,level,buttonValue);
    }


    // find out if the player won or not
    private void win() {
        if (list.isEmpty()) {
            ++level;
            resetLevel(level,buttonValue);
           // ball.increaseSpeed(level);
            start = false;
        }
    }
}
