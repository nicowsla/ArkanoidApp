package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_levels);

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

        Intent i = new Intent(ThemeLevelsActivity.this, MainActivity.class);
        i.putExtra("MODE", 1);
        i.putExtra("Multiplayer", false);


        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 1);
                startActivity(i);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 2);
                startActivity(i);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 3);
                startActivity(i);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 4);
                startActivity(i);
            }
        });

        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 5);
                startActivity(i);
            }
        });

        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 6);
                startActivity(i);
            }
        });

        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 7);
                startActivity(i);
            }
        });

        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 8);
                startActivity(i);
            }
        });

        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 9);
                startActivity(i);
            }
        });

        img10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("Level", 10);
                startActivity(i);
            }
        });
    }


}