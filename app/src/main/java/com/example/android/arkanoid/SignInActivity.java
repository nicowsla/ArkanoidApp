package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputLayout emailLayout;
    private EditText emailET;
    private String email;

    private TextInputLayout usernameLayout;
    private EditText userNameET;
    private String username;

    private TextInputLayout passwordLayout;
    private EditText pswET;
    private String password;

    private TextInputLayout passwordConfirmationLayout;
    private EditText passwordConfirmationET;
    private String passwordConfirmation;

    private Button signinButton;

    private Boolean error = false;

    private Animation frombottom;
    private Animation fromtop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        mAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById((R.id.signin_emailc));
        emailET = (EditText)findViewById(R.id.signin_email);

        usernameLayout = findViewById(R.id.signin_usernamec);
        userNameET = findViewById(R.id.signin_username);

        passwordLayout = findViewById(R.id.signin_pswc);
        pswET = findViewById(R.id.signin_psw);

        passwordConfirmationLayout = findViewById(R.id.signin_psw_confermac);
        passwordConfirmationET = findViewById(R.id.signin_psw_conferma);

        signinButton = findViewById(R.id.signin_confirm);

        emailLayout.startAnimation(fromtop);
        usernameLayout.startAnimation(fromtop);
        passwordLayout.startAnimation(fromtop);
        passwordConfirmationLayout.startAnimation(fromtop);
        signinButton.startAnimation(frombottom);

        insertData();

        emailET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailLayout.setError( null );
                error = false;
            }
        } );

        emailET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLayout.setError( null );
                error = false;
            }
        } );

        userNameET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                usernameLayout.setError( null );
                error = false;
            }
        } );

        userNameET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameLayout.setError( null );
                error = false;
            }
        } );


        pswET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordLayout.setError( null );
                error = false;
            }
        } );

        pswET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordLayout.setError( null );
                error = false;
            }
        } );

        passwordConfirmationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordConfirmationLayout.setError( null );
                error = false;
            }
        } );

        passwordConfirmationET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordConfirmationLayout.setError( null );
                error = false;
            }
        } );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    public void signin(View view){
        insertData();
        verifyCredentials();
        createAccount();
    }


    public void insertData(){
        email = emailET.getText().toString();
        password = pswET.getText().toString();
        passwordConfirmation = passwordConfirmationET.getText().toString();
        username = userNameET.getText().toString();
    }


    public void verifyCredentials(){
        if(username == null || username.length()<1){
            usernameLayout.setError( getString(R.string.signin_username_error) );
            error = true;
        }

        if(email == null || email.length()<1){
            emailLayout.setError( getString(R.string.signin_email_error) );
            error = true;

        }else if(!isValidEmailAddress(email)){
            emailLayout.setError( getString(R.string.signin_email_error) );
            error = true;
        }
        if(password == null || password.length()<1){
            passwordLayout.setError( getString(R.string.signin_psw_error) );
            error = true;

        }else if(password.length()<6){
            passwordLayout.setError( getString(R.string.signin_psw_lenght_error) );
            error = true;

        }else if(!confirmPassword(password, passwordConfirmation)){
            passwordLayout.setError(getString(R.string.signin_psw_confirm_error));
            passwordConfirmationLayout.setError(getString(R.string.signin_psw_confirm_error));
            error = true;
        }
    }

    public boolean confirmPassword(String psw, String pswConfirmation){
        if(psw.equals(pswConfirmation)){
            return true;
        }else{
            return false;
        }
    }


    public void createAccount(){
        if(!error){ mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference( "utenti" ).child( user.getUid() );
                            myRef.setValue( new User(username, email) );

                            Toast.makeText( getApplicationContext(), R.string.signin_check_mail, Toast.LENGTH_LONG ).show();
                            AlertDialog alertDialog = new AlertDialog.Builder( SignInActivity.this ).create();
                            alertDialog.setTitle( R.string.signin_attention);
                            alertDialog.setMessage(getString(R.string.signin_check_mail));
                            alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity( new Intent( SignInActivity.this, LoginActivity.class ) );//qui mettere passaggio per l'altra activity
                                        }
                                    } );
                            alertDialog.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            AlertDialog alertDialog = new AlertDialog.Builder( SignInActivity.this ).create();
                            alertDialog.setTitle( R.string.signin_failed );
                            alertDialog.setMessage( getString(R.string.sigin_failure_msg) );
                            alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    } );
                            alertDialog.show();

                        }

                        // ...
                    }
                });
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}