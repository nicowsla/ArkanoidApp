package com.example.android.arkanoid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ChallengeListViewHolder extends RecyclerView.ViewHolder  {
    public LinearLayout root;
    public TextView result;
    public TextView name1;
    public TextView name2;
    public TextView score1;
    public TextView score2;
    public ImageView img1;
    public ImageView img2;
    public Button button;

    public ChallengeListViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        result = itemView.findViewById(R.id.list_result);
        name1 = itemView.findViewById(R.id.list_request_name);
        name2 = itemView.findViewById(R.id.list_request_name2);
        score1 = itemView.findViewById(R.id.list_request_score);
        score2 = itemView.findViewById(R.id.list_request_score2);
        img1 = itemView.findViewById(R.id.list_image);
        img2 = itemView.findViewById(R.id.list_image2);
        button = itemView.findViewById(R.id.accept);

    }

    public void setResult(String string) {
        result.setText(string);
    }
    public void setName1(String string) {
        name1.setText(string);
    }
    public void setName2(String string) {
        name2.setText(string);
    }
    public void setScore1(String string) { score1.setText(string);}
    public void setScore2(String string) { score2.setText(string);}

    public void setImg1(String a){
        byte[] b = Base64.decode(a, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        img1.setImageBitmap(bitmap);
    }

    public void setImg2(String a){
        byte[] b = Base64.decode(a, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        img2.setImageBitmap(bitmap);
    }

}
