package com.example.android.arkanoid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends NavigationMenuActivity {

    private ImageView logo;
    private Boolean enableTouch;
    private Boolean enableAccelerometer;
    private String selezione = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadLocale();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        enableTouch = pref.getBoolean("touch", true);
        enableAccelerometer = pref.getBoolean("accelerometro", false);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //in base alle shared impostare lo spinner sul comando attivo
    /*    if(enableAccelerometer &&  !enableTouch){

        }else if(!enableAccelerometer && enableTouch){

        }else if(!enableAccelerometer && !enableTouch){

        }*/

        logo = findViewById(R.id.info);
        logo.setBackgroundResource(R.drawable.ic_baseline_info_24);

        Spinner commands_spinner = (Spinner) findViewById(R.id.commands_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.commands, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        commands_spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);

        Spinner language_spinner = (Spinner) findViewById(R.id.languages_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        language_spinner.setAdapter(adapter2);
        //spinner2.setOnItemSelectedListener(this);


    /*    commands_spinner.setEndIconOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] stati = getResources().getStringArray( R.array.commands );
                new MaterialAlertDialogBuilder( SettingsActivity.this )
                        .setTitle( R.string.settings_select_commands_info )
                        .setSingleChoiceItems( stati, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selezione = stati[which];
                            }
                        } )
                        .setPositiveButton( "Conferma", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                statoE.setText( selezione );
                                dialog.dismiss();

                            }
                        } )
                        .setNegativeButton( "Annulla", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } ).show();
            }

        } );*/


        commands_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView

                if(i==1){
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", true );
                    editor.putBoolean("touch", false );
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, getString(R.string.accelerometer_selected), Toast.LENGTH_SHORT).show();
                }else if(i==2){
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", false );
                    editor.putBoolean("touch", false);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, getString(R.string.gamepad_selected), Toast.LENGTH_SHORT).show();
                }else if(i==3){
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", false );
                    editor.putBoolean("touch", true );
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, getString(R.string.touch_selected), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(this,"No selection",Toast.LENGTH_LONG).show();
            }
        });

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView

                if(i==1){
                    /*
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", true );
                    editor.putBoolean("touch", false );
                    editor.apply();
                     */
                    Toast.makeText(SettingsActivity.this, getString(R.string.italian_selected), Toast.LENGTH_SHORT).show();
                }else if(i==2){
                    /*
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", false );
                    editor.putBoolean("touch", false);
                    editor.apply();
                    */
                    Toast.makeText(SettingsActivity.this, getString(R.string.english_selected), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(this,"No selection",Toast.LENGTH_LONG).show();
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Button contact = (Button) findViewById(R.id.mybuttoncontacts);
        Button changeLang = (Button) findViewById(R.id.changeMyLanguage);
    }

    //#############################################################################


    public void showChangeLanguageDialog(View view)
    {
        final String[] listItems={"Italiano", "English"};
        android.app.AlertDialog.Builder mBuilder=new android.app.AlertDialog.Builder(SettingsActivity.this);
        mBuilder.setTitle("Scegli la lingua");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int k)
            {
                if(k==0)
                {
                    setLocale("it");
                    recreate();
                }

                if(k==1)
                {
                    setLocale("en");
                    recreate();
                }

                dialog.dismiss();

            }
        });

        android.app.AlertDialog mDialog=mBuilder.create();
        mDialog.show();
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
        SharedPreferences preferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        setLocale(language);
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

    //#############################################################################

    public void infoCommands(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder( SettingsActivity.this).create();
        alertDialog.setTitle( R.string.settings_select_commands_info );
        alertDialog.setMessage( getString(R.string.commands_info_dialog) );
        alertDialog.show();
    }

    /*
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Get the spinner selected item text
        String selectedItemText = (String) adapterView.getItemAtPosition(i);
        // Display the selected item into the TextView
        mTextView.setText("Selected : " + selectedItemText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Toast.makeText(this,"No selection",Toast.LENGTH_LONG).show();
    }

     */
    @Override
    public void onBackPressed(){
        startActivity( new Intent(SettingsActivity.this, MenuActivity.class) );
    }
}