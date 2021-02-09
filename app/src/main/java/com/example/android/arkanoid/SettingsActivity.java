package com.example.android.arkanoid;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class SettingsActivity extends NavigationMenuActivity {
    private TextInputLayout commandLayout;
    private EditText command;
    private TextInputLayout languageLayout;
    private EditText language;
    private String s= null;
    private String s1 = null;
    private Boolean enableTouch;
    private Boolean enableAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LoadLocale();
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        enableTouch = pref.getBoolean("touch", true);
        enableAccelerometer = pref.getBoolean("accelerometro", false);

        commandLayout = findViewById(R.id.settings_commandsc);
        command = findViewById(R.id.settings_commands);

        languageLayout = findViewById(R.id.settings_languagec);
        language = findViewById(R.id.settings_language);

        language.setText(R.string.select_a_language); //TODO METTERE LA LINGUA CHE STA

        if(enableTouch && !enableAccelerometer){
            command.setText(getString(R.string.touch));
        }else if(!enableTouch && enableAccelerometer){
            command.setText(getString(R.string.accelerometer));
        }else if(!enableTouch && !enableAccelerometer){
            command.setText(getString(R.string.gamepad));
        }

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        commandLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] comandi = getResources().getStringArray( R.array.commands);
                new MaterialAlertDialogBuilder( SettingsActivity.this )
                        .setTitle( R.string.commands_selector )
                        .setSingleChoiceItems( comandi, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                s = comandi[which];
                            }
                        } )
                        .setPositiveButton( getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(s.equals(getString(R.string.touch))){
                                    enableTouch = true;
                                    enableAccelerometer = false;
                                }else if(s.equals(getString(R.string.accelerometer))){
                                    enableTouch = false;
                                    enableAccelerometer = true;
                                }else if(s.equals(getString(R.string.gamepad))){
                                    enableTouch = false;
                                    enableAccelerometer = false;
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                editor.putBoolean("touch", enableTouch);
                                editor.putBoolean("accelerometro", enableAccelerometer);
                                editor.apply();
                                command.setText( s );
                                Toast.makeText(SettingsActivity.this, s, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        } )
                        .setNegativeButton( getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } ).show();
            }
        } );

        languageLayout.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] languages = getResources().getStringArray( R.array.languages);
                new MaterialAlertDialogBuilder( SettingsActivity.this )
                        .setTitle( R.string.commands_selector )
                        .setSingleChoiceItems( languages, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                s1 = languages[which];
                            }
                        } )
                        .setPositiveButton( getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(s1.equals("Italian")){
                                    setLocale("it");
                                    recreate();
                                }else if(s1.equals("English")){
                                    setLocale("en");
                                    recreate();
                                }
                                language.setText( s1 );
                                dialog.dismiss();
                                Toast.makeText(SettingsActivity.this, s1, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                            }
                        } )
                        .setNegativeButton( getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } ).show();
            }
        } );

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Button contact = (Button) findViewById(R.id.mybuttoncontacts);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.mymenu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.mybuttoncontacts:
                Intent i=new Intent(this, ContactUsActivity.class);
                startActivity(i);
                //finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String lang)
    {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    private void LoadLocale()
    {
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        setLocale(language);
    }

    public void infoCommands(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder( SettingsActivity.this).create();
        alertDialog.setTitle( R.string.settings_select_commands_info );
        alertDialog.setMessage( getString(R.string.commands_info_dialog) );
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        startActivity( new Intent(SettingsActivity.this, MenuActivity.class) );
    }
}