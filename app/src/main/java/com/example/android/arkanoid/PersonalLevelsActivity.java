package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PersonalLevelsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private LinearLayoutManager linearLayoutManager;
    private Boolean exchangeMode;
    private String friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_levels);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        friend = pref.getString("friend", "nessuno");
        exchangeMode = pref.getBoolean("exchangeMode", false);

        //nasconde il pannello delle notifiche
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView = findViewById(R.id.list);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetch();
    }

    private void fetch() {
        final Query query = database.getReference().child("utenti").child(user.getUid()).child("livelliPersonali");

        FirebaseRecyclerOptions<Level> options =
                new FirebaseRecyclerOptions.Builder<Level>()
                        .setQuery(query, new SnapshotParser<Level>() {
                            @NonNull
                            @Override
                            public Level parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Level(snapshot.child("id").getValue().toString(),
                                        snapshot.child("matrixString").getValue().toString(),
                                        Integer.parseInt(snapshot.child("speed").getValue().toString()),
                                        snapshot.child("name").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Level, PersonalLevelsActivity.ViewHolder>(options) {
            @Override
            public PersonalLevelsActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_messages, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PersonalLevelsActivity.ViewHolder holder, final int position, final Level lista) {
                holder.setTitolo(lista.getName());
                holder.titolo.setTextColor(getResources().getColor(R.color.button));
                holder.itemView.setBackground(getDrawable(R.drawable.round_shape_rectangular));

                holder.root.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(exchangeMode){
                            DatabaseReference myRef = database.getReference( "utenti" ).child( friend).child("livelliPersonali").child(lista.getId());
                            myRef.setValue( new Level(lista.getId(),lista.getMatrixString(), lista.getSpeed(), lista.getName()) );
                            AlertDialog alertDialog = new AlertDialog.Builder( PersonalLevelsActivity.this ).create();
                            alertDialog.setTitle( R.string.confirm);
                            alertDialog.setMessage(getString(R.string.level_send));
                            alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity( new Intent( PersonalLevelsActivity.this, UserProfileActivity.class ) );//qui mettere passaggio per l'altra activity
                                        }
                                    } );
                            alertDialog.show();

                        }else{
                            SharedPreferences.Editor editor = getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                            editor.putString( "matrixString", lista.getMatrixString());
                            editor.apply();

                            Intent k = new Intent(PersonalLevelsActivity.this, MainActivity.class);
                            k.putExtra("M", 5);
                            startActivity(k);
                        }

                    }
                } );



            }
        };
        recyclerView.setAdapter(adapter);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView titolo;


        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            titolo = itemView.findViewById(R.id.text);
        }

        public void setTitolo(String string) {
            titolo.setText(string);
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
        startActivity( new Intent(PersonalLevelsActivity.this, MenuActivity.class) );
    }

}