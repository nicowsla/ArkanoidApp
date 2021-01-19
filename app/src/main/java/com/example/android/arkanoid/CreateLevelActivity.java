
package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;


public class CreateLevelActivity extends AppCompatActivity{
    private View view;
    private int width;
    private int height;
    private int x;
    private int y;
    private int i;
    private int j;
    private EditLevel game;

    private ArrayList<Brick> list = new ArrayList<Brick>();
    int screenWidth;
    int screenHeight;

    Context contex = this;
    private UpdateThread myThread;
    private Handler updateHandler;




    private TextView livello;
    private TextView velocita;



    private int[][] editableMatrix= new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},};
    private Canvas canvas;
    Paint paint = new Paint();
    private Bitmap stretchedOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_level);

        view = findViewById(R.id.matrix);

        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    width = view.getWidth();
                    height = view.getHeight();

                }
            });
        }

        //non so se servano davvero
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
         screenWidth = displayMetrics.widthPixels;
         screenHeight = displayMetrics.heightPixels;



        view.setOnTouchListener( handleTouch);



    }

    //funzione trasforma il tocco (x,y) (pixel) in coordinate (i,j)
    private  View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //prendo coordinate del tocco
            x = (int) event.getX();
            y = (int) event.getY();
            float deltaWidth = width /9;
            j = (int) (x/deltaWidth);
            float deltaHeight = height /16;
            i = (int) (y/deltaHeight);
            editableMatrix[i][j] = 5; //qui va messo il numero corrispondente al colore delmattone desiderato
           // game = new EditLevel(contex, screenWidth, screenHeight, i, j);
            setContentView(game);
            System.out.println("coordinate: I="+i+"    J="+j );

            for(int a=0; a<16; a++){
                for(int b=0; b<9; b++){

                }
            }
            return true;
        }
    };

    //fare una funzione attivata dal bottone di conferma che salva la matrice nel db!!!


}