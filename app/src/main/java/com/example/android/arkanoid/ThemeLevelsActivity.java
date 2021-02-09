package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;


public class ThemeLevelsActivity extends AppCompatActivity {
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img7;
    private ImageView img8;
    private ImageView img9;
    private ImageView img10;
    private List<ImageView> img = new ArrayList();
    private int level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_levels);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        level = pref.getInt("livTheme", 1);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);
        img7 = findViewById(R.id.img7);
        img8 = findViewById(R.id.img8);
        img9 = findViewById(R.id.img9);
        img10 = findViewById(R.id.img10);

        img.add(img1);
        img.add(img2);
        img.add(img3);
        img.add(img4);
        img.add(img5);
        img.add(img6);
        img.add(img7);
        img.add(img8);
        img.add(img9);
        img.add(img10);

        if(level<=10){
            for(int a = level; a<10; a++){
                img.get(a).setImageDrawable(getDrawable(R.drawable.locker));
            }
        }

        Intent i = new Intent(ThemeLevelsActivity.this, MainActivity.class);
        i.putExtra("MODE", 1);
        i.putExtra("Multiplayer", false);

        for(int a=0; a<level; a++ ){
            final int c = a+1;
            img.get(a).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i.putExtra("Level", c);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ThemeLevelsActivity.this, MenuActivity.class));
    }
}