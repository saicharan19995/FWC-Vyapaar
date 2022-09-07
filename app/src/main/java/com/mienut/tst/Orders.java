package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Orders extends AppCompatActivity {


    private DatabaseReference mDaataRef, ref;
    private ListView listView;
    FirebaseAuth fAuth;
    String user;
    String name;
    String id, strInd;
    ArrayList<String> str = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String cust="customer";
    String product="product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        listView = findViewById(R.id.orders);

        Intent intent = getIntent();
        strInd = intent.getStringExtra("id");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        listView.setAdapter(arrayAdapter);


        if (strInd == null) {
            mDaataRef = FirebaseDatabase.getInstance().getReference("transaction/" + user);
            //arrayAdapter.notifyDataSetChanged();
            mDaataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                        Transaction tst = postsnap.getValue(Transaction.class);

                        id = tst.getProduct_id();
                        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        final Date dt = tst.getTimestamp();
                        String naame = tst.getName();
                        str.add(naame);
                        str.add(tst.getStatus());
                        str.add("RS "+String.valueOf(tst.getAmount()));
                        str.add(dateFormat.format(dt));

                        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMM  " + tst.getStatus());

                        arrayAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Orders.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

        if(product.equals(strInd))
        {
            mDaataRef = FirebaseDatabase.getInstance().getReference("products");
            //arrayAdapter.notifyDataSetChanged();
            mDaataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                        Popular tst = postsnap.getValue(Popular.class);
                        if(user.equals(tst.getUsrid()))
                        {

                            str.add(tst.getProd_name());
                            str.add(tst.getProd_price());
                        }

                        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMM  " + str);

                        arrayAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Orders.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if(cust.equals(strInd))
        {
            mDaataRef = FirebaseDatabase.getInstance().getReference("products");
            //arrayAdapter.notifyDataSetChanged();
            mDaataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                        Popular tst = postsnap.getValue(Popular.class);
                        if(user.equals(tst.getUsrid()))
                        {
                            String id=postsnap.getKey();
                            str.add(tst.getProd_name());
                            str.add(tst.getProd_price());
                            ref = FirebaseDatabase.getInstance().getReference("producer_view").child(id);
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ds) {

                                    for (DataSnapshot ps : ds.getChildren())
                                    {   Transaction ts = ps.getValue(Transaction.class);
                                    str.add(ts.getUser());
                                    str.add(ts.getPhone());
                                    str.add(ts.getStatus());
                                    str.add("RS "+String.valueOf(ts.getAmount()));

                                        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMM  " + ts.getPhone()+ts.getUser());
                                    final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    final Date dt = ts.getTimestamp();
                                    str.add(dateFormat.format(dt));

                                        arrayAdapter.notifyDataSetChanged();
                                }}
                                @Override
                                public void onCancelled(@NonNull DatabaseError de) {

                                    Toast.makeText(Orders.this, de.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMM  " + str);

                        arrayAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Orders.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
