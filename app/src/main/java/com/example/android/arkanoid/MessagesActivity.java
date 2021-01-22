package com.example.android.arkanoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class MessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase database;
    private EditText commento;
    private String friend;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Animation frombottom, fromtop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_messages );
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        recyclerView = findViewById(R.id.list);
        recyclerView.startAnimation( fromtop );
        commento = findViewById( R.id.commenti_scrivi);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        SharedPreferences p = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
        friend = p.getString( "friend", null );
        linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setStackFromEnd(true); //visualizzare messaggi dal più recente

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
                holder.setTxtTitle(lista.getUtente());
                holder.setTxtDesc(lista.getTesto());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void send(View view) {
        String c = commento.getText().toString();
        if(c!= null && !c.isEmpty()){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("arkanoid", MODE_PRIVATE);
            String nomeUtente = pref.getString("username", null);
            DatabaseReference data = database.getReference().child("utenti").child(user.getUid()).child("messaggi").child(friend).push();
            String ckey = data.getKey();
            Messages messaggio = new Messages(nomeUtente, c, ckey);
            data.setValue(messaggio);

            DatabaseReference data1 = database.getReference().child("utenti").child(friend).child("messaggi").child(user.getUid()).push();
            String ckey1 = data1.getKey();
            Messages messaggio1 = new Messages(nomeUtente, c, ckey1);
            data1.setValue(messaggio1);

            commento.setText("");
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtDesc;


        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
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
        startActivity( new Intent(MessagesActivity.this, MenuActivity.class) );
    }
}
