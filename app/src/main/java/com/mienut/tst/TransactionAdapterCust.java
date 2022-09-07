package com.mienut.tst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAdapterCust extends RecyclerView.Adapter<TransactionAdapterCust.ViewHolder1>{
    private List<OrderByTIme> listdata;
    DatabaseReference ref,dref;
    String oa="Confirm";
    String od="Canceled";
    String ordid;
    Context context;

    public TransactionAdapterCust(List<OrderByTIme> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        context=parent.getContext();
        View listItem= layoutInflater.inflate(R.layout.customer_status, parent, false);
        ViewHolder1 viewHolder = new ViewHolder1(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final TransactionAdapterCust.ViewHolder1 holder, int position) {
        final OrderByTIme myList = listdata.get(position);


        ref = FirebaseDatabase.getInstance().getReference("order_summary").child(myList.getOrderid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Transaction myListData = dataSnapshot.getValue(Transaction.class);
                holder.pname.setText(myListData.getName());
                holder.pprice.setText("Rs. "+myListData.getAmount());
                holder.pquantity.setText("Quantity: "+myListData.getQuantity());
                holder.pdate.setText("Time: "+myListData.getTimestamp().toString());
                holder.payid.setText("Payment: "+myListData.getPayment());
                holder.orderid.setText("ID: "+dataSnapshot.getKey());

                ordid = myListData.getOrderid();
                Picasso.get().load(Uri.parse(myListData.getImageuri())).into(holder.imageView);
                holder.cr.setCardBackgroundColor(Color.parseColor("#1203A9F4"));
                if(oa.equals(myListData.getStatus()) )
                { holder.map_btn.setVisibility(View.GONE);
                    holder.cr.setCardBackgroundColor(Color.parseColor("#6348AE1F"));
                    holder.status.setText("Order Accepted");
                    holder.otp.setVisibility(View.VISIBLE);
                    holder.otp.setText("OTP: "+myListData.getOtp());

                    dref = FirebaseDatabase.getInstance().getReference("order_summary").child(myList.getOrderid());
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Transaction tst = dataSnapshot.getValue(Transaction.class);
                            if(tst.getPayment().equals("Nil"))
                            {
                                holder.pay_btn.setVisibility(View.VISIBLE);
                            }
                            else{
                                holder.rel_lyt.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                if(od.equals(myListData.getStatus()))
                { holder.rel_lyt.setVisibility(View.GONE);
                    holder.cr.setCardBackgroundColor(Color.parseColor("#37F80505"));
                    holder.status.setText("Order Canceled");}
                if(myListData.getStatus().equals("Complete"))
                {
                    holder.map_btn.setVisibility(View.GONE);
                    holder.rel_lyt.setVisibility(View.GONE);
                    holder.cr.setCardBackgroundColor(Color.parseColor("#6348AE1F"));
                    holder.status.setText("Product Purchased");
                }

                holder.cr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, Order_details.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", myListData.getOrderid());
                        context.startActivity(intent);



                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref = FirebaseDatabase.getInstance().getReference("order_summary").child(myList.getOrderid());
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("status", "Canceled");
                ref.updateChildren(hopperUpdates);
                holder.status.setText("Order Canceled");
                holder.cr.setCardBackgroundColor(Color.parseColor("#37F80505"));
                holder.rel_lyt.setVisibility(View.GONE);
            }
        });

        holder.pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Payment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", myList.getOrderid());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }





    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView pname,pprice,pquantity,pdate,status,orderid,payid,otp;
        public Button map_btn, address, pay_btn;
        public RelativeLayout relativeLayout;
        public LinearLayout rel_lyt;
        public CardView cr;
        public ViewHolder1(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pr_image);
            this.pname = (TextView) itemView.findViewById(R.id.pr_name);
            this.pprice = (TextView) itemView.findViewById(R.id.pr_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.pr_quant);
            this.pdate = (TextView) itemView.findViewById(R.id.pr_date);
            this.map_btn = (Button) itemView.findViewById(R.id.map);
            this.status = (TextView) itemView.findViewById(R.id.status);
            rel_lyt = (LinearLayout) itemView.findViewById(R.id.btn_lyt);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
            this.orderid = (TextView) itemView.findViewById(R.id.orderid);
            this.cr = (CardView) itemView.findViewById(R.id.cr);
            this.payid = (TextView)itemView.findViewById(R.id.payid);
            this.pay_btn = (Button) itemView.findViewById(R.id.pay);
            this.otp = (TextView) itemView.findViewById(R.id.otp);

        }
    }

}
