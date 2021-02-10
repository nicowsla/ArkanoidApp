package com.example.android.arkanoid;


import androidx.annotation.NonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersListActivity extends NavigationMenuActivity {
    private static final int PERMISSION_ID = 44;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserListAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Animation  fromtop;
    private Context context;
    private SearchView searchBar;
    private Boolean rankingScore;
    private Boolean rankingTime;
    private BottomNavigationView bottonMenu;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView distanceTextView;
    private TextView kmTextView;
    private SeekBar seekBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_users_list, null, false);
        dl.addView(contentView, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        rankingScore = pref.getBoolean("score", false);
        rankingTime = pref.getBoolean("time", false);

        distanceTextView = (TextView) findViewById( R.id.distance );
        seekBar = (SeekBar) findViewById(R.id.seekBarDistance);
        kmTextView = (TextView) findViewById( R.id.kilometri );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

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

        seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0){
                    distanceTextView.setText( "Illimitata" );
                    kmTextView.setVisibility( View.INVISIBLE );
                    adapter.setDistance( null );
                    adapter.notifyDataSetChanged();
                }else {
                    distanceTextView.setText( String.valueOf( progress )  );
                    adapter.setDistance( (long) progress );
                    adapter.notifyDataSetChanged();
                    kmTextView.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                getLastLocation();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );
    }

    private void fetch() {
        //query di tutti gli utenti ordinati per username per visualizzare la lista di utenti
        final Query query;
                if(rankingScore){
                    query = database.getReference().child("punteggi").orderByChild("bestScore");
                }else if(rankingTime){
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
                                       snapshot.child("bestTime").getValue(Long.class), snapshot.child("livArcade").getValue(Integer.class), snapshot.child("livTema").getValue(Integer.class), new Coordinate(snapshot.child( "coordinate" ).child("latitude").getValue(Double.class), snapshot.child( "coordinate" ).child("longitude").getValue(Double.class))) ;
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

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    if(!distanceTextView.getText().toString().equals( "Illimitata" )) {
                                        adapter.setDistance( Long.parseLong( distanceTextView.getText().toString()) );
                                        adapter.setCoordinate( new Coordinate( location.getLatitude(), location.getLongitude() ) );
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if(!distanceTextView.getText().toString().equals( "Illimitata" )) {
                adapter.setDistance( Long.parseLong( distanceTextView.getText().toString()) );
                adapter.setCoordinate( new Coordinate( location.getLatitude(), location.getLongitude() ) );
                adapter.notifyDataSetChanged();
            }
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }
}
