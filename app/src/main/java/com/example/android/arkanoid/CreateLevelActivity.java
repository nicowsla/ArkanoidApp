package com.example.android.arkanoid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.android.arkanoid.entity.Level;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

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

        nameLayout = findViewById(R.id.level_namec);
        nameET = findViewById(R.id.level_name);

        seekBar = findViewById(R.id.seekBar);
        progress = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        progress.setText(getString(R.string.speed_created_level)+speed+"/"+seekBar.getMax());

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
                progress.setText(getString(R.string.speed_created_level)+speed+"/"+seekBar.getMax());
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, CreateLevel.class);
                startActivity(i);
                //finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        onPause();
        AlertDialog alertDialog = new AlertDialog.Builder( CreateLevelActivity.this ).create();
        alertDialog.setTitle( R.string.attention );
        alertDialog.setMessage( getString(R.string.exit_confirm) );
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity( new Intent( CreateLevelActivity.this, CreateLevel.class ) );
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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void saveLevel(View view) {
        name = nameET.getText().toString();
        if(name.isEmpty() || name.length()<2 || name.length()>15){
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