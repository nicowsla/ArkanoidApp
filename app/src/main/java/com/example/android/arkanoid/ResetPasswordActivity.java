package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private  String emailString;
    private TextInputLayout emailLayout;
    private Animation frombottom, fromtop;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        email = findViewById(R.id.email_reset_psw);
        emailLayout =  findViewById(R.id.email_reset_pswc);
        b =  findViewById( R.id.reset_psw) ;
        emailLayout.startAnimation( fromtop );
        b.startAnimation( frombottom );
    }

    public  void resetPassword(View view){
        insertData();
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

    public  void insertData(){
        emailString = email.getText().toString();
    }

    @Override
    public void onBackPressed(){
        startActivity( new Intent(ResetPasswordActivity.this, LoginActivity.class) );
    }
}