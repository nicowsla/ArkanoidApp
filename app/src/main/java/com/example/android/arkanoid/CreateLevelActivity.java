package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;



public class CreateLevelActivity extends AppCompatActivity {
    private View view;
    private int width;
    private int height;
    private int x;
    private int y;
    private int i;
    private int j;

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
            //editableMatrix[i][j] = 123;
            System.out.println("coordinate: I="+i+"    J="+j );
            return true;
        }
    };

    //fare una funzione attivata dal bottone di conferma che salva la matrice nel db!!!



}