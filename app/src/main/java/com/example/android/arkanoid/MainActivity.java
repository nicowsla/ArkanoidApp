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
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;

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
    private SoundPlayer soundPlayer;

    private long bestScore = 0;
    private long bestTime = 0;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mantiene il display acceso durante il gioco
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //introduce il suono
        soundPlayer = new SoundPlayer(this);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        enableTouch = pref.getBoolean("touch", true);
        enableAccelerometer = pref.getBoolean("accelerometro", false);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //prendo l'id per capire quale tasto è stato scelto
        //Credo basti solo uno
        Bundle i = getIntent().getExtras();
        int partita = i.getInt("M");


        //sets the screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get device resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        DatabaseReference myRef = database.getReference("utenti").child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                bestScore = dataSnapshot.child("bestScore").getValue(Long.class);
                bestTime =  dataSnapshot.child("bestTime").getValue(Long.class);
                username = dataSnapshot.child("username").getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        // create a new game
        game = new Game(this, 3, 0, 1, screenWidth, screenHeight, partita);
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
        onPause();
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
                        onResume();
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
        private long score;
        private int level;
        private boolean start;
        private boolean gameOver;
        private Context context;

        private int screenWidth;
        private int screenHeight;
        private int partita;

        private int buttonValue;
        private boolean boss = false;
        private boolean infinityMode = false;
        private boolean timeMode = false;
        private boolean themeMode = false;
        private boolean attivato = false;

        private boolean accelerometro = enableAccelerometer;
        private boolean touch = enableTouch;

        private long startTime;
        private long difference;
        private long minuti;
        private long secondi;
        private long decimi;
        private long centesimi;
        private long millesimi;



        public Game(Context context, int lifes, int score, int level, int screenWidth, int screenHeight, int partita) {
            super(context);
            paint = new Paint();

            // continue context, lifes, score a level
            this.context = context;
            this.lifes = lifes;
            this.score = score;
            this.level = level;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.partita = partita;


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


            buttonValue = partita;
            generateBricks(context, level, buttonValue);

            this.setOnTouchListener(this);

        }

        //fills the list with bricks
        private void generateBricks(Context context, int level, int button) {

            //LIVELLO MOSTRO CLASSIFICATA
            if(button == 2){
                timeMode = true;
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
                                themeMode = true;
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
                int numero;
                infinityMode = true;
                //System.out.println(NumeroLivello);
                if(level >= 17) {

                    if (!gameOver && infinityMode) {
                        Toast.makeText(MainActivity.this, "PARTITA INFINITA...", Toast.LENGTH_LONG).show();
                        for (int i = 3; i < 20; i++) {
                            for (int j = 1; j < 10; j++) {
                                numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
                                list.add(new Brick(context, (size.x / 11) * j, (i * 70 * screenHeight) / screenHeight, numero));
                            }
                        }
                    }
                }else{
                    for (int i = 3; i < level+3; i++) {
                        for (int j = 1; j < 10; j++) {
                            numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * size.y) / screenHeight, numero));
                        }
                    }
                }
            }
            else if(button == 5){
                SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
                String matrixString = pref.getString("matrixString", null);
                Integer[][] personalMatrix = convertStringToArray(matrixString);

                for (int i = 3; i < 20; i++) {
                    for (int j = 1; j < 10; j++) {
                        if (personalMatrix[i][j] != 0) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, personalMatrix[i][j]));
                        }
                    }
                }
            }

        }

        public Integer[][] convertStringToArray(String matrix){
            return Arrays.stream(matrix.split(";"))
                    .map(s ->
                            Arrays.stream(s.split(","))
                                    .map(Integer::parseInt)
                                    .toArray(Integer[]::new)
                    )
                    .toArray(Integer[][]::new);
        }

        // set background
        private void readBackground(Context context) {
            background = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score));
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            size = new Point();
            display.getSize(size);
        }

        public Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
            float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
            int width = Math.round((float) ratio * realImage.getWidth());
            int height = Math.round((float) ratio * realImage.getHeight());
            Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
            return newBitmap;
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

            Bitmap icon_level = BitmapFactory.decodeResource(this.getResources(), R.drawable.up_arrows);
            Bitmap icon_lives = BitmapFactory.decodeResource(this.getResources(), R.drawable.heart);
            Bitmap icon_score = BitmapFactory.decodeResource(this.getResources(), R.drawable.high_score);

            int maxSize = (int) screenWidth/18;

            Bitmap new_icon_level = scaleDown(icon_level, maxSize, true);
            Bitmap new_icon_lives = scaleDown(icon_lives, maxSize, true);
            Bitmap new_icon_score = scaleDown(icon_score, maxSize, true);

            canvas.drawBitmap(new_icon_level, (size.x/6) - (maxSize+5), 50, paint);
            canvas.drawText("" + level,(size.x/6),100, paint );

            canvas.drawBitmap(new_icon_lives, (size.x/6)*3 - (maxSize+5), 50, paint);
            canvas.drawText("" + lifes, (size.x/6)*3, 100, paint);

            canvas.drawBitmap(new_icon_score, (size.x/6)*5 - (maxSize+5), 50, paint);
            canvas.drawText("" + score, (size.x/6)*5, 100, paint);

            //PROVE ##############################################################
            canvas.drawText("xpaddle:"+ paddle.getX(),50,250, paint );

            //in case of loss draw "Game over!"
            if (gameOver) {
                if(infinityMode && score>bestScore){
                    database.getReference("utenti").child(user.getUid()).child( "bestScore" ).setValue( score );
                    database.getReference("punteggi").child(user.getUid()).child("bestScore").setValue(score);
                    database.getReference("punteggi").child(user.getUid()).child( "username" ).setValue( username );
                    database.getReference("punteggi").child(user.getUid()).child( "userID" ).setValue( user.getUid() );
                }

                paint.setColor(Color.RED);
                paint.setTextSize(100);
                canvas.drawText("Game over!", size.x / 2, size.y / 2, paint);
                level = 1;
                infinityMode = false;
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
                if(timeMode){
                    difference = System.currentTimeMillis() - startTime;
                    long min = difference / (1000 * 60);
                    minuti = min;
                    long sec = (difference - (min*60000)) / 1000;
                    secondi = sec;
                    long dec = (difference - (min*60000) - (sec*1000)) / 100;
                    decimi = dec;
                    long cen = (difference - (min*60000) - (sec*1000) - (dec*100))/10;
                    centesimi = cen;
                    long mill = (difference - (min*60000) - (sec*1000) - (dec*100) - (cen*10));
                    millesimi = mill;
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
                        soundPlayer.playHitSound();
                        score = score + 50; //PUNTEGGIO SE ROMPI UN MATTONCINO
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

            if(accelerometro) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    paddle.setX(paddle.getX() - event.values[0] - event.values[0]);

                    //LA DIMENSIONE DELLO SCHERMO IN LARGHEZZA VA DA 35 A 235 CON I BORDI DELLO SFONDO ORIGINALE MENTRE DA 0 A 200 SENZA BORDI
                    if (paddle.getX() + event.values[0] > size.x - (200*screenWidth)/1080) {
                        paddle.setX(size.x - (200*screenWidth)/1080);
                    } else if (paddle.getX() - event.values[0] <= (0*screenWidth)/1080) {
                        paddle.setX((0*screenWidth)/1080);
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

                //LA DIMENSIONE DELLO SCHERMO IN LARGHEZZA VA DA 35 A 235 CON I BORDI DELLO SFONDO ORIGINALE MENTRE DA 0 A 200 SENZA BORDI
            }else if(start && !gameOver && !accelerometro && touch) { //flag accelerometro deve essere false e touch true
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        paddle.setX(event.getRawX() - (100*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                        if ((event.getRawX()  - (100*screenWidth)/1080) > size.x - (200*screenWidth)/1080) {
                            paddle.setX(size.x - (200*screenWidth)/1080);
                        } else if ((event.getRawX()  - (100*screenWidth)/1080) <= (0*screenWidth)/1080) {
                            paddle.setX((0*screenWidth)/1080);
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        paddle.setX(event.getRawX() - (100*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                        if ((event.getRawX()  - (100*screenWidth)/1080) > size.x - (200*screenWidth)/1080) {
                            paddle.setX(size.x - (200*screenWidth)/1080);
                        } else if ((event.getRawX()  - (100*screenWidth)/1080) <= (0*screenWidth)/1080) {
                            paddle.setX((0*screenWidth)/1080);
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        paddle.setX(event.getRawX() - (100*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                        if ((event.getRawX()  - (100*screenWidth)/1080) > size.x - (200*screenWidth)/1080) {
                            paddle.setX(size.x - (200*screenWidth)/1080);
                        } else if ((event.getRawX()  - (100*screenWidth)/1080) <= (0*screenWidth)/1080) {
                            paddle.setX((0*screenWidth)/1080);
                        }
                        return true;
                }
            }else if(start && !gameOver && !accelerometro && !touch){ //se entrambi i flag sono false si sta giocando col gamepad
                float x = event.getRawX();
                float x_paddle = paddle.getX();

                //LA DIMENSIONE DELLO SCHERMO IN LARGHEZZA VA DA 35 A 235 CON I BORDI DELLO SFONDO ORIGINALE MENTRE DA 0 A 200 SENZA BORDI
                if(x < (screenWidth/2) && x_paddle > 0){ //90
                    paddle.setX(paddle.getX() - ((40*screenWidth)/1080)); //100, è il valore di quanto si sposta la barra
                    if(x > size.x - (200*screenWidth)/1080){
                        x_paddle += (50*screenWidth)/1080;
                    }
                }else if(x > (screenWidth/2) && x_paddle < (screenWidth - ((200*screenWidth)/1080))){ //280
                    paddle.setX(paddle.getX() + ((40*screenWidth)/1080)); //100, è il valore di quanto si sposta la barra
                    if(x > size.x - (200*screenWidth)/1080){
                        x_paddle -= (50*screenWidth)/1080;
                    }
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
                }else if(timeMode){
                    difference = System.currentTimeMillis() - startTime;
                    long min = difference / (1000 * 60);
                    minuti = min;
                    long sec = (difference - (min*60000)) / 1000;
                    secondi = sec;
                    long dec = (difference - (min*60000) - (sec*1000)) / 100;
                    decimi = dec;
                    long cen = (difference - (min*60000) - (sec*1000) - (dec*100))/10;
                    centesimi = cen;
                    long mill = (difference - (min*60000) - (sec*1000) - (dec*100) - (cen*10));
                    millesimi = mill;
                    if(difference<bestTime){
                        database.getReference("utenti").child(user.getUid()).child( "bestTime" ).setValue( difference );
                        database.getReference("punteggi").child(user.getUid()).child( "bestTime" ).setValue( difference );
                        database.getReference("punteggi").child(user.getUid()).child( "username" ).setValue( username );
                        database.getReference("punteggi").child(user.getUid()).child( "userID" ).setValue( user.getUid() );

                    }
                    AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                    alertDialog.setTitle( R.string.vittoria_tempo );
                    alertDialog.setMessage( getString(R.string.messaggio_partita_tempo)  + minuti + "'" + secondi + "''" + decimi + centesimi + millesimi);
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
                }else if(themeMode){
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
                    soundPlayer.playOverSound();
                    resetLevel(level,buttonValue);
                    // ball.increaseSpeed(level);
                    start = false;
                }

            }
        }
    }

}
