package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Product_Display extends AppCompatActivity {


    ViewFlipper imgbck;
    private DatabaseReference mDaataRef,ref,trans,prodd,usrref, cartref, addref;
//    int coco[] ={R.drawable.c1, R.drawable.c2, R.drawable.c3 };
    String str,nm;
//    int milk[] ={R.drawable.m1, R.drawable.m2, R.drawable.m3 };
    Button buy,addcart , map_but;
    Context context;
    String usrid, add , prz;
    long quax;
    EditText qunx;
    ImageView picture;
    boolean flm=false, flg=false;

    Double pppp;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;
    Timestamp ts;
    String geoUri,mtttp;
    String cust ="Customer";
    boolean custt=false;
    String imageuri;

    double longi, latit;
    int z=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__display);

        Intent intent = getIntent();
        str = intent.getStringExtra("id");
        context= getApplicationContext();

        picture=findViewById(R.id.picture);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        qunx =findViewById(R.id.quanx);
        userID = fAuth.getCurrentUser().getUid();
        map_but = findViewById(R.id.map_but);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Product Details");

        documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    if (cust.equals(documentSnapshot.getString("type"))) {

                        custt = true;
                    }
                }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }

            });



        buy = findViewById(R.id.buy);
        addcart = findViewById(R.id.addtocart);
