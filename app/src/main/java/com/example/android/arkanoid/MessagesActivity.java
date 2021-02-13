package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.android.arkanoid.entity.Messages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Locale;
import java.util.Objects;

public class MessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private EditText messaggio;
    private String friend;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Animation frombottom, fromtop;
    private TextView actionBarName;
    private StorageReference mStorageRef;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        actionBarName = (TextView) findViewById(R.id.action_bar_name);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.startAnimation( fromtop );
        messaggio = (EditText) findViewById( R.id.commenti_scrivi);
        photo = (ImageView) findViewById(R.id.action_bar_img);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        SharedPreferences p = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        friend = p.getString( "friend", null );
        String friendUsername = p.getString( "friendName", null );
        actionBarName.setText(friendUsername);
        linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setStackFromEnd(true); //visualizzare messaggi dal più recente


        StorageReference riversRef = mStorageRef.child(friend).child("images/profilo.jpg");
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

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        fetch();
    }
    private void fetch() {
        final Query query = database.getReference().child("utenti").child(user.getUid()).child("messaggi").child(friend);

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(query, new SnapshotParser<Messages>() {
                            @NonNull
                            @Override
                            public Messages parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Messages(snapshot.child("utente").getValue().toString(),
                                        snapshot.child("testo").getValue().toString(), null);
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Messages, MessagesActivity.ViewHolder>(options) {
            @Override
            public MessagesActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_messages, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MessagesActivity.ViewHolder holder, final int position, final Messages lista) {
                holder.setTxtDesc(lista.getTesto());
                if(lista.getUtente().equals(friend)){
                    holder.itemView.setBackground(getDrawable(R.drawable.round_view_received));
                    holder.txtDesc.setTextColor(getResources().getColor(R.color.purple_dark));
                }else{
                    holder.itemView.setBackground(getDrawable(R.drawable.round_view_send));
                    holder.txtDesc.setTextColor(getResources().getColor(R.color.button));
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void send(View view) {
        String c = messaggio.getText().toString();
        if(c!= null && !c.isEmpty()){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
            String nomeUtente = pref.getString("username", null);
            DatabaseReference data = database.getReference().child("utenti").child(user.getUid()).child("messaggi").child(friend).push();
            String ckey = data.getKey();
            Messages messaggio = new Messages(user.getUid(), c, ckey);
            data.setValue(messaggio);

            DatabaseReference data1 = database.getReference().child("utenti").child(friend).child("messaggi").child(user.getUid()).push();
            String ckey1 = data1.getKey();
            Messages messaggio1 = new Messages(user.getUid(), c, ckey1);
            data1.setValue(messaggio1);

            this.messaggio.setText("");
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtDesc;


        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtDesc = itemView.findViewById(R.id.text);
        }



        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed(){
        startActivity( new Intent(MessagesActivity.this, UserProfileActivity.class) );
    }

    public void goBackToProfile(View view){
        startActivity( new Intent(MessagesActivity.this, UserProfileActivity.class) );
    }
}