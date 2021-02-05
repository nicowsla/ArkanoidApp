package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChallengeListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChallengeListAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Animation fromtop;
    private Context context;
    private String myUsername;
    private boolean requestReceived;
    private boolean requestSend;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_list);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        myUsername=  pref.getString("username", null);

        Bundle i = getIntent().getExtras();
        requestSend = i.getBoolean("R");
        requestReceived = i.getBoolean("S");

        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.startAnimation( fromtop );

        fetch();

    }

    private void fetch() {
        //query di tutti gli utenti ordinati per username per visualizzare la lista di utenti
        final Query query;

        if(requestReceived){
            query = database.getReference().child("utenti").child(user.getUid()).child("RichiesteSfidaRicevute");
        }else if(requestSend){
            query = database.getReference().child("utenti").child(user.getUid()).child("RichiesteSfidaEffettuate");
        }else{
            query = database.getReference().child("utenti").child(user.getUid()).child("Storico");
        }

        FirebaseRecyclerOptions<Challenge> options =
                new FirebaseRecyclerOptions.Builder<Challenge>()
                        .setQuery(query, new SnapshotParser<Challenge>() {
                            @NonNull
                            @Override
                            public Challenge parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Challenge(snapshot.child("id").getValue(String.class), snapshot.child("userID").getValue(String.class),
                                        snapshot.child("username").getValue(String.class), snapshot.child("score").getValue(Long.class),
                                        snapshot.child("yourScore").getValue(Long.class), snapshot.child("accepted").getValue(Boolean.class),
                                        snapshot.child("refused").getValue(Boolean.class)) ;
                            }
                        })
                        .build();

        adapter = new ChallengeListAdapter(options, user.getUid(), myUsername, getApplicationContext(), requestReceived, requestSend);
        recyclerView.setAdapter(adapter);
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
        startActivity( new Intent( ChallengeListActivity.this, MenuActivity.class) );
    }
}