package com.example.android.arkanoid;


import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersListActivity extends NavigationMenuActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserListAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Animation  fromtop;
    Context context;
    private SearchView searchBar;
    private Boolean rankingScore;
    private Boolean rankingTime;
    private BottomNavigationView bottonMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_users_list, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        rankingScore = pref.getBoolean("score", false);
        rankingTime = pref.getBoolean("time", false);

        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        searchBar = findViewById(R.id.search_bar);

        bottonMenu = findViewById(R.id.bottom_navigation_rankings);
        bottonMenu.setOnNavigationItemSelectedListener(navListener);

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

        if(rankingTime || rankingScore){        //se visualizzo la classifica nascondo il menu
            searchBar.setVisibility(View.GONE);
        }else{
            bottonMenu.setVisibility(View.GONE); // se visualizzo la lista utenti nascondo la searchBar
        }
        fetch();

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setFilter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        } );
    }

    private void fetch() {
        //query di tutti gli utenti ordinati per username per visualizzare la lista di utenti
        final Query query;
                if(rankingScore){
                    //visualizzo i primi 100
                    query = database.getReference().child("punteggi").orderByChild("bestScore");
                }else if(rankingTime){
                    //visualizzo i primi 100
                    query = database.getReference().child("punteggi").orderByChild("bestTime");
                }else{
                    query = database.getReference().child("utenti").orderByChild("username");
                }

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>() {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new User(snapshot.child("id").getValue(String.class), snapshot.child("username").getValue(String.class),
                                        snapshot.child("email").getValue(String.class), snapshot.child("bestScore").getValue(Long.class),
                                       snapshot.child("bestTime").getValue(Long.class), snapshot.child("livArcade").getValue(Integer.class), snapshot.child("livTema").getValue(Integer.class)) ;
                            }
                        })
                        .build();

        adapter = new UserListAdapter(options, "", getApplicationContext(), rankingScore, rankingTime);
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
        startActivity( new Intent( UsersListActivity.this, MenuActivity.class) );
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.score_ranking:
                    if(rankingScore){
                        Toast.makeText(UsersListActivity.this, getString(R.string.ranking_score),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                        editor.putBoolean( "score", true);
                        editor.putBoolean( "time", false);
                        editor.apply();
                        startActivity(new Intent(UsersListActivity.this, UsersListActivity.class));
                    }
                    break;
                case R.id.time_ranking:
                    if(rankingTime){
                        Toast.makeText(UsersListActivity.this, getString(R.string.ranking_time),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                        editor.putBoolean( "score", false);
                        editor.putBoolean( "time", true);
                        editor.apply();
                        startActivity(new Intent(UsersListActivity.this, UsersListActivity.class));
                    }
                    break;
            }

            return true;
        }
    };
}
