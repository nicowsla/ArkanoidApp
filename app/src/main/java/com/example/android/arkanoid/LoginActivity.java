package com.example.android.arkanoid;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final long ONE_MEGABYTE= 1024 * 1024;
    private FirebaseAuth mAuth;

    private ImageView logo;

    private TextInputLayout emailLayout;
    private EditText emailET;
    private String email;

    private TextInputLayout pswLayout;
    private EditText pswET;
    private String password;
    private String photo;

    private Button loginButton;
    private Button guestButton;
    private StorageReference mStorageRef;

    private boolean error=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //salvo lo userID per non perdere l'accesso
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        String uid = pref.getString("uid", null);
        if(!Objects.isNull(uid)){
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        }

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation frombottom = AnimationUtils.loadAnimation(this, R.anim.show_from_bottom); //prima era frombottom
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        logo = findViewById(R.id.logo);
        logo.setBackgroundResource(R.drawable.redball);

        emailLayout = findViewById(R.id.login_emailc);
        emailET = findViewById(R.id.login_email);

        pswLayout = findViewById(R.id.login_pswc);
        pswET = findViewById(R.id.login_psw);

        loginButton = findViewById(R.id.login_button);
        TextView signin = findViewById(R.id.sign_in);
        guestButton = findViewById(R.id.guest_button);

        /*
        logo.startAnimation(fromtop);
        emailLayout.startAnimation(fromtop);
        pswLayout.startAnimation(fromtop);
        loginButton.startAnimation(frombottom);
        signin.startAnimation(frombottom);
        */
        guestButton.startAnimation(frombottom);

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

        pswET.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                pswLayout.setError( null );
                error = false;
            }
        } );

        pswET.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pswLayout.setError( null );
                error = false;
            }
        } );

    }
    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            showSystemUI();
        }else{
            hideSystemUI();
        }
    }
    */
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void login(View view){
        insertData();
        verifyCredentials();
        if(!error) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                final FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null && user.isEmailVerified()) {
                                    //salvo lo userID dell'utente che si è appena loggato
                                    SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                    editor.putString("uid", user.getUid());
                                    editor.apply();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("utenti").child(user.getUid());
                                    // Read from the database
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                String username = dataSnapshot.child( "username" ).getValue(String.class);
                                                SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                                editor.putString( "uid", user.getUid() );
                                                editor.putString("username", username);
                                                editor.apply();
                                                startActivity(new Intent(LoginActivity.this, MenuActivity.class));

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                        }
                                    });
                                    //salvo nelle sharedPreferences l'immagine del profilo: scarico l'immagine dallo storage in un array di byte che converto in stringa
                                    StorageReference riversRef = mStorageRef.child(user.getUid()).child("images/profilo.jpg");
                                    riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            String img = Base64.encodeToString(bytes, Base64.DEFAULT);
                                            SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                                            editor.putString( "photo", img );
                                            editor.apply();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });

                                } else {
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
        }else{
            Toast.makeText(LoginActivity.this,getString(R.string.empty_field) ,
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void insertData(){
        email = emailET.getText().toString();
        password = pswET.getText().toString();
    }

    public void verifyCredentials(){
        if(Objects.isNull(email) || email.length()<1){
            emailLayout.setError( getString(R.string.login_missing_mail) );
            error = true;
            return;
        }
        if(Objects.isNull(password) || password.length()<1){
            pswLayout.setError( getString(R.string.login_missing_psw)  );
            error = true;
            return;
        }
    }

    public void signInNewUser(View view){
        startActivity(new Intent(LoginActivity.this, SignInActivity.class));
    }

    public void goToResetPassword(View view){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    public void goToContactUs(View view){
        startActivity(new Intent(LoginActivity.this, ContactUsActivity.class));
    }


}