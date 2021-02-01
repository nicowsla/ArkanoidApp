package com.example.android.arkanoid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;
    Boolean enableTouch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    Boolean enableAccelerometer;
    Integer previousScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mantiene il display acceso durante il gioco
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        enableTouch = pref.getBoolean("touch", true);
        enableAccelerometer = pref.getBoolean("accelerometro", false);
        previousScore = pref.getInt("bestScore", 0);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //prendo l'id per capire quale tasto è stato scelto
        Bundle i = getIntent().getExtras();
        Bundle j = getIntent().getExtras();
        Bundle k = getIntent().getExtras();
        Bundle z = getIntent().getExtras();
        int partita_a_tema = i.getInt("T");
        int classificata = j.getInt("C");
        int arcade = k.getInt("A");
        int partita_infinita = z.getInt("I");

        //sets the screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get device resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // create a new game
        game = new Game(this, 3, 0, 1, screenWidth, screenHeight, partita_a_tema, classificata, arcade, partita_infinita);
        setContentView(game);

        // create an handler and thread
        createHandler();
        myThread = new UpdateThread(updateHandler);
        myThread.start();
    }

    private void createHandler() {
        updateHandler = new Handler() {
            public void handleMessage(Message msg) {
                game.invalidate();
                game.update();
                super.handleMessage(msg);
            }
        };
    }

    protected void onPause() {
        super.onPause();
        game.flagRemoval();
        game.start = false;
    }

    protected void onResume() {
        super.onResume();
        game.lowerShooting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities

    //MANCA VOCE GAMEPAD
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            onPause();
            final CharSequence[] items={getString(R.string.resume),getString(R.string.commands), getString(R.string.exit)};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.pause));

            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals(getString(R.string.resume))) {
                        onResume();
                        dialogInterface.dismiss();
                    }else if(items[i].equals(getString(R.string.commands))){
                     //CAMBIA COMANDI

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.change_command);
                        final CharSequence[] items={getString(R.string.touch), getString(R.string.accelerometer), getString(R.string.gamepad)};
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (items[i].equals(getString(R.string.touch))) {
                                    game.touch = true;
                                    game.accelerometro = false;

                                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                    editor.putBoolean( "accelerometro", false );
                                    editor.putBoolean("touch", true );
                                    editor.apply();

                                    dialogInterface.dismiss();
                                    onResume();
                                    Toast.makeText(MainActivity.this, getString(R.string.touch_selected), Toast.LENGTH_SHORT).show();

                                }else if (items[i].equals(getString(R.string.accelerometer))){
                                    game.touch = false;
                                    game.accelerometro = true;

                                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                    editor.putBoolean( "accelerometro", true );
                                    editor.putBoolean("touch", false );
                                    editor.apply();

                                    dialogInterface.dismiss();
                                    onResume();
                                    Toast.makeText(MainActivity.this, getString(R.string.accelerometer_selected), Toast.LENGTH_SHORT).show();

                                }
                                else if(items[i].equals(getString(R.string.gamepad))){
                                    game.touch = false;
                                    game.accelerometro = false;

                                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                    editor.putBoolean( "accelerometro", false );
                                    editor.putBoolean("touch", false );
                                    editor.apply();

                                    dialogInterface.dismiss();
                                    onResume();
                                    Toast.makeText(MainActivity.this, getString(R.string.gamepad_selected), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        builder.show();

                    } else if(items[i].equals(getString(R.string.exit))){
                        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                        alertDialog.setTitle( R.string.attention );
                        alertDialog.setMessage( getString(R.string.exit_confirm) );
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        startActivity( new Intent( MainActivity.this, MenuActivity.class ) );
                                    }
                                } );
                        alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                } );
                        alertDialog.show();
                    }
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
        alertDialog.setTitle( R.string.attention );
        alertDialog.setMessage( getString(R.string.exit_confirm) );
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        startActivity( new Intent( MainActivity.this, MenuActivity.class ) );
                    }
                } );
        alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
        alertDialog.show();
    }

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
        private Sensor sensorAccelerometer;

        private int lifes;
        private int score;
        private int level;
        private boolean start;
        private boolean gameOver;
        private Context context;

        private int screenWidth;
        private int screenHeight;
        private int partita_a_tema;
        private int classificata;
        private int ardade;
        private int partita_infinita;
        private int buttonValue;
        private boolean boss = false;
        private boolean infinita = false;
        private boolean tempo = false;
        private boolean tema = false;
        private boolean attivato = false;

        private boolean accelerometro = enableAccelerometer;
        private boolean touch = enableTouch;

        private long startTime;
        private long difference;
        private double minuti;
        private double secondi;
        private double decimi;
        private double centesimi;
        private double millesimi;



        public Game(Context context, int lifes, int score, int level, int screenWidth, int screenHeight, int partita_a_tema, int classificata, int arcade, int partita_infinita) {
            super(context);
            paint = new Paint();

            // continue context, lifes, score a level
            this.context = context;
            this.lifes = lifes;
            this.score = score;
            this.level = level;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.partita_a_tema = partita_a_tema;
            this.classificata = classificata;
            this.ardade = arcade;
            this.partita_infinita = partita_infinita;


            // start a gameOver to see if the game continues or the player has lost all the lives
            start = false;
            gameOver = false;
            startTime = 0;
            difference = 0;

            readBackground(context);

            //creates an accelerometer and a SensorManager
            sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // create a bitmap for the ball and paddle
            redBall = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
            paddle_p = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);

            // creates a new ball, paddle, and list of bricks
            ball = new Ball((size.x / 2) - (30*screenWidth)/1080, size.y - (470*screenHeight)/1920, level);
            paddle = new Paddle((size.x / 2) - (100*screenWidth)/1080, size.y - (400*screenHeight)/1920);
            list = new ArrayList<Brick>();

            if(partita_a_tema == 1){
                buttonValue = 1;
                generateBricks(context,level,buttonValue);
            }else if(classificata == 2){
                buttonValue = 2;
                generateBricks(context,level,buttonValue);
            }else if(arcade == 3){
                buttonValue = 3;
                generateBricks(context,level,buttonValue);
            }else if(partita_infinita == 4){
                buttonValue = 4;
                generateBricks(context,level,buttonValue);
            }

            this.setOnTouchListener(this);

        }

        //fills the list with bricks
        private void generateBricks(Context context, int level, int button) {

            //LIVELLO MOSTRO CLASSIFICATA
            if(button == 2){
                tempo = true;
                for (int i = 3; i < 20; i++) {
                    for (int j = 1; j < 10; j++) {
                        if (Levels.LivelloMOSTRO[i][j] != 0) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.LivelloMOSTRO[i][j]));
                        }
                    }
                }
                //PARTITE A TEMA
            }else if(button == 1){
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
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello2PIKACHU[i][j]));
                                }
                                break;
                            case 3:
                                if (Levels.Livello3ZELDA[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello3ZELDA[i][j]));
                                }
                                break;
                            case 4:
                                if (Levels.Livello4IRONMAN[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello4IRONMAN[i][j]));
                                }
                                break;
                            case 5:
                                if (Levels.Livello5FANTASMINO[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello5FANTASMINO[i][j]));
                                }
                                break;
                            case 6:
                                if (Levels.Livello6PACMAN[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello6PACMAN[i][j]));
                                }
                                break;
                            case 7:
                                if (Levels.Livello7BATMAN[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello7BATMAN[i][j]));
                                }
                                break;
                            case 8:
                                if (Levels.Livello8SFERA[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello8SFERA[i][j]));
                                }
                                break;
                            case 9:
                                if (Levels.Livello9FIORE[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello9FIORE[i][j]));
                                }
                                break;
                            case 10:
                                tema = true;
                                if (Levels.Livello10CREEPER[i][j] != 0) {
                                    list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.Livello10CREEPER[i][j]));
                                }
                                break;
                        }

                    }
                }
             //PARTITA ARCADE
            }else if(button == 3){
                //In questo modo genero una serie di righe
                int numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
                if(level == 17) {
                    boss = true;
                    if (!gameOver && boss) {
                        Toast.makeText(MainActivity.this, "THE BOSS IS COMING...", Toast.LENGTH_LONG).show();
                        for (int i = 3; i < 20; i++) {
                            for (int j = 1; j < 10; j++) {
                                if (Levels.LivelloMOSTRO[i][j] != 0) {
                                    list.add(new Brick(context, (size.x / 11) * j, (i * 70 * screenHeight) / screenHeight, Levels.LivelloMOSTRO[i][j]));
                                }
                            }
                        }
                    }
                }else{
                    for (int i = 3; i < level+3; i++) {
                        for (int j = 1; j < 10; j++) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * size.y) / screenHeight, numero));
                        }
                    }
                }

                //MODALITA INFINITA
            }else if(button == 4){
                int numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
                //System.out.println(NumeroLivello);
                if(level >= 17) {
                    infinita = true;
                    if (!gameOver && infinita) {
                        Toast.makeText(MainActivity.this, "PARTITA INFINITA...", Toast.LENGTH_LONG).show();
                        for (int i = 3; i < 20; i++) {
                            for (int j = 1; j < 10; j++) {
                                list.add(new Brick(context, (size.x / 11) * j, (i * 70 * screenHeight) / screenHeight, numero));
                            }
                        }
                    }
                }else{
                    for (int i = 3; i < level+3; i++) {
                        for (int j = 1; j < 10; j++) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * size.y) / screenHeight, numero));
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

            float velocitaX = ball.getxSpeed();
            float velocitaY = ball.ySpeed;

            canvas.drawText("" + lifes, (size.x/4), 100, paint);
            canvas.drawText("" + score, (size.x/4)*2, 100, paint);
            canvas.drawText("" + level,(size.x/4)*3,100, paint );
            canvas.drawText("Inizio:" + startTime,50,150, paint );
            canvas.drawText("Fine:" + minuti + ":" +  secondi + ":" + decimi+ ":" + centesimi + ":" + millesimi,50,200, paint );
            //canvas.drawText("xpaddle:"+ getTime(),50,250, paint );
            //canvas.drawText("Ypaddle:"+paddle.getY(),50,300, paint );

            //in case of loss draw "Game over!"
            if (gameOver) {
                if(score>previousScore){
                    DatabaseReference myRef =  database.getReference("utenti").child(user.getUid()).child("bestScore");
                    myRef.child( "bestScore" ).setValue( score );
                }

                paint.setColor(Color.RED);
                paint.setTextSize(100);
                canvas.drawText("Game over!", size.x / 3, size.y / 2, paint);
                level = 1;
                infinita = false;
                boss = false;
                startTime = 0;
                //difference = 0;
                attivato = false;
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
                level = 1;
                if(tempo){
                    difference = System.currentTimeMillis() - startTime;
                    double min = difference / (1000 * 60);
                    minuti = Math.floor(min);
                    double sec = (difference - (minuti*60000)) / 1000;
                    secondi = Math.floor(sec);
                    double dec = (sec - secondi)*10;
                    decimi = Math.floor(dec);
                    double cen = (dec - decimi)*10;
                    centesimi = Math.floor(cen);
                    double mill = (cen - centesimi)*10;
                    millesimi = Math.floor(mill);
                }
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
            sManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
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
            }else if(start && !gameOver && !accelerometro && touch) { //flag accelerometro deve essere false e touch true
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
            }else if(start && !gameOver && !accelerometro && !touch){ //se entrambi i flag sono false si sta giocando col gamepad
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
                if(!attivato){
                    startTime = System.currentTimeMillis();
                    attivato = true;
                    difference = 0;
                }
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
                if(boss){
                    AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                    alertDialog.setTitle( R.string.vittoria );
                    alertDialog.setMessage( getString(R.string.messaggio_vittoria_boss) );
                    alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.commands_not_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.show();
                    start = false;
                }else if(tempo){
                    difference = System.currentTimeMillis() - startTime;
                    double min = difference / (1000 * 60);
                    minuti = Math.floor(min);
                    double sec = (difference - (minuti*60000)) / 1000;
                    secondi = Math.floor(sec);
                    double dec = (sec - secondi)*10;
                    decimi = Math.floor(dec);
                    double cen = (dec - decimi)*10;
                    centesimi = Math.floor(cen);
                    double mill = (cen - centesimi)*10;
                    millesimi = Math.floor(mill);
                    AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                    alertDialog.setTitle( R.string.vittoria_tempo );
                    alertDialog.setMessage( getString(R.string.messaggio_partita_tempo)  + minuti + ":" + secondi + ":" + decimi + ":" + centesimi + ":" + millesimi);
                    alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.commands_not_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.show();
                    start = false;
                }else if(tema){
                    AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                    alertDialog.setTitle( R.string.vittoria_tempo );
                    alertDialog.setMessage( getString(R.string.messaggio_partita_tema) );
                    alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.commands_not_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                }
                            } );
                    alertDialog.show();
                    start = false;
                }
                else{
                    ++level;
                    resetLevel(level,buttonValue);
                    // ball.increaseSpeed(level);
                    start = false;
                }

            }
        }
    }

}
