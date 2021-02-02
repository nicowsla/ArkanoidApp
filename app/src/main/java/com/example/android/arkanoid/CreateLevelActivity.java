package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateLevelActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private SeekBar seekBar;
    private TextView progress;
    String matrixString;
    int speed = 0;

    private TextInputLayout nameLayout;
    private EditText nameET;
    private String name;

    private boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_level2);

        nameLayout = findViewById(R.id.level_namec);
        nameET = findViewById(R.id.level_name);

        seekBar = findViewById(R.id.seekBar);
        progress = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        progress.setText("Speed: "+speed+"/"+seekBar.getMax());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        matrixString = pref.getString("matrixString", null);


        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                progress.setText("Speed: "+speed+"/"+seekBar.getMax());
            }
        });

        nameET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                nameLayout.setError( null );
                error = false;
            }
        } );

        nameET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameLayout.setError( null );
                error = false;
            }
        } );
    }


    public void saveLevel(View view) {
        name = nameET.getText().toString();
        if(name.isEmpty() || name.length()<2){
            nameLayout.setError(getString(R.string.name_level_error));
            error = true;
        }
        if(!matrixString.isEmpty() && !error){
            DatabaseReference myRef = database.getReference("utenti").child(user.getUid()).child("livelliPersonali").push();
            String key = myRef.getKey();
            myRef.setValue(new Level(key, matrixString, speed, name));

            startActivity(new Intent(CreateLevelActivity.this, PersonalLevelsActivity.class));
        }
        //creare un pop up che conferma il salvataggio del livello e poi uscire

    }

}