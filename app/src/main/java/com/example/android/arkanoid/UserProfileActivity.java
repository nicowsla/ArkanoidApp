package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
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
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Locale;

import static android.view.View.VISIBLE;
import static com.example.android.arkanoid.SignInActivity.hasPermissions;

public class UserProfileActivity extends NavigationMenuActivity {
    private EditText username;
    private TextView email;
    private TextInputLayout usernameLayout;
    private TextInputLayout emailLayout;


    private FloatingActionButton modifyProfile;
    private FloatingActionButton modifyPicture;
    private FloatingActionButton modifyPassword;
    private FloatingActionMenu menu;
    private BottomNavigationView bottonMenu;

    private Button confirmButton;
    private FirebaseAuth mAuth;
    private ImageView photo;

    private String usernameString;
    private String emailString;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private String currentUser;

    private String profileUser;

    private StorageReference mStorageRef;

    private final int IMG_REQUEST =1;
    private final int IMG_REQUEST_ALBUM=2;
    private Bitmap bitmap;
    private Uri path;
    private String imageString;
    byte[] imgByte;
    private Boolean errore =false;
    private Animation frombottom, fromtop;

    private Long bestScore;
    private Long bestTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //metto la NavBar laterale
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_user_profile, null, false);
        dl.addView(contentView, 0);

        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        currentUser = user.getUid();


        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        profileUser = pref.getString("friend", "nessuno");


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
        bottonMenu = findViewById(R.id.bottom_navigation);
        bottonMenu.setOnNavigationItemSelectedListener(navListener);

        //se il l'id dell'utente che sta usando l'app è uguale all'id del profilo che vuole vedere va al suo profilo, dove può modificare
        //altrimenti nascondo il menu per modificare poichè non si trova sul suo profilo
        if(currentUser.equals(profileUser)){
            bottonMenu.setVisibility(View.GONE);
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }else{
            menu.setVisibility(View.GONE);
        }

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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.levels:
                    Toast.makeText(UserProfileActivity.this, getString(R.string.send_level),
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                    editor.putBoolean( "exchangeMode", true);
                    editor.apply();
                    startActivity(new Intent(UserProfileActivity.this, PersonalLevelsActivity.class));
                    break;
                case R.id.challenge:
                    Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
                    i.putExtra("MODE", 4);
                    i.putExtra("Level", 1);
                    i.putExtra("Multiplayer", true);
                    i.putExtra("Sfidante", true);
                    i.putExtra("Sfidato", false);
                    startActivity(i);
                    Toast.makeText(UserProfileActivity.this, getString(R.string.play),
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.message:
                    startActivity(new Intent(UserProfileActivity.this, MessagesActivity.class));
                    break;
            }

            return true;
        }
    };


    public void showData() {

        DatabaseReference myRef = database.getReference("utenti").child(profileUser);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                usernameString = (dataSnapshot.child("username").getValue(String.class));
                emailString = dataSnapshot.child("email").getValue(String.class);
                bestScore = dataSnapshot.child("bestScore").getValue(Long.class);
                bestTime = dataSnapshot.child("bestTime").getValue(Long.class);
                username.setText(usernameString);
                email.setText(emailString);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        StorageReference riversRef = mStorageRef.child(profileUser).child("images/profilo.jpg");
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
            DatabaseReference myRef1 = database.getReference( "utenti" ).child( currentUser );
            myRef1.child( "username" ).setValue( usernameString );

            if(bestScore!=0 || bestTime!=1000000000){
                DatabaseReference myRef2 = database.getReference( "punteggi" ).child( currentUser );
                myRef2.child( "username" ).setValue( usernameString );
            }

            SharedPreferences.Editor editor = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
            editor.putString("username", usernameString);
            editor.apply();

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
        if(usernameString.isEmpty() || usernameString.length()<1 || usernameString.length()>16){
            usernameLayout.setError( getString(R.string.invalid_username) );
            errore = true;
        }

    }

    public void editImg(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef1 = storageRef.child(currentUser).child("images/profilo.jpg");

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

        StorageReference riversRef = storageRef.child(currentUser).child("images/profilo.jpg");
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
                imageString = imageToString();
                SharedPreferences.Editor editor1 = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
                editor1.putString("photo", imageString);
                editor1.apply();
            }catch (IOException e){
            }
        } else if(requestCode==IMG_REQUEST && data!=null){
            bitmap = (Bitmap) data.getExtras().get("data");
            path = data.getData();
            photo.setImageBitmap(bitmap);
            photo.setVisibility(VISIBLE);
            imageString = imageToString();
            SharedPreferences.Editor editor1 = getSharedPreferences("arkanoid", MODE_PRIVATE).edit();
            editor1.putString("photo", imageString);
            editor1.apply();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(UserProfileActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent( UserProfileActivity.this, MenuActivity.class));
                }
                return;
            }

        }
    }
}
