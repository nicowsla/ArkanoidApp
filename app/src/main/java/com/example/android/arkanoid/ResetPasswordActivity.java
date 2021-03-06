package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import java.util.Locale;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private  String emailString;
    private TextInputLayout emailLayout;
    private Animation frombottom, fromtop;
    private Button b;
    private Boolean error = false;
    private String uid;
    private Boolean guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        uid = pref.getString("uid", null);
        guest = pref.getBoolean("guest", false);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        email = (EditText) findViewById(R.id.email_reset_psw);
        emailLayout =  (TextInputLayout) findViewById(R.id.email_reset_pswc);
        b =  (Button) findViewById( R.id.reset_psw) ;
        emailLayout.startAnimation( fromtop );
        b.startAnimation( frombottom );

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        insertData();

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

    }

    public void insertData(){ emailString = email.getText().toString(); }

    public void verifyCredentials(){
        if(email == null || email.length()<1){
            emailLayout.setError( getString(R.string.signin_email_error) );
            error = true;

        }else if(!isValidEmailAddress(emailString)){
            emailLayout.setError( getString(R.string.signin_email_error) );
            error = true;
        }
    }

    public  void resetPassword(View view){
        insertData();
        verifyCredentials();

        if(!error) {
            mAuth.fetchSignInMethodsForEmail(emailString)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            boolean emailNotExists = task.getResult().getSignInMethods().isEmpty();

                            if (emailNotExists) {
                                AlertDialog alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                                alertDialog.setTitle(getString(R.string.error));
                                alertDialog.setMessage(getString(R.string.email_nor_registered));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                email.setText("");
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            } else {
                                mAuth.sendPasswordResetEmail(emailString);
                                AlertDialog alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                                alertDialog.setTitle(getString(R.string.reset_psw));
                                alertDialog.setMessage(getString(R.string.check_email));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if(guest){
                                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                                }else{
                                                    startActivity(new Intent(ResetPasswordActivity.this, UserProfileActivity.class));
                                                }
                                            }
                                        });
                                alertDialog.show();
                            }

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

    @Override
    public void onBackPressed() {
        if(guest){
            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
        }else{
            startActivity(new Intent(ResetPasswordActivity.this, UserProfileActivity.class));
        }
    }
}
