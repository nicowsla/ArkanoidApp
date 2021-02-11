package com.example.android.arkanoid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android.arkanoid.entity.Ball;
import com.example.android.arkanoid.entity.Challenge;
import com.example.android.arkanoid.entity.Paddle;
import com.example.android.arkanoid.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import com.example.android.arkanoid.Brick;
import com.example.android.arkanoid.Coordinate;

public class MainActivity extends AppCompatActivity {

    public  static final int IDEAL_WIDTH=1080;
    public  static final int IDEAL_HEIGHT=1920;
    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;
    private Boolean enableTouch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private Boolean enableAccelerometer;
    private SoundPlayer soundPlayer;
    private long bestScore = 0;
    private long bestTime = 0;
    private String username;
    private String friend;
    private String friendUsername;
    private String idRequest;
    private Long friendScore;
    private int levArcade;
    private int levTheme;
    private Boolean guestMode = false;
    private Boolean multiplayer;
    private Boolean sfidante = false;
    private Boolean sfidato = false;
    private Boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        //creo la action bar dinamica che si modifica in base alla modalità
        ActionBar actionBar = getSupportActionBar();

        //mantiene il display acceso durante il gioco
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //introduce il suono
        soundPlayer = new SoundPlayer(this);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        enableTouch = pref.getBoolean("touch", true);
        enableAccelerometer = pref.getBoolean("accelerometro", false);
        friend = pref.getString("friend", null);
        friendUsername = pref.getString("friendName", null);
        idRequest = pref.getString("idRichiesta", null);
        //int levTheme = pref.getInt("livTheme",1);
        levArcade = pref.getInt("livArcade", 1);
        levTheme = pref.getInt("livTheme", 1);
        guestMode = pref.getBoolean("guest", false);
        String s = pref.getString("friendScore", null);
        if(s!=null){
            friendScore = Long.parseLong(s)*(-1);
        }

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //sets the screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get device resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if(!guestMode){
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            Bundle i = getIntent().getExtras();
            int partita = i.getInt("MODE");

            if(actionBar != null) {
                switch (partita) {
                    case 1:
                        actionBar.setTitle(R.string.label_theme_mode);
                        break;
                    case 2:
                        actionBar.setTitle(R.string.label_time_mode);
                        break;
                    case 3:
                        actionBar.setTitle(R.string.label_arcade_mode);
                        break;
                    case 4:
                        actionBar.setTitle(R.string.label_infinity_home);
                        break;
                    case 6:
                        actionBar.setTitle(R.string.label_landscape_mode);
                        break;
                }
            }

