package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class NavigationMenuActivity extends AppCompatActivity {
    private NavigationView nv;
    protected DrawerLayout dl;
    private ActionBarDrawerToggle t;

    private TextView username;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);

        nv = (NavigationView)findViewById(R.id.nv);
        dl = (DrawerLayout) findViewById(R.id.activity_nav_menu);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        final String currentUser = pref.getString("uid", null);
        String usernameString = pref.getString("username", null);
        String imageString = pref.getString("photo", "ciao");



        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        username = findViewById(R.id.username);
        username.setText(usernameString);
        photo = findViewById(R.id.menu_photo);

        if( imageString.equals("ciao")){

        }else{
            byte[] b = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            photo.setImageBitmap(bitmap);
        }
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                int id = item.getItemId();
                switch(id){
                    case R.id.show_menu:
                        startActivity(new Intent(NavigationMenuActivity.this, MenuActivity.class));
                        break;
                    case R.id.profile:
                        editor.putString("friend", currentUser );
                        editor.apply();
                        startActivity(new Intent(NavigationMenuActivity.this, UserProfileActivity.class));
                        break;
                    case R.id.rankings:
                        editor.putBoolean("score", true );
                        editor.putBoolean("time", false );
                        editor.apply();
                        startActivity(new Intent(NavigationMenuActivity.this, UsersListActivity.class));
                        break;
                    case R.id.received_request:
                        Intent i = new Intent(NavigationMenuActivity.this, ChallengeListActivity.class);
                        i.putExtra("R", true);
                        i.putExtra("S", false);
                        startActivity(i);
                        break;
                    case R.id.story:
                        Intent i2 = new Intent(NavigationMenuActivity.this, ChallengeListActivity.class);
                        i2.putExtra("R", false);
                        i2.putExtra("S", false);
                        startActivity(i2);
                        break;
                    case R.id.settings:
                        startActivity(new Intent(NavigationMenuActivity.this, SettingsActivity.class));
                        break;
                    case R.id.logout:
                        AlertDialog alertDialog = new AlertDialog.Builder( NavigationMenuActivity.this ).create();
                        alertDialog.setTitle( R.string.attention );
                        alertDialog.setMessage( getString(R.string.exit_confirm) );
                        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", MODE_PRIVATE ).edit();
                                        editor.clear();
                                        editor.commit();
                                        startActivity( new Intent( NavigationMenuActivity.this, LoginActivity.class ) );
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}