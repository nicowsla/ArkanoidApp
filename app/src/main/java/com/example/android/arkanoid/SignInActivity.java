package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailET;
    private EditText pswET;
    private EditText userNameET;
    private String email;
    private String password;
    private String username;
    private Boolean validCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.signin_email);
        pswET = findViewById(R.id.signin_psw);
        userNameET = findViewById(R.id.signin_username);
    }

    public void createAccount(View view){
        email = emailET.getText().toString();
        password = pswET.getText().toString();
        username = userNameET.getText().toString();

        validCredentials = verifyCredentials(email, password, username);

        if(validCredentials){ mAuth.createUserWithEmailAndPassword(email, password)
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

                            Toast.makeText( getApplicationContext(), "Registrazione completata, controlla la tua email!", Toast.LENGTH_LONG ).show();
                            AlertDialog alertDialog = new AlertDialog.Builder( SignInActivity.this ).create();
                            alertDialog.setTitle( "ATTENZIONE!" );
                            alertDialog.setMessage( "Per proseguire verifica la tua mail!" );
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
                            alertDialog.setTitle( "REGISTRAZIONE FALLITA!" );
                            alertDialog.setMessage( "Verifica di aver inserito correttamente i dati!" );
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
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder( SignInActivity.this ).create();
            alertDialog.setTitle( "CAMPI VUOTI" );
            alertDialog.setMessage( "Verifica di aver inserito correttamente i dati!" );
            alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    } );
            alertDialog.show();
        }

    }

    public boolean verifyCredentials(String email, String psw, String userName){
        if(email!=null && psw!=null &&userName!=null){
            return true;
        }else
            return false;
    }
}