            multiplayer = i.getBoolean("Multiplayer");
            if(multiplayer){
                sfidante = i.getBoolean("Sfidante");
                sfidato = i.getBoolean("Sfidato");
            }
            int level = i.getInt("Level");
            DatabaseReference myRef = database.getReference("utenti").child(user.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    bestScore = dataSnapshot.child("bestScore").getValue(Long.class)*(-1);
                    bestTime =  dataSnapshot.child("bestTime").getValue(Long.class);
                    username = dataSnapshot.child("username").getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });

            game = new Game(this, 3, 0, level, screenWidth, screenHeight, partita, multiplayer, sfidante, sfidato);

        }else{
            game = new Game(this, 3, 0, 1, screenWidth, screenHeight, 3, false, false, false);

        }

        // create a new game
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

    //MANCA VOCE GAMEPAD
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        pause=true;
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
                                    pause=false;
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
                                    pause=false;
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
                                    pause=false;
                                    Toast.makeText(MainActivity.this, getString(R.string.gamepad_selected), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        builder.show();

                    } else if(items[i].equals(getString(R.string.exit))){
                        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                        alertDialog.setTitle( R.string.attention );
                        alertDialog.setMessage( getString(R.string.exit_confirm) );
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        if(guestMode){
                                            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
                                        }else if(game.themeMode){
                                            startActivity( new Intent( MainActivity.this, ThemeLevelsActivity.class ) );
                                        }else {
                                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                        }
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
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if(guestMode){
                            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
                        }else if(game.themeMode){
                            startActivity( new Intent( MainActivity.this, ThemeLevelsActivity.class ) );
                        }else {
                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        }
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
        private Bitmap backgroundGamepad;
        private Bitmap backgroundGamepadLandscape;
        private Bitmap redBall;
        private Bitmap stretchedOut;
        private Bitmap stretchedOutGamepad;
        private Bitmap paddle_p;
        private Bitmap new_paddle;
        private Display display;
        private Point size;
        private Paint paint;
        private Paint paintGameOver;

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
        private final Context context;

        private int screenWidth;
        private int screenHeight;
        private int partita;
        private Boolean multiplayer;
        private Boolean sfidante;
        private Boolean sfidato;

        private int buttonValue;
        private boolean boss = false;
        private boolean infinityMode = false;
        private boolean timeMode = false;
        private boolean themeMode = false;
        private boolean arcadeMode = false;
        private boolean landscape = false;
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

        private int paddle_width = 200;
        private int paddle_height = 40;

        Bitmap icon_level;
        Bitmap icon_lives;
        Bitmap icon_score;
        Bitmap icon_time ;

        int maxSize;

        Bitmap new_icon_level;
        Bitmap new_icon_lives;
        Bitmap new_icon_score;
        Bitmap new_icon_time;

        public Game(Context context, int lifes, int score, int level, int screenWidth, int screenHeight, int partita, Boolean multiplayer, Boolean sfidante, Boolean sfidato) {
            super(context);
            paint = new Paint();
            paintGameOver = new Paint();

            // continue context, lifes, score a level
            this.context = context;
            this.lifes = lifes;
            this.score = score;
            this.level = level;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.partita = partita;
            this.multiplayer = multiplayer;
            this.sfidante = sfidante;
            this.sfidato = sfidato;

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
            Bitmap originalBall =  BitmapFactory.decodeResource(getResources(), R.drawable.sfera);
            redBall = scaleDown(originalBall, 60, true);
            paddle_p = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);

            new_paddle = Bitmap.createScaledBitmap(paddle_p,(paddle_width*screenWidth)/1080,(paddle_height*screenHeight)/1920,true);

            // creates a new ball, paddle, and list of bricks

            ball = new Ball(size.x / 2, size.y - (470*screenHeight)/1920, level);
            paddle = new Paddle((size.x / 2) - (100*screenWidth)/1080, size.y - (400*screenHeight)/1920);
            list = new ArrayList<Brick>();

            icon_level = BitmapFactory.decodeResource(this.getResources(), R.drawable.up_arrows);
            icon_lives = BitmapFactory.decodeResource(this.getResources(), R.drawable.heart);
            icon_score = BitmapFactory.decodeResource(this.getResources(), R.drawable.high_score);
            icon_time = BitmapFactory.decodeResource(this.getResources(), R.drawable.stopwatch);

            buttonValue = partita;
            switch(buttonValue){
                case 1:
                    themeMode = true;
                    break;
                case 2:
                    timeMode = true;
                    break;
                case 3:
                    arcadeMode = true;
                    break;
                case 4:
                    infinityMode = true;
                    break;
                case 6:
                    landscape = true;
                    break;
            }
            generateBricks(context, level, buttonValue);

            if(landscape){
                maxSize = (int) screenWidth/36;
            }else{
                maxSize = (int) screenWidth / 18;
            }
            new_icon_level = scaleDown(icon_level, maxSize, true);
            new_icon_lives = scaleDown(icon_lives, maxSize, true);
            new_icon_score = scaleDown(icon_score, maxSize, true);
            new_icon_time = scaleDown(icon_time, maxSize, true);


            this.setOnTouchListener(this);

        }

        //fills the list with bricks
        private void generateBricks(Context context, int level, int button) {

            //LIVELLO MOSTRO CLASSIFICATA
            if(button == 2){
                for (int i = 3; i < 20; i++) {
                    for (int j = 1; j < 10; j++) {
                        if (Levels.LivelloMOSTRO[i][j] != 0) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, Levels.LivelloMOSTRO[i][j]));
                        }
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.attention);
                builder.setMessage(getString(R.string.timeMode_info_dialog));
                AlertDialog dialog = builder.show();
                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                dialog.show();
            }else if(button == 1){              //PARTITE A TEMA
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
                int numero;
                if(level == 17) {
                    boss = true;
                    if (!gameOver && boss) {
                        Toast.makeText(MainActivity.this, getString(R.string.boss_coming), Toast.LENGTH_LONG).show();
                        for (int i = 3; i < 20; i++) {
                            for (int j = 1; j < 10; j++) {
                                if (Levels.LivelloMOSTRO[i][j] != 0) {
                                    list.add(new Brick(context, (size.x / 11) * j, (i * 70 * screenHeight) / screenHeight, Levels.LivelloMOSTRO[i][j]));
                                }
                            }
                        }
                    }
                }else{
                    boss=false;
                    for (int i = 3; i < level+3; i++) {
                        for (int j = 1; j < 10; j++) {
                            numero = 1 + (int)(Math.random() * ((10 - 1) + 1));
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * size.y) / screenHeight, numero));
                        }
                    }
                }

                //MODALITA INFINITA
            }else if(button == 4){
                int numero;
                if(level >= 17) {
                    if (!gameOver && infinityMode) {
                        //Toast.makeText(MainActivity.this, getString(R.string.infinity_mode), Toast.LENGTH_LONG).show();
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
            }else if(button == 5){ //crea livello
                SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
                String matrixString = pref.getString("matrixString", null);
                System.out.println(matrixString);
                Integer[][] personalMatrix = convertStringToArray(matrixString);

                for (int i = 3; i < 20; i++) {
                    for (int j = 1; j < 10; j++) {
                        if (personalMatrix[i][j] != 0) {
                            list.add(new Brick(context, (size.x/11)*j, (i * 70 * screenHeight) / screenHeight, personalMatrix[i][j]));
                        }
                    }
                }
            }else if(button == 6){ //modalità landscape
                for (int i = 2; i < 8; i++) { //6*15
                    for (int j = 3; j < 18; j++) {
                        switch (level){
                            case 1:
                                if (Levels.Livello1LANDSCAPE[i][j] != 0) {
                                    list.add(new Brick(context, (screenHeight/11) * j, (i * 70), Levels.Livello1LANDSCAPE[i][j]));
                                }
                                break;
                            case 2:
                                if (Levels.Livello2LANDSCAPE[i][j] != 0) {
                                    list.add(new Brick(context, (screenHeight/11) * j, (i * 70), Levels.Livello2LANDSCAPE[i][j]));
                                }
                                break;
                            case 3:
                                if (Levels.Livello3LANDSCAPE[i][j] != 0) {
                                    list.add(new Brick(context, (screenHeight/11) * j, (i * 70), Levels.Livello3LANDSCAPE[i][j]));
                                }
                                break;
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

            backgroundGamepad = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score_gamepad));
            backgroundGamepadLandscape = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_score_gamepad_landscape));
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
            return Bitmap.createScaledBitmap(realImage, width, height, filter);
        }

        protected void onDraw(Canvas canvas) {

            if(landscape){
                if (stretchedOut == null ) {
                    stretchedOut = Bitmap.createScaledBitmap(background, size.x, size.y, true);
                }
                if (stretchedOutGamepad == null ) {
                    stretchedOutGamepad = Bitmap.createScaledBitmap(backgroundGamepadLandscape, size.x, size.y, true);
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                int a = screenWidth;
                screenWidth = screenHeight;
                screenWidth = a;

                //Posiziona lo sfondo nello schermo
                if(!accelerometro && !touch){
                    canvas.drawBitmap(stretchedOutGamepad, 0, 0, null);
                }else {
                    canvas.drawBitmap(stretchedOut, 0, 0, null);
                }

                // draw the ball
                paint.setColor(Color.RED);
                //canvas.drawCircle(ball.getX(), ball.getY(), 30, paint);
                canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint);

                // draw fell, disegna rettangolo cioè barra
                paint.setColor(Color.WHITE);
                //La riga sotto era +200 + 40
                //r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + (200*screenWidth)/1080, paddle.getY() + (40*screenHeight)/1920);
               // canvas.drawRect(paddle.getX(), paddle.getY(), paddle.getX() + (200*screenWidth)/1080, paddle.getY() + (40*screenHeight)/1920, paint);
                canvas.drawBitmap(new_paddle, paddle.getX(), paddle.getY(), paint);
                // draw bricks
                paint.setColor(Color.GREEN);
                for (int i = 0; i < list.size(); i++) {
                    Brick b = list.get(i);
                    //r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/screenWidth, b.getY() + (70*screenHeight)/screenHeight) ;
                    canvas.drawBitmap(b.getBrick(), null, new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/1920, b.getY() + (100*screenHeight)/2880) , paint);
                }

                // draw text and icons
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);

                canvas.drawBitmap(new_icon_level, size.x/6, 50 - 48, paint);
                canvas.drawText("" + level,(size.x/6) + 60,100 - 50, paint );
                canvas.drawBitmap(new_icon_lives, (size.x/6)*3 - 50, 50 - 45, paint);
                canvas.drawText("" + lifes, (size.x/6)*3 + 20, 100 - 50, paint);
                canvas.drawBitmap(new_icon_score, (size.x/6)*5 - 100, 50 - 45, paint);
                canvas.drawText("" + score, (size.x/6)*5 - 40, 100 - 50, paint);

                //in case of loss draw "Game over!"
                if (gameOver) {
                    Bitmap gameovericon = BitmapFactory.decodeResource(this.getResources(), R.drawable.gameover);
                    canvas.drawBitmap(gameovericon, (canvas.getWidth() - gameovericon.getWidth()) / 2, (canvas.getHeight() - gameovericon.getHeight())/ 2, null);
                    level = 1;
                    //infinityMode = false;
                    startTime = 0;
                    attivato = false;
                }
            }else {
                    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (stretchedOut == null) {
                        stretchedOut = Bitmap.createScaledBitmap(background, size.x, size.y, true);
                    }
                    if (stretchedOutGamepad == null) {
                        stretchedOutGamepad = Bitmap.createScaledBitmap(backgroundGamepad, size.x, size.y, true);
                    }

                    //Posiziona lo sfondo nello schermo
                    if (!accelerometro && !touch) {
                        canvas.drawBitmap(stretchedOutGamepad, 0, 0, null);
                    } else {
                        canvas.drawBitmap(stretchedOut, 0, 0, null);
                    }

                    // draw the ball
                    paint.setColor(Color.RED);


                    canvas.drawBitmap(redBall, ball.getX(), ball.getY(), paint);
                    //canvas.drawCircle(ball.getX(), ball.getY(), 30, paint);

                    // draw fell, disegna rettangolo cioè barra
                    paint.setColor(Color.WHITE);
                    //La riga sotto era +200 + 40
                    //r = new RectF(paddle.getX(), paddle.getY(), paddle.getX() + (200*screenWidth)/1080, paddle.getY() + (40*screenHeight)/1920);
                   // canvas.drawRect(paddle.getX(), paddle.getY(), paddle.getX() + (paddle_width * screenWidth) / 1080, paddle.getY() + (paddle_height * screenHeight) / 1920, paint);
                canvas.drawBitmap(new_paddle, paddle.getX(), paddle.getY(), paint);
                //canvas.drawBitmap(new_paddle, paddle.getX(), paddle.getY(), paint);
                    // draw bricks
                    paint.setColor(Color.GREEN);
                    for (int i = 0; i < list.size(); i++) {
                        Brick b = list.get(i);
                        //r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/screenWidth, b.getY() + (70*screenHeight)/screenHeight) ;
                        //canvas.drawBitmap(b.getBrick(), null, new RectF(b.getX(), b.getY(), b.getX() + (100 * screenWidth) / 1080, b.getY() + (70 * screenHeight) / 1920), paint);
                        Bitmap brick =    scaleDown(b.getBrick(), (100 * screenWidth) / 1080, true);
                        canvas.drawBitmap(brick, b.getX(), b.getY(), paint);
                    }

                    // draw text
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);



                    if (timeMode) {
                        difference = System.currentTimeMillis() - startTime;
                        minuti = difference / (1000 * 60);
                        secondi = (difference - (minuti * 60000)) / 1000;
                        decimi = (difference - (minuti * 60000) - (secondi * 1000)) / 100;
                        centesimi = (difference - (minuti * 60000) - (secondi * 1000) - (decimi * 100)) / 10;
                        millesimi = (difference - (minuti * 60000) - (secondi * 1000) - (decimi * 100) - (centesimi * 10));
                        canvas.drawBitmap(new_icon_time, (size.x / 6) * 4 - 40 - (maxSize + 5), 50, paint);
                        canvas.drawBitmap(new_icon_lives, (size.x / 6) * 2 - (maxSize + 5), 50, paint);
                        canvas.drawText("" + lifes, (size.x / 6) * 2, 100, paint);
                        if(!start && !gameOver && !pause){
                            canvas.drawText(0 + "'" + 0 + "''" + 0 + 0 + 0, (size.x / 6) * 4 - 40, 100, paint);
                        }else {
                            canvas.drawText(minuti + "'" + secondi + "''" + decimi + centesimi + millesimi, (size.x / 6) * 4 - 40, 100, paint);
                        }
                    } else {
                        if(!infinityMode){
                            canvas.drawBitmap(new_icon_lives, (size.x / 6) * 4 - (maxSize + 5), 50, paint);
                            canvas.drawText("" + lifes, (size.x / 6) * 4, 100, paint);
                            canvas.drawBitmap(new_icon_level, (size.x / 6)*2 - (maxSize + 5), 50, paint);
                            canvas.drawText("" + level, (size.x / 6)*2, 100, paint);
                        } else{
                            canvas.drawBitmap(new_icon_lives, (size.x / 6) * 3 - (maxSize + 5), 50, paint);
                            canvas.drawText("" + lifes, (size.x / 6) * 3, 100, paint);
                            canvas.drawBitmap(new_icon_level, (size.x / 6) - (maxSize + 5), 50, paint);
                            canvas.drawText("" + level, (size.x / 6), 100, paint);
                            canvas.drawBitmap(new_icon_score, (size.x / 6) * 5 - (maxSize + 5), 50, paint);
                            canvas.drawText("" + score, (size.x / 6) * 5, 100, paint);
                        }

                    }

                    //in case of loss draw "Game over!"
                    if (gameOver) {
                        paddle_width = 200;

                        if (infinityMode && score > bestScore) {
                            database.getReference("utenti").child(user.getUid()).child("bestScore").setValue(score*(-1));
                            database.getReference("punteggi").child(user.getUid()).setValue(new User(user.getUid(), username, user.getEmail(), score*(-1), bestTime, levArcade, levTheme, new Coordinate(0,0)));
                        }

                            Bitmap gameovericon = BitmapFactory.decodeResource(this.getResources(), R.drawable.gameover);
                            canvas.drawBitmap(gameovericon, (canvas.getWidth() - gameovericon.getWidth()) / 2, (canvas.getHeight() - gameovericon.getHeight())/ 2, null);
                            if(!arcadeMode && !themeMode){
                                level = 1;
                            }
                            if(!multiplayer){
                                boss = false;
                                startTime = 0;
                                attivato = false;
                        }
                    }
                }
            }
    
            //check that the ball has not touched the edge
            private void chechEdges() {
                if (ball.getX() + ball.getxSpeed() >= size.x - (60*screenWidth)/1080) {
                    ball.changeDirection("rights");
                } else if (ball.getX() + ball.getxSpeed() <= (0*screenWidth)/1080) {
                    ball.changeDirection("left");
                } else if (ball.getY() + ball.getySpeed() <= (180*screenHeight)/1920) {
                    ball.changeDirection("up");
                } else if (ball.getY() + ball.getySpeed() >= size.y - (200*screenHeight)/1920) {
                    checkLives();
                }
            }
    
            //checks the status of the game. whether my lives or whether the game is over
            private void checkLives() {
                if (lifes == 1) {
                    soundPlayer.playGameOverSound();
                    if(multiplayer && sfidante){
                        onPause();
                       DatabaseReference myRef = database.getReference("utenti").child(user.getUid()).child("RichiesteSfidaEffettuate").push();
                        String key = myRef.getKey();
                        myRef.setValue(new Challenge(key, friend, friendUsername, (long) 0, score*(-1)));
    
                        DatabaseReference myRef1 = database.getReference("utenti").child(friend).child("RichiesteSfidaRicevute").child(key);
                        myRef1.setValue(new Challenge(key, user.getUid(), username, score*(-1), (long) 0));
    
                        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                        alertDialog.setTitle( R.string.wait_for_friend );
                        alertDialog.setMessage( getString(R.string.wait) );
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                    }
                                } );
                        alertDialog.show();
                    }
                    if(multiplayer && sfidato){
                        onPause();
                        DatabaseReference myRef = database.getReference("utenti").child(user.getUid());
                        myRef.child("RichiesteSfidaRicevute").child(idRequest).removeValue();
    
                        DatabaseReference myRef1 = database.getReference("utenti").child(friend);
                        myRef1.child("RichiesteSfidaEffettuate").child(idRequest).removeValue();
    
    
                        DatabaseReference myRef2 = database.getReference("utenti").child(user.getUid()).child("Storico").push();
                        String key = myRef2.getKey();
                        myRef2.setValue(new Challenge(key, friend, friendUsername, friendScore*(-1), score*(-1)));
    
                        myRef1.child("Storico").child(key).setValue(new Challenge(key, user.getUid(), username, score*(-1), friendScore*(-1)));
    
                        if(score>friendScore){
                            AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                            alertDialog.setTitle( R.string.winner );
                            alertDialog.setMessage( getString(R.string.win) + getString(R.string.score_show_dialog) + score + "-" + friendScore);
                            alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                        }
                                    } );
                            alertDialog.show();
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                            alertDialog.setTitle( R.string.loser );
                            alertDialog.setMessage( getString(R.string.lost) + getString(R.string.score_show_dialog) + score + "-" + friendScore );
                            alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                        }
                                    } );
                            alertDialog.show();
                        }
                    }
                    if(arcadeMode && level==17 && !guestMode){
                        database.getReference("utenti").child(user.getUid()).child("livArcade").setValue(level);
                        SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                        editor.putInt( "livArcade", level );
                        editor.apply();
                    }

                    gameOver = true;
                    start = false;
                    if(!arcadeMode && !themeMode){
                        level = 1;
                    }
                    soundPlayer.playGameOverSound();
                    invalidate();

                } else {
                    lifes--;
                    ball.setX((size.x / 2) - (30*screenWidth)/1080);
                    ball.setY(size.y - (470*screenHeight)/1920);
                    ball.createSpeed(level);
                    start = false;
                }
            }
    
            //each step checks whether there is a collision, a loss or a win, etc.
            public void update() {
                if (start) {
                    win();
                    chechEdges();
                    ball.suddentlyPaddle(paddle.getX(), paddle.getY(), new_paddle, redBall);
                    for (int i = 0; i < list.size(); i++) {
                        Brick b = list.get(i);
                        if (ball.suddentlyBrick(b.getX(), b.getY(), scaleDown(b.getBrick(), (100 * screenWidth) / 1080, true), redBall)) {
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
                        if (paddle.getX() + event.values[0] > size.x - (paddle_width*screenWidth)/1080) {
                            paddle.setX(size.x - (paddle_width*screenWidth)/1080);
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
                            paddle.setX(event.getRawX() - ((paddle_width/2)*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                            if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) > size.x - (paddle_width*screenWidth)/1080) {
                                paddle.setX(size.x - (paddle_width*screenWidth)/1080);
                            } else if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) <= (0*screenWidth)/1080) {
                                paddle.setX((0*screenWidth)/1080);
                            }
                            invalidate();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            paddle.setX(event.getRawX() - ((paddle_width/2)*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                            if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) > size.x - (paddle_width*screenWidth)/1080) {
                                paddle.setX(size.x - (paddle_width*screenWidth)/1080);
                            } else if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) <= (0*screenWidth)/1080) {
                                paddle.setX((0*screenWidth)/1080);
                            }
                            invalidate();
                            return true;
                        case MotionEvent.ACTION_DOWN:
                            paddle.setX(event.getRawX() - ((paddle_width/2)*screenWidth)/1080); //quando tocco lo schermo il dito sarà al centro del paddle
                            if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) > size.x - (paddle_width*screenWidth)/1080) {
                                paddle.setX(size.x - (paddle_width*screenWidth)/1080);
                            } else if ((event.getRawX()  - ((paddle_width/2)*screenWidth)/1080) <= (0*screenWidth)/1080) {
                                paddle.setX((0*screenWidth)/1080);
                            }
                            return true;
                    }
                }else if(start && !gameOver && !accelerometro && !touch){ //se entrambi i flag sono false si sta giocando col gamepad
                    float y = event.getRawY();
                    if(y>(screenHeight/5)*4){
                        float x = event.getRawX();
                        float x_paddle = paddle.getX();
    
                        //LA DIMENSIONE DELLO SCHERMO IN LARGHEZZA VA DA 35 A 235 CON I BORDI DELLO SFONDO ORIGINALE MENTRE DA 0 A 200 SENZA BORDI
                        if(x < (screenWidth/2) && x_paddle > 0){ //90
                            paddle.setX(paddle.getX() - ((40*screenWidth)/1080)); //100, è il valore di quanto si sposta la barra
                            if(x > size.x - (paddle_width*screenWidth)/1080){
                                x_paddle += (50*screenWidth)/1080;
                            }
                        }else if(x > (screenWidth/2) && x_paddle < (screenWidth - ((paddle_width*screenWidth)/1080))){ //280
                            paddle.setX(paddle.getX() + ((40*screenWidth)/1080)); //100, è il valore di quanto si sposta la barra
                            if(x > size.x - (paddle_width*screenWidth)/1080){
                                x_paddle -= (50*screenWidth)/1080;
                            }
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
                    soundPlayer.playOverSound();
                    if(timeMode){
                        onPause();
                        difference = System.currentTimeMillis() - startTime;
                        minuti = difference / (1000 * 60);
                        secondi = (difference - (minuti*60000)) / 1000;
                        decimi = (difference - (minuti*60000) - (secondi*1000)) / 100;
                        centesimi = (difference - (minuti*60000) - (secondi*1000) - (decimi*100))/10;
                        millesimi = (difference - (minuti*60000) - (secondi*1000) - (decimi*100) - (centesimi*10));
    
                        if(difference<bestTime){
                            database.getReference("utenti").child(user.getUid()).child( "bestTime" ).setValue( difference );
                            database.getReference("punteggi").child(user.getUid()).setValue(new User(user.getUid(), username, user.getEmail(), bestScore*(-1), difference, levArcade, levTheme, new Coordinate(0,0)));
                        }
    
                        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this).create();
                        alertDialog.setTitle( R.string.vittoria_tempo );
                        alertDialog.setMessage( getString(R.string.messaggio_partita_tempo)  + minuti + "'" + secondi + "''" + decimi + centesimi + millesimi);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                });
                        alertDialog.show();
                        //start = false;
                    }else if(themeMode){
                        if(level==10) {
                            AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
                            alertDialog.setTitle( R.string.vittoria_tempo );
                            alertDialog.setMessage( getString(R.string.messaggio_partita_tema) );
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                    });
                            alertDialog.show();
                        }else{
                            level++;
                            if(level>levTheme){
                                database.getReference("utenti").child(user.getUid()).child("livTema").setValue(level);
                                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                editor.putInt( "livTheme", level );
                                editor.apply();
                            }
                            resetLevel(level,buttonValue);
                            start = false;
                        }
                    }else if(arcadeMode){
                        if(boss){
                            AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this).create();
                            alertDialog.setTitle( R.string.vittoria );
                            alertDialog.setMessage( getString(R.string.messaggio_vittoria_boss) );
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(guestMode){
                                                startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
                                            }else{
                                                startActivity( new Intent( MainActivity.this, MenuActivity.class ) );
                                            }
                                        }
                                    } );
                            alertDialog.show();
                            start = false;
                        }else {
                            level++;
                            if (level > levArcade && !guestMode) {
                                database.getReference("utenti").child(user.getUid()).child("livArcade").setValue(level);
                                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                editor.putInt("livArcade", level);
                                editor.apply();
                            }
                            if (level % 5 == 0) {         //SE ARRIVI AD UN LIVELLO MULTIPLO DI 5 IN ARCADE MODE
                                lifes++;                            //AGGIUNGE UNA VITA
                                paddle_width += 50;                 //AUMENTA LA LUNGHEZZA DEL PADDLE
                            }
                            //soundPlayer.playOverSound();
                            resetLevel(level, buttonValue);
                            start = false;
                        }
                    }else if(landscape && level==3) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle(R.string.vittoria_tempo);
                        alertDialog.setMessage(getString(R.string.messaggio_landscape));
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                    }
                                } );
                        alertDialog.show();
                        start = false;
                    }else{
                        level++;
                        resetLevel(level,buttonValue);
                        start = false;
                    }
                }
            }
        }
}
      