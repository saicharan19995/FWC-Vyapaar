package com.mienut.tst;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.util.List;


public class PopularAdaptar extends RecyclerView.Adapter<PopularAdaptar.ImageViewHolder> {


    private List<Popular> mPopular;

    Context context;
    DecimalFormat two = new DecimalFormat("##.00");

    public PopularAdaptar(List<Popular> popular) {

        this.mPopular = popular;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_items, parent, false);
        context=parent.getContext();
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        final Popular popularCur = mPopular.get(position);

            holder.prod_name.setText(popularCur.getProd_name() + " "+popularCur.getUnit());
            holder.prod_price.setText("Rs. "+popularCur.getProd_price());
            holder.prod_name.setSelected(true);
            holder.vendor.setSelected(true);
            holder.rt.setRating(popularCur.getStars());
            holder.mls.setText("Distance: "+two.format(popularCur.getMiles())+"Km");
        DatabaseReference prd = FirebaseDatabase.getInstance().getReference("Address").child(popularCur.getUsrid());
        prd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address_retriver adr = dataSnapshot.getValue(address_retriver.class);

                holder.vendor.setText(adr.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Picasso.get().load(Uri.parse(popularCur.getImageuri())).into(holder.prod_image);

        holder.c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, Product_Display.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", popularCur.getPid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPopular.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{

    TextView prod_name, prod_price, mls, vendor;
    ImageView prod_image;
    CardView c;
    RatingBar rt;
    ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        prod_name = itemView.findViewById(R.id.product_name);
        prod_price = itemView.findViewById(R.id.prodprice);
        prod_image = itemView.findViewById(R.id.product_image);
        mls =itemView.findViewById(R.id.miles);
        vendor = itemView.findViewById(R.id.vendor_name);
        c = itemView.findViewById(R.id.c);
        rt = itemView.findViewById(R.id.ratingBar);

    }
}
}
