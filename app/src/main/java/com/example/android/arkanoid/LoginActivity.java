package com.example.android.arkanoid;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputLayout emailLayout;
    private EditText emailET;
    private String email;

    private TextInputLayout pswLayout;
    private EditText pswET;
    private String password;

    private Button loginButton;

    private Animation frombottom;
    private Animation fromtop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        emailLayout = findViewById(R.id.login_emailc);
        emailET = findViewById(R.id.login_email);

        pswLayout = findViewById(R.id.login_pswc);
        pswET = findViewById(R.id.login_psw);

        loginButton = findViewById(R.id.login_button);

        emailLayout.startAnimation(fromtop);
        pswLayout.startAnimation(fromtop);
        loginButton.startAnimation(frombottom);

        emailET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailLayout.setError( null );

            }
        } );

        emailET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLayout.setError( null );
            }
        } );

        pswET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                pswLayout.setError( null );
            }
        } );

        pswET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pswLayout.setError( null );
            }
        } );

    }

    public void login(View view){
        insertData();
        verifyCredentials();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user!=null && user.isEmailVerified()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this, getString(R.string.login_verify_mail),
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed_authentication),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }

    public void insertData(){
        email = emailET.getText().toString();
        password = pswET.getText().toString();
    }

    public void verifyCredentials(){
        if(Objects.isNull(email) || email.length()<1){

            emailLayout.setError( getString(R.string.login_missing_mail) );
            return;
        }
        if(Objects.isNull(password) || password.length()<1){
            pswLayout.setError( getString(R.string.login_missing_psw)  );
            return;
        }
    }

    public void signInNewUser(View view){
        startActivity(new Intent(LoginActivity.this, SignInActivity.class));
    }

}