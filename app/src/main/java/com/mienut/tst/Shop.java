package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop extends AppCompatActivity {

    ViewFlipper imgbck;
    private RecyclerView mRecycleView;
    private PopularAdaptar mAdapter;
    private DatabaseReference mDaataRef;

    FirebaseFirestore fStore;
    private List<Popular> mPopular;

    Map<String,Object> user = new HashMap<>();

    CardView cm,cc,ck;
    String type="Milk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        imgbck = findViewById(R.id.imgback);
        int sliders[] ={R.drawable.ic_email_black_24dp, R.drawable.ic_local_phone_black_24dp, R.drawable.ic_person_black_24dp };

        cm=(CardView)findViewById(R.id.cm) ;
        cc=(CardView)findViewById(R.id.cc);
        ck=(CardView)findViewById(R.id.ck);



        fStore = FirebaseFirestore.getInstance();

        for(int imag:sliders)
        {
            bannerflip(imag);
        }

        cm.setCardBackgroundColor(Color.parseColor("#1203A9F4"));

        cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Milk";
                cc.setCardBackgroundColor(Color.TRANSPARENT);
                cm.setCardBackgroundColor(Color.parseColor("#1203A9F4"));
                showPopularProducts();
            }
        });
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Tender Coconut";
                cm.setCardBackgroundColor(Color.TRANSPARENT);
                cc.setCardBackgroundColor(Color.parseColor("#1203A9F4"));
                showPopularProducts();

            }
        });

        showPopularProducts();


    }
    public void bannerflip(int image)
    {
        ImageView im = new ImageView(this);
        im.setImageResource(image);
        imgbck.addView(im);
        imgbck.setFlipInterval(6000);
        imgbck.setAutoStart(true);
    }
    public void showPopularProducts()
    {

        mPopular = new ArrayList<Popular>();
        mDaataRef = FirebaseDatabase.getInstance().getReference("products");

        mDaataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postsnap:dataSnapshot.getChildren())
                {
                     Popular plr =postsnap.getValue(Popular.class);
                     if(type.equals(plr.getType()) ) {
                         mPopular.add(plr);
                     }

                   // System.out.println(popular.getProd_name()+"PRODUCTTTTTTTTTTTTTTTTT");
                }
                mAdapter = new PopularAdaptar(mPopular);
                mRecycleView = findViewById(R.id.recycle_view);
                mRecycleView.setHasFixedSize(true);
                mRecycleView.setLayoutManager(new GridLayoutManager(Shop.this, 3, GridLayoutManager.VERTICAL, false));
                mRecycleView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Shop.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
