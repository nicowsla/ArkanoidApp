package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PersonalLevelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_levels);

        //leggere dal db i livelli personali e convertire in matrice per poi fare andare in MainActivity e creare il gioco con quella matrice e speed
    }
}