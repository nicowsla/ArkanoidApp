package com.example.android.arkanoid;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.view.View.VISIBLE;

public class UserProfileActivity extends AppCompatActivity {
    private EditText username;
    private TextView email;
    private TextInputLayout usernameLayout;
    private TextInputLayout emailLayout;


    private FloatingActionButton modifyProfile;
    private FloatingActionButton modifyPicture;
    private FloatingActionButton modifyPassword;
    private FloatingActionMenu menu;

    private Button confirmButton;
    private FirebaseAuth mAuth;
    private ImageView photo;

    private String usernameString;
    private String emailString;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private final int IMG_REQUEST =1;
    private final int IMG_REQUEST_ALBUM=2;
    private Bitmap bitmap;
    private Uri path;
    private String image;
    byte[] imgByte;
    private Boolean errore =false;
    private Animation frombottom, fromtop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_user_profile);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        username =  findViewById(R.id.profile_username);
        usernameLayout = findViewById( R.id.profile_usernamec ) ;
        email = findViewById(R.id.profile_email);
        emailLayout = findViewById( R.id.profile_emailc );


        confirmButton = findViewById( R.id.confirm );

        photo =  findViewById(R.id.photo);
        showData();
        modifyProfile =  findViewById( R.id.mod_profile );
        modifyPicture = findViewById( R.id.mod_picture );
        modifyPassword =   findViewById( R.id.mod_psw );
        menu =  findViewById( R.id.menu );


        email.setFocusable( false );
        email.setFocusableInTouchMode( false );
        username.setFocusable(false);
        username.setFocusableInTouchMode(false);


        usernameLayout.startAnimation( fromtop );
        photo.startAnimation( fromtop );
        emailLayout.startAnimation( frombottom );


        modifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmButton.setVisibility( VISIBLE );
                menu.close( true );
                menu.setVisibility( View.INVISIBLE );
                username.setFocusable(true);
                username.setFocusableInTouchMode(true);
            }
        } );

        modifyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close( true );
                uploadPhoto();
            }
        } );

        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close( true );
                resetPassword();
            }
        } );

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProfile();

            }
        } );
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameLayout.setError( null );
                errore = false;
            }
        } );

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                usernameLayout.setError( null );
                errore = false;
            }
        } );


    }

    public void showData() {

        DatabaseReference myRef = database.getReference("utenti").child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                usernameString = (dataSnapshot.child("username").getValue(String.class));
                emailString = dataSnapshot.child("email").getValue(String.class);

                username.setText(usernameString);
                email.setText(emailString);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        StorageReference riversRef = mStorageRef.child(user.getUid()).child("images/profilo.jpg");
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).into(photo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void modifyProfile( ) {
        insertData();
        checkData();
        if(!errore) {
            DatabaseReference myRef1 = database.getReference( "utenti" ).child( user.getUid() );

            myRef1.child( "username" ).setValue( usernameString );
            SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
            editor.putString("username", usernameString);

            username.setFocusable( false );
            username.setFocusableInTouchMode( false );
            confirmButton.setVisibility( View.INVISIBLE );
            menu.setVisibility( VISIBLE );

        }

    }

    private void insertData(){
        usernameString = username.getText().toString();
    }

    private void checkData(){
        if(usernameString.isEmpty()){
            usernameLayout.setError( getString(R.string.invalid_username) );
            errore = true;
        }

    }

    public void editImg(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef1 = storageRef.child(user.getUid()).child("images/profilo.jpg");

        riversRef1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });


        StorageReference riversRef = storageRef.child(user.getUid()).child("images/profilo.jpg");
        riversRef.putBytes(imgByte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    public void uploadPhoto() {
        final CharSequence[] items={getString(R.string.camera),getString(R.string.gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle(getString(R.string.select_image));

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(getString(R.string.camera))) {  // equivale if i==0 ecc
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, IMG_REQUEST);
                }else if(items[i].equals(getString(R.string.gallery))){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, IMG_REQUEST_ALBUM);
                } else if(items[i].equals(getString(R.string.cancel))){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST_ALBUM && resultCode == RESULT_OK && data!=null) {
            path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
                image = imageToString();
            }catch (IOException e){
            }
        } else if(requestCode==IMG_REQUEST && data!=null){
            bitmap = (Bitmap) data.getExtras().get("data");
            path = data.getData();
            photo.setImageBitmap(bitmap);
            photo.setVisibility(VISIBLE);
            image = imageToString();
        }
    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        imgByte = byteArrayOutputStream.toByteArray();
        editImg();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public void resetPassword() {
        menu.close( true );
        startActivity(new Intent(UserProfileActivity.this, ResetPasswordActivity.class));
    }
    @Override
    public void onBackPressed(){
        menu.close( true );
        startActivity( new Intent(UserProfileActivity.this, MenuActivity.class) );
    }
}