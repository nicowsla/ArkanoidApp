package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity {
    private NavigationView nv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        String usernameString = pref.getString("username", null);
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        username = findViewById(R.id.username);
        username.setText(usernameString);
        nv = (NavigationView)findViewById(R.id.nv);
        dl = (DrawerLayout) findViewById(R.id.activity_menu);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.profile:


                             break;
                    case R.id.logout:
                        AlertDialog alertDialog = new AlertDialog.Builder( MenuActivity.this ).create();
                        alertDialog.setTitle( R.string.attention );
                        alertDialog.setMessage( getString(R.string.exit_confirm) );
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", MODE_PRIVATE ).edit();
                                        editor.clear();
                                        editor.apply();
                                        startActivity( new Intent( MenuActivity.this, LoginActivity.class ) );
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


                return false;
            }
        });

    }


    public void goToGame(View View){
       startActivity(new Intent(MenuActivity.this, MainActivity.class));
    }

    public void goToCreateLevel(View View){
        startActivity(new Intent(MenuActivity.this, CreateLevelActivity.class));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder( MenuActivity.this ).create();
        alertDialog.setTitle( "ATTENZIONE!" );
        alertDialog.setMessage( "Sei sicuro di voler uscire?" );
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent a = new Intent( Intent.ACTION_MAIN );
                        a.addCategory( Intent.CATEGORY_HOME );
                        a.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( a );
                    }
                } );
        alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
        alertDialog.show();
    }
}