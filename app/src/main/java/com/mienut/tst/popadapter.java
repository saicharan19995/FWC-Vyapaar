package com.mienut.tst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class popadapter extends RecyclerView.Adapter<popadapter.ViewHolder> {

    private List<Popular> listdata;
    private String milk = "Milk";
    Context context;
    public popadapter(List<Popular> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        context=parent.getContext();
        View listItem= layoutInflater.inflate(R.layout.product_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Popular myListData = listdata.get(position);
        holder.pname.setText(myListData.getProd_name()+" "+myListData.getUnit());
        holder.pprice.setText("Rs. "+myListData.getProd_price());
        holder.pquantity.setText("Available Quantity: "+myListData.getQuantity());

        Picasso.get().load(Uri.parse(myListData.getImageuri())).into(holder.imageView);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Product_edit.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", myListData.getPid());
                context.startActivity(intent);
                ((Activity)view.getContext()).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView pname,pprice,pquantity;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
         super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pr_image);
            this.pname = (TextView) itemView.findViewById(R.id.pr_name);
            this.pprice = (TextView) itemView.findViewById(R.id.pr_price);
            this.pquantity = (TextView) itemView.findViewById(R.id.pr_quant);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}