package com.example.android.arkanoid;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

final public class UsersListViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView txtTitle;
    public TextView score;
    public ImageView img;



    public UsersListViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_name);
        score = itemView.findViewById(R.id.list_desc);
        img = itemView.findViewById(R.id.card_view_img);

    }

    public void setTxtTitle(String string) {
        txtTitle.setText(string);
    }
    public void setScore(String s) { score.setText(s);
    }

    public void setImg(String a){
        byte[] b = Base64.decode(a, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        img.setImageBitmap(bitmap);
    }

    public void hide() {
        root.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        score.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
      //  root.setPadding(0, 0, 0, 0);
    }

    public void show() {
        root.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);
        score.setVisibility(View.VISIBLE);
       // root.setPadding(0, 15, 0, 15 );
    }
}