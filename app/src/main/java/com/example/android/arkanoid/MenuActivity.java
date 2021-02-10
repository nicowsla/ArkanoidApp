package com.example.android.arkanoid;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class MenuActivity extends NavigationMenuActivity {
    private int level;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //inserire la navbar
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_menu, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        level = pref.getInt("livArcade", 1);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //CONTROLLO PER IL PRIMO AVVIO
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            // AlertDialog code here
            AlertDialog alertDialog = new AlertDialog.Builder( MenuActivity.this ).create();
            alertDialog.setTitle( getString(R.string.first_run) );
            alertDialog.setMessage( getString(R.string.first_run_info) );
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                        }
                    } );
            alertDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();
        }
    }

    public void goToGame(View View){
        Intent i = new Intent(this, MainActivity.class);
        switch (View.getId()) {
            case (R.id.menu_go_to_tema):
                startActivity(new Intent(this, ThemeLevelsActivity.class));
                break;
            case (R.id.menu_go_to_classificata):
                i.putExtra("Level", 1);
                i.putExtra("MODE", 2);
                i.putExtra("Multiplayer", false);
                startActivity(i);
                break;
            case (R.id.menu_go_to_arcade):
                i.putExtra("Level", level);
                i.putExtra("MODE", 3);
                i.putExtra("Multiplayer", false);
                startActivity(i);
                break;
            case (R.id.menu_go_to_infinita):
                i.putExtra("Level", 1);
                i.putExtra("MODE", 4);
                i.putExtra("Multiplayer", false);
                startActivity(i);
                break;
            case (R.id.menu_go_to_landscape):
                i.putExtra("Level", 1);
                i.putExtra("MODE", 6);
                i.putExtra("Multiplayer", false);
                startActivity(i);
                break;
        }
    }

    public void goToShowLevels(View View){
        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
        editor.putBoolean( "exchangeMode", false);
        editor.apply();
        startActivity(new Intent(MenuActivity.this, PersonalLevelsActivity.class));
    }

    public void goToUserList(View View){
        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
        editor.putBoolean("score", false );
        editor.putBoolean("time", false );
        editor.apply();
        startActivity(new Intent(MenuActivity.this, UsersListActivity.class));
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder( MenuActivity.this ).create();
        alertDialog.setTitle(getString(R.string.attention));
        alertDialog.setMessage(getString(R.string.exit_confirm));
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent a = new Intent( Intent.ACTION_MAIN );
                        a.addCategory( Intent.CATEGORY_HOME );
                        a.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( a );
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
}