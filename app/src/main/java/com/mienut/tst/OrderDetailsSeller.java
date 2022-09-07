package com.mienut.tst;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.List;

public class OrderDetailsSeller extends RecyclerView.Adapter<OrderDetailsSeller.ViewHolder1> {
    private List<Transaction> listdata;

    public OrderDetailsSeller(List<Transaction> listdata) {
        this.listdata = listdata;
    }

    @Override
    public OrderDetailsSeller.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.order_details_view, parent, false);
        OrderDetailsSeller.ViewHolder1 viewHolder = new OrderDetailsSeller.ViewHolder1(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final OrderDetailsSeller.ViewHolder1 holder, int position) {
        final Transaction myListData = listdata.get(position);
        holder.pname.setText(myListData.getName());
        holder.pprice.setText("RS " + myListData.getAmount());
        holder.pquantity.setText("Qnty: "+myListData.getQuantity());

        Picasso.get().load(Uri.parse(myListData.getImageuri())).into(holder.imageView);


    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView pname, pprice;
        public TextView pquantity;

        public ViewHolder1(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pr_image);
            this.pname = (TextView) itemView.findViewById(R.id.pr_name);
            this.pprice = (TextView) itemView.findViewById(R.id.pr_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.pr_qnty);

        }
    }
}
