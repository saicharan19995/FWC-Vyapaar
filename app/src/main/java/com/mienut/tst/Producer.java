package com.mienut.tst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Producer extends AppCompatActivity {

    CardView profile,product_view,customer_view,add_product,view_address,log_out;
    TextView name;
    FirebaseFirestore fStore;
    DocumentReference userref;
    FirebaseAuth fAuth;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);
        profile=findViewById(R.id.proflie);
        view_address=findViewById(R.id.address);
        product_view=findViewById(R.id.products_view);
        customer_view=findViewById(R.id.view_customer);
        add_product=findViewById(R.id.add_product);
        log_out=findViewById(R.id.log_out);
        name=findViewById(R.id.usr_name);

        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();
        user_id = fAuth.getCurrentUser().getUid();


        userref = fStore.collection("users").document(user_id);
        userref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    name.setText(documentSnapshot.getString("fName"));

                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Producer.this, MainActivity.class);
                startActivity(i);
            }
        });
               add_product.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent i = new Intent(Producer.this, Product.class);
                       startActivity(i);
                   }
               });
               log_out.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       FirebaseAuth.getInstance().signOut();//logout
                       startActivity(new Intent(getApplicationContext(),Login.class));
                       finish();
                   }
               });

               view_address.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent i = new Intent(Producer.this, MapsActivityCurrentPlace.class);
                       startActivity(i);
                   }
               });
               product_view.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent i = new Intent(Producer.this, Producer_prods.class);
                       //i.putExtra("id", "product");
                       startActivity(i);
                   }
               });
        customer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Producer.this, Order_accept.class);
                //i.putExtra("id", "customer");
                startActivity(i);
            }
        });
    }
}
