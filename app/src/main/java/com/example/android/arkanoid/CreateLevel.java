package com.example.android.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Locale;

public class CreateLevel extends Activity {

    private DrawingView dv ;
    private int i,j;
    private Bitmap originalBrick;
    private Bitmap brick;

    private int screenWidth;
    private int screenHeight;

    private int chosenColor = 17;

    int raw = 24;
    int raw1 = 25;

    int maxSize;

    Integer[][] matrix = new Integer[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this);
        setContentView(dv);

        //FORZO IMPOSTAZIONE LINGUA
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");

        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //mattoncino con dimensioni originali
        originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_red);
        //setta la larghezza del mattoncino
        maxSize= (int)screenWidth/9;
        //rimpicciolisce in scala le dimensioni del mattoncino
        brick = scaleDown(originalBrick, maxSize, true);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public class DrawingView extends View {

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Paint   mBitmapPaint;
        Context context;
        private Paint paint;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            paint = new Paint();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            h= screenHeight;
            w = screenWidth;
            Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.backgroud);
            Bitmap scaledBackground = Bitmap.createScaledBitmap(background, w, h, true);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas = new Canvas(mBitmap);

            mCanvas.drawBitmap(scaledBackground, 0, 0, null);
            int raws = (int) h/brick.getHeight();
            int dw = (int) mBitmap.getWidth() /9;
            int dh = (int)(mBitmap.getHeight()/raws);

            Bitmap box = BitmapFactory.decodeResource(this.getResources(), R.drawable.lilla);
            Bitmap scaledBox = Bitmap.createScaledBitmap(box, w, 16*dh, true);
            disegna(0, 4*dh, scaledBox);


            int color = ContextCompat.getColor(context, R.color.white);
            paint.setColor(color);
            paint.setTextSize(45);

            mCanvas.drawText(getString(R.string.select_the_color), 30, 2*dh, paint);
            mCanvas.drawText(getString(R.string.draw_bricks), 30, 3*dh, paint);
            mCanvas.drawText(getString(R.string.touch_the_color), 50, (raw -2)*dh, paint);
            mCanvas.drawText(getString(R.string.touch_here_to_continue), (screenWidth/2)-100, (raw +6)*dh, paint);

            //gomma da cancellare
            //  Bitmap ereaser = Bitmap.createScaledBitmap(box, brick.getWidth(), brick.getHeight(), true);
            // disegna(4*dw, (raw -2)*dh, ereaser);

            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_blue);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(0*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_green);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(1*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_orange);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(2*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_pink);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(3*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_purple);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(4*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_red);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(5*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_yellow);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(6*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_black);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(7*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_bordeaux);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(8*dw, raw*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_green2);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(0*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_white);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(1*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_bluscuro);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(2*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_carne);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(3*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_brown);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(4*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_aqua);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(5*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_grey);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(6*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_lite_grey);
            brick = scaleDown(originalBrick, maxSize, true);
            disegna(7*dw, raw1*dh, brick);
            originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.gomma);
            Bitmap scaledBrick = Bitmap.createScaledBitmap(originalBrick, brick.getWidth(), brick.getHeight(), true);
            //brick = scaleDown(scaledBrick, maxSize, true);
            disegna(8*dw, raw1*dh, scaledBrick);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        }

        public void disegna(int j, int i, Bitmap bitmap){
            mCanvas.drawBitmap(bitmap, j, i, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            float deltaWidth = mBitmap.getWidth() /9;
            j = (int) (x/deltaWidth);
            int raws = screenHeight/brick.getHeight();
            float deltaHeight = mBitmap.getHeight()/raws;
            i = (int) (y/deltaHeight);

            int a = (int)deltaWidth*j;
            int b = (int) deltaHeight*i;

            if(i>3 && i<20){//MODIFICA
                matrix[i][j+1]=chosenColor;
                disegna(a, b, brick);

            }else if (i ==raw || i==raw1){
                changeColor(i, j);
            }else if(i>raw1+2){

                // converto la matrice in stringa e la metto nelle shared preferences
                StringBuilder matrixString= new StringBuilder();
                int j=0;
                for(Integer[] riga: matrix){
                    int i=0;
                    if(j!=0){
                        matrixString.append(";");
                    }
                    for(Integer elemento: riga){
                        if(i==0){
                            matrixString.append(elemento);
                        }else{
                            matrixString.append(",").append(elemento);
                        }
                        i++;
                    }
                    j++;
                }

                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                editor.putString( "matrixString", matrixString.toString() );
                editor.apply();

                startActivity(new Intent(CreateLevel.this, CreateLevelActivity.class));

            }


            System.out.println("coordinate: I="+i+"    J="+j );

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    invalidate();
                    break;
            }
            return true;
        }
    }

    public void changeColor(int i, int j){
        if(i==raw){
            switch (j){
                case 0:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_blue);
                    chosenColor = 1;
                    Toast.makeText(CreateLevel.this,getString(R.string.blue_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_green);
                    chosenColor = 2;
                    Toast.makeText(CreateLevel.this,getString(R.string.green_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_orange);
                    chosenColor = 3;
                    Toast.makeText(CreateLevel.this,getString(R.string.orange_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_pink);
                    chosenColor = 4;
                    Toast.makeText(CreateLevel.this,getString(R.string.pink_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_purple);
                    chosenColor = 5;
                    Toast.makeText(CreateLevel.this,getString(R.string.purple_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_red);
                    chosenColor = 6;
                    Toast.makeText(CreateLevel.this,getString(R.string.red_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_yellow);
                    chosenColor = 7;
                    Toast.makeText(CreateLevel.this,getString(R.string.yellow_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_black);
                    chosenColor = 8;
                    Toast.makeText(CreateLevel.this,getString(R.string.black_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_bordeaux);
                    chosenColor = 9;
                    Toast.makeText(CreateLevel.this,getString(R.string.dark_red_chosen) , Toast.LENGTH_SHORT).show();
                    break;
            }
            brick = scaleDown(originalBrick, maxSize, true);

        }else if(i==raw1){
            switch (j){
                case 0:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_green2);
                    chosenColor = 10;
                    Toast.makeText(CreateLevel.this,getString(R.string.dark_grey_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_white);
                    chosenColor = 11;
                    Toast.makeText(CreateLevel.this,getString(R.string.white_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_bluscuro);
                    chosenColor = 12;
                    Toast.makeText(CreateLevel.this,getString(R.string.dark_blue_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_carne);
                    chosenColor = 13;
                    Toast.makeText(CreateLevel.this,getString(R.string.skin_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_brown);
                    chosenColor = 14;
                    Toast.makeText(CreateLevel.this,getString(R.string.brown_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_aqua);
                    chosenColor = 15;
                    Toast.makeText(CreateLevel.this,getString(R.string.aqua_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_grey);
                    chosenColor = 16;
                    Toast.makeText(CreateLevel.this,getString(R.string.grey_chosen) , Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    originalBrick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_lite_grey);
                    chosenColor = 17;
                    Toast.makeText(CreateLevel.this,getString(R.string.lite_grey_chosen) , Toast.LENGTH_SHORT).show();
                    break;
            }
            brick = scaleDown(originalBrick, maxSize, true);
        }
        if(i==raw1 && j==8){
            Bitmap box = BitmapFactory.decodeResource(this.getResources(), R.drawable.lilla);
            brick =  Bitmap.createScaledBitmap(box, brick.getWidth(), brick.getHeight(), true);
            chosenColor = 0;
            Toast.makeText(CreateLevel.this,getString(R.string.ereaser_chosen) , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        onPause();
        AlertDialog alertDialog = new AlertDialog.Builder( CreateLevel.this ).create();
        alertDialog.setTitle( R.string.attention );
        alertDialog.setMessage( getString(R.string.exit_confirm) );
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity( new Intent( CreateLevel.this, PersonalLevelsActivity.class ) );
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

}