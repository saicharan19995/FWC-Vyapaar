package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class Order_details extends AppCompatActivity {

    FirebaseAuth fAuth;
    String user;
    DatabaseReference ref,orddtl, refaddr;

    String id,usrid, prdid;
    List<Transaction> mtranz;
    TextView total,items,total_price;
    Button map_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        total=findViewById(R.id.total_items_price);
        total_price=findViewById(R.id.total_price);
        items=findViewById(R.id.total_items);
        map_btn = findViewById(R.id.map_btn);
        this.setTitle("Order Details");
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        set_page();


        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refaddr  = FirebaseDatabase.getInstance().getReference("products").child(prdid);
                refaddr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Popular pd = dataSnapshot.getValue(Popular.class);
                        double latitude = pd.getLatitude();
                        double longitude = pd.getLatitude();
                        if (latitude != 0 && longitude != 0) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Vendor" + ")";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            });





    }

    void set_page()
    {


        ref = FirebaseDatabase.getInstance().getReference("order_detials").child(id);

        orddtl = FirebaseDatabase.getInstance().getReference("order_summary").child(id);

        orddtl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Transaction tst = dataSnapshot.getValue(Transaction.class);

                total.setText("RS "+String.valueOf(tst.getAmount())+"/-");
                total_price.setText("RS "+String.valueOf(tst.getAmount())+"/-");
                items.setText("Price("+tst.getQuantity()+") items");
                usrid= tst.getUid();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mtranz = new ArrayList<Transaction>();

                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Transaction tst = postsnap.getValue(Transaction.class);
                    mtranz.add(tst);
                    prdid = postsnap.getKey().toString();

                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cartrc);

                    OrderDetailsSeller adapter = new OrderDetailsSeller(mtranz);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Order_details.this));
                    recyclerView.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Order_details.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
