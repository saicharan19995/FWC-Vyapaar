package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Accept_prod extends AppCompatActivity {

    FirebaseAuth fAuth;
    String user;
    DatabaseReference ref;

    String id;
    List<OrderByTIme> mtranz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_accept_prod);

        this.setTitle("Orders");


        ref = FirebaseDatabase.getInstance().getReference("transaction").child(user);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mtranz = new ArrayList<OrderByTIme>();

                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    OrderByTIme tst = postsnap.getValue(OrderByTIme.class);

                    mtranz.add(tst);

                }
                Collections.sort(mtranz, new Comparator<OrderByTIme>() {
                    @Override
                    public int compare(OrderByTIme u1, OrderByTIme u2) {
                        return u2.getTime().compareTo(u1.getTime());
                    }
                });
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcorder);
                TransactionAdapterCust adapter = new TransactionAdapterCust(mtranz);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Accept_prod.this));
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Accept_prod.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Intent i3 = new Intent(Accept_prod.this, Main2Activity.class);
        //startActivity(i3);
        finish();
    }




    public void mapso(final View view, String lk, final long amt )
    {
        ref = FirebaseDatabase.getInstance().getReference("Address").child(lk);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                double latitude = pd.getLatitute();
                double longitude = pd.getLongitutde();
                System.out.println("Cityyyyyyyyyy  " + pd.getFlat());


                if (latitude != 0 && longitude != 0) {
                    final String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Vendor" + ")";


                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                    passwordResetDialog.setTitle("Order Placed");
                    passwordResetDialog.setMessage("Collect from Address:\n\n"+pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity() + "\nPhone number - " + pd.getPhone() +"\n\nTotal Payable Amount is RS "+amt);


                    passwordResetDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);*/

                        }
                    });

                    passwordResetDialog.create().show();

                } else {
                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                    passwordResetDialog.setTitle("Order Placed");
                    passwordResetDialog.setMessage("Collect from Address:\n\n"+pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity() + " " + pd.getPhone() +"\n\nTotal Payable Amount is RS "+amt);

                    final String add = pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity();

                    passwordResetDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                     /*       String geoUrii = "http://maps.google.com/maps?q=loc:" + add;
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUrii));
                            startActivity(intent);*/
                        }
                    });



                    passwordResetDialog.create().show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Accept_prod.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

