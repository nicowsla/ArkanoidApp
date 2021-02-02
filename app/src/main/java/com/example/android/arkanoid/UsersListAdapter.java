package com.example.android.arkanoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UsersListAdapter extends FirebaseRecyclerAdapter<User, UsersListViewHolder> {
    private String filter;
    private Context context;
    private StorageReference mStorageRef;
    private static final long ONE_MEGABYTE= 1024 * 1024;
    private  String img;


    public UsersListAdapter(@NonNull FirebaseRecyclerOptions<User> options, String filter, Context context) {
        super( options );
        this.filter = filter.toLowerCase();
        this.context = context;
        mStorageRef = FirebaseStorage.getInstance().getReference();


    }

    void setFilter(String filter) {
        this.filter = filter;
    }


    @Override
    protected void onBindViewHolder(@NonNull final UsersListViewHolder holder, int i, @NonNull final User lista) {
        holder.setTxtTitle(lista.getUsername());


        StorageReference riversRef = mStorageRef.child(lista.getId()).child("images/profilo.jpg");
        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                img = Base64.encodeToString(bytes, Base64.DEFAULT);
                holder.setImg(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        if(lista.getEmail().toLowerCase().contains( filter) || lista.getUsername().toLowerCase().contains( filter)) {

            holder.show();

            holder.root.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = context.getSharedPreferences( "arkanoid", Context.MODE_PRIVATE ).edit();
                    editor.putString( "friend", lista.getId());
                    editor.putString( "friendName", lista.getUsername());
                    editor.apply();
                    Intent intent = new Intent( context, UserProfileActivity.class  );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity( intent );

                }
            } );
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
