package com.mienut.tst;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder1> {
    private List<Transaction> listdata;
    DatabaseReference ref, dref;
    Context context;

    String userID;
    FirebaseAuth fAuth;



    public CartAdapter(List<Transaction> listdata) {
        this.listdata = listdata;
    }

    @Override
    public CartAdapter.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        context = parent.getContext();
        View listItem = layoutInflater.inflate(R.layout.cart_view, parent, false);
        CartAdapter.ViewHolder1 viewHolder = new CartAdapter.ViewHolder1(listItem);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder1 holder, int position) {
        final Transaction myListData = listdata.get(position);
        dref = FirebaseDatabase.getInstance().getReference("products").child(myListData.getProduct_id());
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Popular plr = dataSnapshot.getValue(Popular.class);
                holder.pname.setText(plr.getProd_name()+" "+plr.getUnit());
                holder.pprice.setText("Rs. " + plr.getProd_price());
                holder.pquantity.setText("Quantity: "+myListData.getQuantity());

                Picasso.get().load(Uri.parse(plr.getImageuri())).into(holder.imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        holder.delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref = FirebaseDatabase.getInstance().getReference("cart").child(userID);
                ref.child(myListData.getProduct_id()).setValue(null);


            }
        });
        holder.subq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref = FirebaseDatabase.getInstance().getReference("cart").child(userID).child(myListData.getProduct_id());
                if(Integer.parseInt(myListData.getQuantity())>1){
                Map<String, Object> hopperUpdate = new HashMap<>();
                hopperUpdate.put("quantity",String.valueOf(Integer.parseInt(myListData.getQuantity())-1) );
                ref.updateChildren(hopperUpdate);}
            }
        });
        holder.addq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref = FirebaseDatabase.getInstance().getReference("cart").child(userID).child(myListData.getProduct_id());
                Map<String, Object> hopperUpdate = new HashMap<>();

                hopperUpdate.put("quantity",String.valueOf(Integer.parseInt(myListData.getQuantity())+1) );
                    ref.updateChildren(hopperUpdate);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView pname, pprice;
        public TextView pquantity;
        public Button delete_item,addq,subq;
        public RelativeLayout relativeLayout;
        public LinearLayout rel_lyt;
        public CardView cr;

        public ViewHolder1(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pr_image);
            this.pname = (TextView) itemView.findViewById(R.id.pr_name);
            this.pprice = (TextView) itemView.findViewById(R.id.pr_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.qnty);
            rel_lyt = (LinearLayout) itemView.findViewById(R.id.btn_lyt);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            this.cr = (CardView) itemView.findViewById(R.id.cr);
            this.addq = (Button) itemView.findViewById(R.id.addq);
            this.subq = (Button) itemView.findViewById(R.id.subq);
            this.delete_item = (Button) itemView.findViewById(R.id.delete);

        }
    }
}