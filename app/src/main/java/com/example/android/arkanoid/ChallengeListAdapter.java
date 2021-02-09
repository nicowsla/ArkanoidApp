package com.example.android.arkanoid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ChallengeListAdapter extends FirebaseRecyclerAdapter<Challenge, ChallengeListViewHolder> {
    private String myID;
    private String myUsername;
    private Context context;
    private StorageReference mStorageRef;
    private static final long ONE_MEGABYTE= 1024 * 1024;
    private  String img;
    private Boolean received;
    private Boolean sent;

    public ChallengeListAdapter(@NonNull FirebaseRecyclerOptions<Challenge> options, String myID, String myUsername, Context context, Boolean received, Boolean sent) {
        super( options );
        this.myID = myID;
        this.myUsername = myUsername;
        this.context = context;
        this.sent = sent;
        this.received = received;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //FORZO IMPOSTAZIONE LINGUA
        SharedPreferences preferences=context.getSharedPreferences("Settings", MODE_PRIVATE);
        String language=preferences.getString("My_Lang","");

        //IMPOSTA LA LINGUA
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChallengeListViewHolder holder, int i, @NonNull final Challenge lista) {
        holder.setName1(lista.getUsername());

        holder.setName2(myUsername);

        Long score = lista.getScore()*(-1);
        if(score>0){
            String s1 = score.toString();
            holder.setScore1(s1);
        }else{
            holder.setScore1(context.getString(R.string.waiting));
        }

        if((lista.getYourScore()*(-1))>0){
            Long score1 = lista.getYourScore()*(-1);
            String s1 = score1.toString();
            holder.setScore2(s1);
        }else{
            holder.setScore2(context.getString(R.string.waiting));
        }

        StorageReference riversRef = mStorageRef.child(lista.getUserID()).child("images/profilo.jpg");
        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            img = Base64.encodeToString(bytes, Base64.DEFAULT);
            holder.setImg1(img);
        }).addOnFailureListener(exception -> {
        });

        riversRef = mStorageRef.child(myID).child("images/profilo.jpg");
        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            img = Base64.encodeToString(bytes, Base64.DEFAULT);
            holder.setImg2(img);
        }).addOnFailureListener(exception -> {
        });

        if(sent){
            holder.button.setVisibility(View.GONE);
        }
        if(received || sent ){
            holder.setResult(context.getString(R.string.play));
        }

        if(!sent && !received){
            if(lista.getYourScore()<lista.getScore()){
                holder.setResult(context.getString(R.string.win_challenges_list));
            }else if (lista.getYourScore()==lista.getScore()){
                holder.setResult(context.getString(R.string.draw_challenge));
            }else{
                holder.setResult(context.getString(R.string.lose_challenges_list));
            }
            holder.button.setVisibility(View.GONE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                editor.putString("idRichiesta", lista.getId());
                editor.putString( "friend", lista.getUserID());
                editor.putString( "friendName", lista.getUsername());
                editor.putString( "friendScore", lista.getScore().toString());
                editor.apply();
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("MODE", 4);
                i.putExtra("Level", 1);
                i.putExtra("Multiplayer", true);
                i.putExtra("Sfidato", true);
                i.putExtra("Sfidante", false);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    @NonNull
    @Override
    public ChallengeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_request, parent, false);
        return new ChallengeListViewHolder( view);
    }



}
