package com.example.android.arkanoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserListAdapter extends FirebaseRecyclerAdapter<User, UsersListViewHolder> {
    private String filter;
    private Context context;
    private StorageReference mStorageRef;
    private static final long ONE_MEGABYTE= 1024 * 1024;
    private Boolean rankingScore;
    private Boolean rankingTime;
    private  String img;


    public UserListAdapter(@NonNull FirebaseRecyclerOptions<User> options, String filter, Context context, Boolean rankingScore, Boolean rankingTime) {
        super( options );
        this.filter = filter.toLowerCase();
        this.context = context;
        this.rankingScore = rankingScore;
        this.rankingTime = rankingTime;
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    void setFilter(String filter) {
        this.filter = filter;
    }


    @Override
    protected void onBindViewHolder(@NonNull final UsersListViewHolder holder, int i, @NonNull final User lista) {
        holder.setTxtTitle(lista.getUsername());
        if(rankingScore){
            Long s = lista.getBestScore();
            if(s!=0){
                String s1 = s.toString();
                holder.setScore(s1);
            }else{
                holder.setScore("Non pervenuto");
            }

        }else  if(rankingTime){
            Long msec = lista.getBestTime();
            if(msec<10000000){
                long minuti = msec / (1000 * 60);
                long secondi = (msec - (minuti*60000)) / 1000;
                long decimi = (msec - (minuti*60000) - (secondi*1000)) / 100;
                holder.setScore(minuti + "'" + secondi + "''" + decimi +"'''");
            }else{
                holder.setScore("Non pervenuto");
            }


        }

        StorageReference riversRef = mStorageRef.child(lista.getId()).child("images/profilo.jpg");
        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            img = Base64.encodeToString(bytes, Base64.DEFAULT);
            holder.setImg(img);
        }).addOnFailureListener(exception -> {
        });

        if(lista.getEmail().toLowerCase().contains( filter) || lista.getUsername().toLowerCase().contains( filter)) {

            holder.show();

            holder.root.setOnClickListener(view -> {
                SharedPreferences.Editor editor = context.getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                editor.putString( "friend", lista.getId());
                editor.putString( "friendName", lista.getUsername());
                editor.apply();
                Intent intent = new Intent( context, UserProfileActivity.class  );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity( intent );

            });
        } else {
            holder.hide();
        }
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new UsersListViewHolder( view);
    }



}
