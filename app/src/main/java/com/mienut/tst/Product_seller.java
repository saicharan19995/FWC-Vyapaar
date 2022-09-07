package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Product_seller extends AppCompatActivity {


    private DatabaseReference mDaataRef;
    FirebaseAuth fAuth;
    String user;
    List<Popular> mpop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_seller);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();

        this.setTitle("Products");


        mDaataRef = FirebaseDatabase.getInstance().getReference("products");

        mDaataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                mpop = new ArrayList<Popular>();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Popular tst = postsnap.getValue(Popular.class);

                    if(user.equals(tst.getUsrid()))
                    {
                        mpop.add(tst);

                    }

                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcorder);
                popadapter adapter = new popadapter(mpop);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Product_seller.this));
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Product_seller.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
