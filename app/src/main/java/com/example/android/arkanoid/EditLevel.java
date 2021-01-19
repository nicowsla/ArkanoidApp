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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Logger;

public class EditLevel extends View  {

    private Paint paint;
    private ArrayList<Brick> list;
    private RectF r;
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private Integer[][] matrix;



    public EditLevel(Context context, int screenWidth, int screenHeight, Integer[][] matrix ) {
        super(context);
        paint = new Paint();
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.matrix = matrix ;
        list = new ArrayList<Brick>();
        generateBricks(context);

    }

    //fills the list with bricks
    private void generateBricks(Context context) {
        for (int i = 3; i < 15; i++) {
            for (int j = 1; j < 9; j++) {
                if(matrix[i][j]!=0) {
                    list.add(new Brick(context, (j * 100 * screenWidth) / 1080, (i * 70 * screenHeight) / 2340, matrix[i][j]));
                }
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        // draw bricks


        paint.setColor(Color.GREEN);
        for (int i = 0; i < list.size(); i++) {
            Brick b = list.get(i);
            r = new RectF(b.getX(), b.getY(), b.getX() + (100*screenWidth)/1080, b.getY() + (70*screenHeight)/2340) ;
            canvas.drawBitmap(b.getBrick(), null, r, paint);
        }

        // draw text
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        canvas.drawText("PCCIAN D sord" + 111, 100, 100, paint);

    }

}

