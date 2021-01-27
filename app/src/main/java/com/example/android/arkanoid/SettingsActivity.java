package com.example.android.arkanoid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        commands_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView
                TextView stampadiprova = findViewById(R.id.stampadiprova);
                stampadiprova.setText("Selected : " + selectedItemText + i);
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
                TextView stampadiprova = findViewById(R.id.stampadiprova2);
                stampadiprova.setText("Selected : " + selectedItemText);
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
        AlertDialog alertDialog = new AlertDialog.Builder( SettingsActivity.this ).create();
        alertDialog.setTitle( R.string.settings_select_commands_info );
        alertDialog.setMessage( getString(R.string.commands_info_dialog) );
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, getString(R.string.commands_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
        alertDialog.setButton( AlertDialog.BUTTON_NEGATIVE, getString(R.string.commands_not_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
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
}