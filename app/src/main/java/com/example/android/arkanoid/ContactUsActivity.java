package com.example.android.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ContactUsActivity extends AppCompatActivity {
    EditText email;
    EditText subject;
    EditText body;
    Button buttonSend;
    private TextInputLayout emailLayout;
    private TextInputLayout subjectLayout;
    private TextInputLayout BodyLayout;

    private Animation frombottom;
    private Animation fromtop;
    private Boolean error = false;

    private String emailString;
    private String subjectString;
    private String bodyString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailLayout = findViewById((R.id.etTo));
        email = findViewById(R.id.email);
        subjectLayout = findViewById(R.id.etSubject);
        subject = findViewById(R.id.Subject);
        BodyLayout = findViewById(R.id.etBody);
        body = findViewById(R.id.Body);
        buttonSend = findViewById(R.id.btnSend);

        emailLayout.startAnimation(fromtop);
        subjectLayout.startAnimation(fromtop);
        BodyLayout.startAnimation(fromtop);
        buttonSend.startAnimation(frombottom);

        email.setFocusable( false );
        email.setFocusableInTouchMode( false );

        email.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailLayout.setError( null );
                error = false;
            }
        } );

        email.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLayout.setError( null );
                error = false;
            }
        } );

        subject.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                subjectLayout.setError( null );
                error = false;
            }
        } );

        subject.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectLayout.setError( null );
                error = false;
            }
        } );


        body.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                BodyLayout.setError( null );
                error = false;
            }
        } );

        body.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BodyLayout.setError( null );
                error = false;
            }
        } );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!email.getText().toString().isEmpty() && !subject.getText().toString().isEmpty()
                        && !body.getText().toString().isEmpty()) {

                    emailString = email.getText().toString();
                    subjectString = subject.getText().toString();
                    bodyString = body.getText().toString();

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email.getText().toString()});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectString);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, bodyString);
                    emailIntent.setData(Uri.parse("mailto:"));

                    if(emailIntent.resolveActivity(getPackageManager())!=null)
                    {
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_app_email)));
                    }else
                    {
                        Toast.makeText(ContactUsActivity.this, getString(R.string.error_contact), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ContactUsActivity.this,getString(R.string.error_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
