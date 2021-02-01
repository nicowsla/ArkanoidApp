package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends NavigationMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //inserire la navbar
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_menu, null, false);
        dl.addView(contentView, 0);
    }


    public void goToGame(View View){
        switch (View.getId()) {
            case (R.id.menu_go_to_tema):
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("T", 1);
                startActivity(i);
                break;
            case (R.id.menu_go_to_classificata):
                Intent j = new Intent(this, MainActivity.class);
                j.putExtra("C", 2);
                startActivity(j);
                break;
            case (R.id.menu_go_to_arcade):
                Intent k = new Intent(this, MainActivity.class);
                k.putExtra("A", 3);
                startActivity(k);
                break;
            case (R.id.menu_go_to_infinita):
                Intent z = new Intent(this, MainActivity.class);
                z.putExtra("I", 4);
                startActivity(z);
                break;
        }
    }

    public void goToCreateLevel(View View){
        startActivity(new Intent(MenuActivity.this, CreateLevel.class));
    }

    public void goToUserList(View View){
        startActivity(new Intent(MenuActivity.this, UsersListActivity.class));
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