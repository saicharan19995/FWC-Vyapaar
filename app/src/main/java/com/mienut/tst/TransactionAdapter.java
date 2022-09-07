package com.mienut.tst;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    private List<Transaction> listdata;
    DatabaseReference ref,dref,trans,ref2, ordref;
    String ild;
    String oa="Confirm";
    String od="Canceled";

    boolean flg=true;
    Context context;
    List<String> qnnt,ordid;
    int i=0;
    // RecyclerView recyclerView;
    public TransactionAdapter(List<Transaction> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.order_acceptview, parent, false);
        context = parent.getContext();
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Transaction myListData = listdata.get(position);
        holder.pname.setText(myListData.getName());
        holder.puser.setText(myListData.getUser());
        holder.pprice.setText("Rs. "+myListData.getAmount());
        holder.pquantity.setText("Quantity: "+myListData.getQuantity());
        holder.puserphone.setText("Phone No: "+myListData.getPhone());
        holder.pdate.setText("Time: "+myListData.getTimestamp().toString());
        holder.payid.setText("Payment: "+myListData.getPayment().toString());
        holder.orderpid.setText("ID: "+myListData.getPuid());
        holder.ordtyp.setText(myListData.getOrdertype());

        holder.ordtyp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordref = FirebaseDatabase.getInstance().getReference("Address").child(myListData.getUserid());

                ordref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        address_retriver pd = dataSnapshot.getValue(address_retriver.class);

                        String geoUr = "http://maps.google.com/maps?q=loc:" + pd.getLatitute() + "," + pd.getLongitutde() + " (" + myListData.getUser() + ")";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUr));
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        //Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        Picasso.get().load(Uri.parse(myListData.getImageuri())).into(holder.imageView);
        holder.cr.setCardBackgroundColor(Color.parseColor("#1203A9F4"));

        if(oa.equals(myListData.getStatus()) )
        { holder.rel_lyt.setVisibility(View.GONE);
            if(!myListData.getPayment().equals("Nil")){
            holder.otplyt.setVisibility(View.VISIBLE);}
            holder.status.setText("Order Accepted");
            holder.cr.setCardBackgroundColor(Color.parseColor("#6348AE1F"));
            }
        else if(od.equals(myListData.getStatus()))
        { holder.rel_lyt.setVisibility(View.GONE);
            holder.status.setText("Order Canceled");
            holder.cr.setCardBackgroundColor(Color.parseColor("#37F80505"));}
        else if(myListData.getStatus().equals("Complete") )
        { holder.rel_lyt.setVisibility(View.GONE);
            holder.status.setText("Product Delivered");
            holder.cr.setCardBackgroundColor(Color.parseColor("#6348AE1F"));
        }
        else {
            holder.status.setVisibility(View.GONE);
            holder.rel_lyt.setVisibility(View.VISIBLE);
        }
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trans = FirebaseDatabase.getInstance().getReference("order_summary").child(myListData.getOrderid());
                Map<String, Object> hopperUpdatet = new HashMap<>();
                hopperUpdatet.put("status", "Confirm");
                trans.updateChildren(hopperUpdatet);






                holder.rel_lyt.setVisibility(View.GONE);
                holder.status.setText("Order Accepted");
                holder.status.setVisibility(View.VISIBLE);
                holder.cr.setCardBackgroundColor(Color.parseColor("#6348AE1F"));

            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref = FirebaseDatabase.getInstance().getReference("order_summary").child(myListData.getOrderid());
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("status", "Canceled");
                ref.updateChildren(hopperUpdates);
                holder.rel_lyt.setVisibility(View.GONE);
                holder.status.setText("Order Canceled");
                holder.status.setVisibility(View.VISIBLE);
                holder.cr.setCardBackgroundColor(Color.parseColor("#37F80505"));
            }
        });

        holder.ord_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String ottp = holder.otp_data.getText().toString().trim();

                System.out.println("OOOOOOOOOTP   "+holder.otp_data);

                ref = FirebaseDatabase.getInstance().getReference("order_summary").child(myListData.getOrderid());

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Transaction tst = dataSnapshot.getValue(Transaction.class);
                        if(tst.getOtp().equals(ottp))
                        {
                            ref2 = FirebaseDatabase.getInstance().getReference("order_detials").child(myListData.getOrderid());
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    qnnt = new ArrayList<String>();
                                    ordid = new ArrayList<String>();

                                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                        Transaction tst = postsnap.getValue(Transaction.class);
                                        qnnt.add(tst.getQuantity());
                                        ordid.add(postsnap.getKey().toString());
                                    }
                                    for( String iid:ordid)
                                    {
                                        dref = FirebaseDatabase.getInstance().getReference("products");

                                        ild=iid;
                                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                                    if(ild.equals(postsnap.getKey())) {
                                                        Popular plr = postsnap.getValue(Popular.class);
                                                        long val = plr.getQuantity();
                                                        Map<String, Object> hopperUpdate = new HashMap<>();
                                                        hopperUpdate.put("quantity", val - Long.parseLong(qnnt.get(i)));
                                                        dref.child(ild).updateChildren(hopperUpdate);
                                                        i++;
                                                        System.out.println("IIIIIIIIIDDDDDD   " + i);
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    ref = FirebaseDatabase.getInstance().getReference("order_summary").child(myListData.getOrderid());
                                    Map<String, Object> hopperUpdates = new HashMap<>();
                                    hopperUpdates.put("status", "Complete");
                                    ref.updateChildren(hopperUpdates);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            holder.otplyt.setVisibility(View.GONE);
                            holder.status.setText("Product Delivered");
                        }
                        else{
                            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(context);
                            passwordResetDialog.setTitle("OTP");
                            passwordResetDialog.setMessage("OTP Incorrect"); passwordResetDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });


                            passwordResetDialog.create().show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(view.getContext(),"click on item: "+myListData.getName(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, Order_details.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", myListData.getOrderid());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
         return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView pname,pprice,pquantity,pdate,puser,puserphone,status,orderpid,payid, ordtyp;
        public Button accept, decline, ord_complete;
        public EditText otp_data;
        public RelativeLayout relativeLayout;
        public LinearLayout rel_lyt, otplyt;
        public CardView cr;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pr_image);
            this.pname = (TextView) itemView.findViewById(R.id.pr_name);
            this.pprice = (TextView) itemView.findViewById(R.id.pr_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.pr_quant);
            this.pdate = (TextView) itemView.findViewById(R.id.pr_date);
            this.puser =(TextView) itemView.findViewById(R.id.usrname);
            this.puserphone = (TextView) itemView.findViewById(R.id.phone_number);
            this.accept = (Button) itemView.findViewById(R.id.accep);
            this.decline = (Button) itemView.findViewById(R.id.rejext);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.orderpid = (TextView) itemView.findViewById(R.id.orderpid);
            this.cr = (CardView) itemView.findViewById(R.id.cr);
            rel_lyt = (LinearLayout)itemView.findViewById(R.id.btn_lytt);
            payid = (TextView) itemView.findViewById(R.id.payid);
            ord_complete = (Button) itemView.findViewById(R.id.ord_complete);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
            otp_data = (EditText) itemView.findViewById(R.id.otp_data);
            otplyt = (LinearLayout) itemView.findViewById(R.id.otplyt);
            ordtyp = (TextView) itemView.findViewById(R.id.ordtyp);
        }
    }
}