System.out.println("IIIIIIIIIIIIIDDDDDDD   "+str);
        mDaataRef = FirebaseDatabase.getInstance().getReference("products");

        showProduct();


        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (Long.parseLong(qunx.getText().toString().trim()) > quax) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Product_Display.this);
                    dialog.setMessage("Quantity");
                    dialog.setTitle("Quantity not in stock. Quantity present is " + quax);
                    dialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_LONG).show();


                                }
                            });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                } else {

                    System.out.println("PPPPPPPPPPPPPPPP  " + Double.parseDouble(qunx.getText().toString().trim()) + " kdkd " + prz);
                    pppp = Double.parseDouble(prz) * Double.parseDouble(qunx.getText().toString().trim());
                    Date date = new Date();
                    //getTime() returns current time in milliseconds
                    long time = date.getTime();
                    //Passed the milliseconds to constructor of Timestamp class
                    ts = new Timestamp(time);
                    UUID uniqueKey = UUID.randomUUID();
                    final Map<String, Object> userInfo = new HashMap<>();

                    trans = FirebaseDatabase.getInstance().getReference("transaction").child(userID).child(String.valueOf(uniqueKey));

                    Map<String, Object> user = new HashMap<>();
                    user.put("product_id", str);
                    user.put("name", nm);
                    user.put("quantity", qunx.getText().toString().trim());
                    user.put("type", mtttp);
                    user.put("timestamp", ts);
                    user.put("amount", pppp);
                    user.put("puid",usrid);
                    user.put("uid",String.valueOf(uniqueKey));
                    user.put("status", "Pending");
                    trans.setValue(user);

                    userInfo.put("name", nm);
                    userInfo.put("type", mtttp);
                    userInfo.put("product_id", str);
                    userInfo.put("userid", userID);
                    userInfo.put("quantity", qunx.getText().toString().trim());
                    userInfo.put("uid", String.valueOf(uniqueKey));
                    userInfo.put("status", "Pending");


                    prodd = FirebaseDatabase.getInstance().getReference("producer_view").child(str).child(String.valueOf(uniqueKey));


                    usrref = FirebaseDatabase.getInstance().getReference("Address").child(userID);

                    usrref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            address_retriver mpd = dataSnapshot.getValue(address_retriver.class);
                            String usr_name = mpd.getName();
                            String usr_ph = mpd.getPhone();
                            userInfo.put("user", usr_name);
                            userInfo.put("phone", usr_ph);
                            userInfo.put("timestamp", ts);
                            userInfo.put("amount", pppp);
                            prodd.setValue(userInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Product_Display.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                    //mapso(view);
                   // startActivity(new Intent(getApplicationContext(),Accept_prod.class));

                    ref = FirebaseDatabase.getInstance().getReference("Address").child(usrid);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                            double latitude = pd.getLatitute();
                            double longitude = pd.getLongitutde();
                            System.out.println("Cityyyyyyyyyy  " + pd.getFlat());


                            if (latitude != 0 && longitude != 0) {
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Vendor" + ")";


                                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                                passwordResetDialog.setTitle("Order Placed");
                                passwordResetDialog.setMessage("Check status & Collect from Address:\n\n"+pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity() + "\nPhone number - " + pd.getPhone() +"\n\nTotal Payable Amount is RS "+pppp);


                                passwordResetDialog.setPositiveButton("Open map", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
                                        startActivity(intent);

                                    }
                                });

                                passwordResetDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(custt)
                                        {Intent intent = new Intent(Product_Display.this, Main2Activity.class);
                                            intent.putExtra("type", cust);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else{
                                            Intent i = new Intent(Product_Display.this, Main2Activity.class);
                                            startActivity(i);}
                                    }
                                });

                                passwordResetDialog.create().show();

                            } else {
                                //final EditText resetMail = new EditText(view.getContext());
                                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                                passwordResetDialog.setTitle("Order Placed");
                                passwordResetDialog.setMessage("Collect from Address:\n\n"+pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity() + " " + pd.getPhone() +"\n\nTotal Payable Amount is RS "+pppp);
                                //passwordResetDialog.setView(resetMail);
                                add = pd.getFlat() + " " + pd.getLocality() + " " + pd.getCity();

                                passwordResetDialog.setPositiveButton("Open map", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // extract the email and send reset link
                                        String geoUrii = "http://maps.google.com/maps?q=loc:" + add;
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUrii));
                                        startActivity(intent);

                                    }
                                });

                                passwordResetDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // close the dialog
                                        if(custt)
                                        {Intent intent = new Intent(Product_Display.this, Main2Activity.class);
                                            intent.putExtra("type", cust);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else{
                                            Intent i = new Intent(Product_Display.this, Main2Activity.class);
                                            startActivity(i);}
                                    }
                                });

                                passwordResetDialog.create().show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Product_Display.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });



        addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Long.parseLong(qunx.getText().toString().trim()) > quax) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Product_Display.this);
                    dialog.setMessage("Quantity");
                    dialog.setTitle("Quantity not in stock. Quantity present is " + quax);
                    dialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_LONG).show();


                                }
                            });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                } else {

                    cartref = FirebaseDatabase.getInstance().getReference("cart").child(userID);




                    cartref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         int l=0;
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                                    Transaction tst = postsnap.getValue(Transaction.class);
                                    if (tst.getPuid().equals(usrid)) {
                                        flm = true;
                                        l++;
                                    } else {
                                        flm = false;
                                    }

                                }

                            }
                            else {
                                   flg=true;
                                }
                            System.out.println("LLLLLLLLLLL "+l+" "+z);

                            if ((flm && z<=l) || (flg && z==0)) {
                                add_cart();
                                flg=false;
                                addcart.setText("In Cart");
                            }
                            else {
                                addcart.setText("Clear Cart");

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Product_Display.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                }

            }
        });

        map_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String geoUr = "http://maps.google.com/maps?q=loc:" + latit + "," + longi + " (" + "Vendor" + ")";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUr));
                startActivity(intent);
            }
        });



    }
    private  void add_cart()
    {
        trans = FirebaseDatabase.getInstance().getReference("cart").child(userID).child(str);

        Map<String, Object> user = new HashMap<>();
        user.put("product_id", str);
        user.put("quantity", qunx.getText().toString().trim());
        user.put("amount", Long.parseLong(prz));
        user.put("puid",usrid);
        flm = false;
        trans.setValue(user);
        z++;
        //Intent i5 = new Intent(Product_Display.this, Main2Activity.class);
        //startActivity(i5);
        finish();
    }


    private void showProduct() {


        mDaataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final TextView name,price,disc,addrss, mobility, ratings, no_of_ratings;;
                name = findViewById(R.id.product_title);
                price =findViewById(R.id.product_price);
                disc = findViewById(R.id.discription);
                addrss = findViewById(R.id.add_prd);
                mobility = findViewById(R.id.mobility);
                ratings = findViewById(R.id.tv_product_rating_miniview);
                no_of_ratings = findViewById(R.id.total_ratings_miniview);
                String c = "Tender Coconut";
                String m = "Milk";

                for(DataSnapshot postsnap:dataSnapshot.getChildren())
                {
                    if(str.equals(postsnap.getKey())) {
                        Popular popular = postsnap.getValue(Popular.class);

                        usrid=popular.getUsrid();
                        nm=popular.getProd_name();
                        name.setText(popular.getProd_name()+" "+popular.getUnit());
                        mobility.setText(popular.getMobility());
                        System.out.println("OUOUOUIUIIOOIOUIIIO     " + popular.getProd_name());
                        price.setText("RS " + popular.getProd_price());


                        //if(!popular.getBrand().equals("mlk") && !popular.getMilk_type().equals("mlk") && !popular.getDiscription().equals(""))
                        //{disc.setText(popular.getBrand() +" "+ popular.getMilk_type() +" "+ popular.getDiscription() );}
                        quax= popular.getQuantity();
                        prz = popular.getProd_price();
                        disc.setText(popular.getDiscription());
                        ratings.setText(String.valueOf(popular.getStars()));
                        no_of_ratings.setText(String.valueOf(popular.getPeople()));

                        mtttp=popular.getType();
                        imageuri=popular.getImageuri();

                        Picasso.get().load(Uri.parse(popular.getImageuri())).into(picture);

                        latit =popular.getLatitude();
                        longi = popular.getLongitude();

                        if(popular.getMobility().equals("Cart(Moving)")){
                            addrss.setVisibility(View.GONE);
                        }
                        else{
                        addref = FirebaseDatabase.getInstance().getReference("Address").child(popular.getUsrid());

                        addref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                                addrss.setText(pd.getFlat());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(Product_Display.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        }


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Product_Display.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.cart_action);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart").child(userID);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                menuItem.setIcon(Converter.convertLayoutToImage(Product_Display.this, (int) count, R.drawable.ic_action_cart_24));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.cart_action:
                Intent i5 = new Intent(Product_Display.this, Cart.class);
                startActivity(i5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
