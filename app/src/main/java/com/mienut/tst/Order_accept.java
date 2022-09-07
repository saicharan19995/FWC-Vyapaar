package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

public class Order_accept extends AppCompatActivity {

    FirebaseAuth fAuth;
    String user;
    DatabaseReference ref, oref;
    String id;
    List<Transaction> mtranz;
    List<OrderByTIme> mOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_order_accept);
        this.setTitle("Orders");





        mOrders = new ArrayList<OrderByTIme>();

        mtranz = new ArrayList<Transaction>();
                        ref = FirebaseDatabase.getInstance().getReference("producer_view").child(user);

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                    OrderByTIme ord = postsnap.getValue(OrderByTIme.class);

                                    mOrders.add(ord);

                                }
                               Collections.sort(mOrders, new Comparator<OrderByTIme>() {
                                    @Override
                                    public int compare(OrderByTIme u1, OrderByTIme u2) {
                                        return u2.getTime().compareTo(u1.getTime());
                                    }
                                });


                                for( OrderByTIme odr: mOrders)
                                {
                                    final String oid = odr.getOrderid();

                                    oref = FirebaseDatabase.getInstance().getReference("order_summary");
                                    oref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                                if(postsnap.getKey().equals(oid)){
                                                Transaction tst = postsnap.getValue(Transaction.class);
                                                tst.setPuid(postsnap.getKey());
                                                mtranz.add(tst);}

                                            }

                                            //System.out.println("OOOOOOORD  "+mtranz);
                                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcorder);
                                            TransactionAdapter adapter = new TransactionAdapter(mtranz);
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(Order_accept.this));
                                            recyclerView.setAdapter(adapter);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(Order_accept.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });




                    }
                }


