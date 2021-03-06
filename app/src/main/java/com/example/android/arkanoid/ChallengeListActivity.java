package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.android.arkanoid.entity.Challenge;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;

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

        //metto la NavBar laterale
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_challenge_list, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        myUsername=  pref.getString("username", null);

        bottonMenu = (BottomNavigationView) findViewById(R.id.bottom_navigation_challenge);
        bottonMenu.setOnNavigationItemSelectedListener(navListener);

        //nasconde il pannello delle notifiche
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");
        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        // i Boolean seguenti servono per impostare la visualizzazione della classifica se per tempo o puntiu
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

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.startAnimation( fromtop );

        //titolo ActionBar
        if(!requestReceived && !requestSend){
            bottonMenu.setVisibility(View.GONE);
            getSupportActionBar().setTitle(R.string.history);
        }
        if(requestSend){
            getSupportActionBar().setTitle(R.string.send_challenges);
        }

        if(requestReceived){
            getSupportActionBar().setTitle(R.string.received_challenges);
        }
        fetch();

    }

    private void fetch() {

        //in base ai flag booleani ricevuti si effettua una diversa query
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
                                        snapshot.child("yourScore").getValue(Long.class)) ;
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

    //imposto il BottomNavigationMenu per effettuare lo switch tra le varie visualizzazioni della classifica
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.request_sent:
                    if(requestSend){
                        Toast.makeText(ChallengeListActivity.this, getString(R.string.send_challenges),
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
                        Toast.makeText(ChallengeListActivity.this, getString(R.string.received_challenges),
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