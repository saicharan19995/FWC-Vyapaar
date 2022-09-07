package com.mienut.tst;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private Context mContext ;
    private List<Cat> mData ;


    public RecyclerViewAdapter(Context mContext, List<Cat> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_category,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_book_title.setText(mData.get(position).getTitle());
//        holder.img_book_thumbnail.setImageResource(mData.get(position).getThumbnail());
        Picasso.get().load(Uri.parse(mData.get(position).getImageUrl())).into(holder.img_book_thumbnail);
        System.out.println("URL :"+mData.get(position).getImageUrl());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,Type.class);
                intent.putExtra("cat", mData.get(position).getTitle());
                mContext.startActivity(intent);
                System.out.println("Click Success:"+mData.get(position).getTitle());

            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_book_title;
        ImageView img_book_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title = (TextView) itemView.findViewById(R.id.cat_title_id) ;
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.cat_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);


        }
    }


}
