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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChallengeListActivity extends NavigationMenuActivity  {
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
    private BottomNavigationView bottonMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_challenge_list, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        myUsername=  pref.getString("username", null);

        bottonMenu = findViewById(R.id.bottom_navigation_challenge);
        bottonMenu.setOnNavigationItemSelectedListener(navListener);
        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle i = getIntent().getExtras();
        requestSend = i.getBoolean("S");
        requestReceived = i.getBoolean("R");

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

        if(!requestReceived && !requestSend){
            bottonMenu.setVisibility(View.GONE);
        }

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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.request_sent:
                    if(requestSend){
                        Toast.makeText(ChallengeListActivity.this, getString(R.string.challenge_request),
                                Toast.LENGTH_SHORT).show();
                    }else{
                       Intent i = new Intent(ChallengeListActivity.this, ChallengeListActivity.class);
                        i.putExtra("R", false);
                        i.putExtra("S", true);
                        startActivity(i);
                    }
                    break;
                case R.id.request_received:
                    if(requestReceived){
                        Toast.makeText(ChallengeListActivity.this, getString(R.string.challenge_received),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Intent i1 = new Intent(ChallengeListActivity.this, ChallengeListActivity.class);
                        i1.putExtra("R", true);
                        i1.putExtra("S", false);
                        startActivity(i1);
                    }
                    break;
            }

            return true;
        }
    };
}