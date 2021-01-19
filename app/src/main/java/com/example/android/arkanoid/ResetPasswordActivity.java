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
    private EditText emailET;
    private  String email;
    private TextInputLayout emailL;
    private Animation frombottom, fromtop;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_reset_password);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        mAuth = FirebaseAuth.getInstance();
        emailET = (EditText) findViewById(R.id.emailRecuperoPsw);
        emailL = (TextInputLayout) findViewById(R.id.emailRecuperoPswc);
        b = (Button) findViewById( R.id.recuperoPsw ) ;
        emailL.startAnimation( fromtop );
        b.startAnimation( frombottom );
    }

    public  void recuperoPassword (View view){
        inserimentomail();
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean emailNotExists = task.getResult().getSignInMethods().isEmpty();

                        if (emailNotExists) {
                            AlertDialog alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                            alertDialog.setTitle("Errore");
                            alertDialog.setMessage("L'e-mail fornita non risulta essere iscritta");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            mAuth.sendPasswordResetEmail(email);
                            AlertDialog alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                            alertDialog.setTitle("Recupero password");
                            alertDialog.setMessage("Controlla la tua casella e-mail!");
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

    public  void inserimentomail(){
        email = emailET.getText().toString();
    }

    @Override
    public void onBackPressed(){
        startActivity( new Intent(ResetPasswordActivity.this, LoginActivity.class) );
    }
}
