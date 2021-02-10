package com.example.android.arkanoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class UserListAdapter extends FirebaseRecyclerAdapter<User, UsersListViewHolder> {
    private String filter;
    private Context context;
    private StorageReference mStorageRef;
    private static final long ONE_MEGABYTE= 1024 * 1024;
    private Boolean rankingScore;
    private Boolean rankingTime;
    private  String img;
    private Coordinate userCoordinate;
    private Long distance;


    public UserListAdapter(@NonNull FirebaseRecyclerOptions<User> options, String filter, Context context, Boolean rankingScore, Boolean rankingTime) {
        super( options );
        this.filter = filter.toLowerCase();
        this.context = context;
        this.rankingScore = rankingScore;
        this.rankingTime = rankingTime;
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

    void setFilter(String filter) {
        this.filter = filter;
    }
    void setUserCoordinate(Coordinate coordinateUtente){this.userCoordinate = coordinateUtente;}
    void setDistance (Long distance){this.distance = distance;}

    @Override
    protected void onBindViewHolder(@NonNull final UsersListViewHolder holder, int i, @NonNull final User lista) {

        if((rankingScore && lista.getBestScore()==0)|| (rankingTime && lista.getBestTime()>=1000000)) {
            holder.hide();
        }else  {
            if(rankingScore){
            Long s = lista.getBestScore()*(-1);
            String s1 = s.toString();
            holder.setScore(s1);
        }else if(rankingTime){
            Long msec = lista.getBestTime();
            long minuti = msec / (1000 * 60);
            long secondi = (msec - (minuti*60000)) / 1000;
            long decimi = (msec - (minuti*60000) - (secondi*1000)) / 100;
            long centesimi = (msec - (minuti * 60000) - (secondi * 1000) - (decimi * 100)) / 10;
            long millesimi = (msec - (minuti * 60000) - (secondi * 1000) - (decimi * 100) - (centesimi * 10));

            holder.setScore(minuti + "'" + secondi + "''" + decimi + centesimi + millesimi);
        }
            holder.setTxtTitle(lista.getUsername());
            StorageReference riversRef = mStorageRef.child(lista.getId()).child("images/profilo.jpg");
            riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                img = Base64.encodeToString(bytes, Base64.DEFAULT);
                holder.setImg(img);
            }).addOnFailureListener(exception -> {
            });

            if((lista.getEmail().toLowerCase().contains( filter) || lista.getUsername().toLowerCase().contains( filter))
                    && (userCoordinate ==null || distance==null || distance( userCoordinate.getLatitude(),lista.getCoordinate().getLatitude(), userCoordinate.getLongitude(), lista.getCoordinate().getLongitude() )<distance)) {

                holder.show();

                holder.root.setOnClickListener(view -> {
                    SharedPreferences.Editor editor = context.getSharedPreferences( "arkanoid", MODE_PRIVATE ).edit();
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

    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new UsersListViewHolder( view);
    }

    public Long distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // raggio della Terra
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c);
    }

}
