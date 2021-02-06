package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class ResetPasswordActivity extends NavigationMenuActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private  String emailString;
    private TextInputLayout emailLayout;
    private Animation frombottom, fromtop;
    private Button b;
    private Boolean error = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reset_password, null, false);
        dl.addView(contentView, 0);
        mAuth = FirebaseAuth.getInstance();

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        email = findViewById(R.id.email_reset_psw);
        emailLayout =  findViewById(R.id.email_reset_pswc);
        b =  findViewById( R.id.reset_psw) ;
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
                                                dialog.dismiss();
                                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
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
                                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
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

}
