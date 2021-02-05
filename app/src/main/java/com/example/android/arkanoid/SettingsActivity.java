package com.example.android.arkanoid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends NavigationMenuActivity {

    private ImageView logo;
    private Boolean enableTouch;
    private Boolean enableAccelerometer;
    private String selezione = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                if(i==0){
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", true );
                    editor.putBoolean("touch", false );
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, getString(R.string.accelerometer_selected), Toast.LENGTH_SHORT).show();
                }else if(i==1){
                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                    editor.putBoolean( "accelerometro", false );
                    editor.putBoolean("touch", false);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, getString(R.string.gamepad_selected), Toast.LENGTH_SHORT).show();
                }else if(i==2){
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(this,"No selection",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void changeCommands(View view) {
    }

    public void changeLanguage(View view) {
    }

    public void infoCommands(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder( SettingsActivity.this, R.style.MyDialogTheme).create();
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