package com.mienut.tst;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder1> {
    private List<Transaction> listdata;
    Context context;
    DatabaseReference mDaataRef;
    float stars;
    int people;
    boolean flg;
    public OrderDetailsAdapter(List<Transaction> listdata) {
        this.listdata = listdata;
    }

    @Override
    public OrderDetailsAdapter.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.feedback, parent, false);
        context= parent.getContext();
        OrderDetailsAdapter.ViewHolder1 viewHolder = new OrderDetailsAdapter.ViewHolder1(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final OrderDetailsAdapter.ViewHolder1 holder, int position) {
        final Transaction myListData = listdata.get(position);
                holder.pname.setText(myListData.getName());
                holder.pprice.setText("Rs. " + myListData.getAmount());
                holder.pquantity.setText("Quantity: "+myListData.getQuantity());
                Picasso.get().load(Uri.parse(myListData.getImageuri())).into(holder.imageView);

        mDaataRef = FirebaseDatabase.getInstance().getReference("products").child(myListData.getProduct_id());
        mDaataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    flg=true;
            Popular plr = dataSnapshot.getValue(Popular.class);
            stars = plr.getStars();
            people = plr.getPeople();
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            // Called when the user swipes the RatingBar
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                if(flg){
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("stars", (((people*stars)+rating)/(people+1)));
                hopperUpdates.put("people",people+1);
                mDaataRef.updateChildren(hopperUpdates);}

            }
        });


    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        public RatingBar ratingbar;
        public ImageView imageView,star1,star2,star3,star4,star5;
        public TextView pname, pprice;
        public TextView pquantity;

        public ViewHolder1(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.product_image);
            this.pname = (TextView) itemView.findViewById(R.id.product_title);
            this.pprice = (TextView) itemView.findViewById(R.id.product_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.product_quantity);
            this.ratingbar=(RatingBar)itemView.findViewById(R.id.ratingBar);


        }
    }
}
