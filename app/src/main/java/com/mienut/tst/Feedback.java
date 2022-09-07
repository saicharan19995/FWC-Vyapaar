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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class Feedback extends AppCompatActivity {

    FirebaseAuth fAuth;
    String user;
    DatabaseReference ref,orddtl,feed;
    String id,usrid, prdid;
    List<Transaction> mtranz;
    TextView total,items,total_price;
    Button sub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        total=findViewById(R.id.total_items_price);
        total_price=findViewById(R.id.total_price);
        items=findViewById(R.id.total_items);
        sub = findViewById(R.id.sub_btn);
        this.setTitle("Order Details");
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        feed = FirebaseDatabase.getInstance().getReference("order_summary").child(id);
        set_page();
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("rating", "done");
                feed.updateChildren(hopperUpdates);
                finish();
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
                    tst.setProduct_id(postsnap.getKey());
                    mtranz.add(tst);
                    prdid = postsnap.getKey().toString();


                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cartrc);

                    OrderDetailsAdapter adapter = new OrderDetailsAdapter(mtranz);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Feedback.this));
                    recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Feedback.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
