package com.example.android.arkanoid;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Locale;
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
        LoadLocale();
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
        TextView lostpassword = findViewById(R.id.recovery_password);
        guestButton = findViewById(R.id.guest_button);

        /*
        logo.startAnimation(fromtop);
        emailLayout.startAnimation(fromtop);
        pswLayout.startAnimation(fromtop);
        loginButton.startAnimation(frombottom);
        signin.startAnimation(frombottom);
        lostpassword.startAnimation(frombottom);
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

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Button changeLang = (Button) findViewById(R.id.changeMyLanguage);
        Button contact = (Button) findViewById(R.id.mybuttoncontacts);
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

    public void showChangeLanguageDialog(View view)
    {
        final String[] listItems={"Italiano", "English"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(LoginActivity.this);
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

        AlertDialog mDialog=mBuilder.create();
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