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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Cart extends AppCompatActivity {

    FirebaseAuth fAuth;

    String user,usr, pr_id;
    long amt=0;
    int cnt=0,prds=0,prs=0;
    DatabaseReference ref,addref;

    Map<String, Object> userInfo,ord_prd;
    DatabaseReference cref,aref,usrref,prodd,dref,ordref,pref,usrre;
    String id, uid="l";
    List<Transaction> mtranz;
    long amt1=0, item_count=0;
    int cnt1=0;
    boolean fllg=true;
    TextView total,items,total_price,address_seller;
    boolean moving_prd=true;

    RadioButton genderradioButton;
    RadioGroup radioGroup;

    ArrayList<String> pid= new ArrayList<String>();
    ArrayList<String> qnty= new ArrayList<String>();
    UUID uniqueKey;
    Button buy_btn, map_btn;
LinearLayout l1;
    Timestamp ts;
    double longi, latit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        total=findViewById(R.id.total_items_price);
        total_price=findViewById(R.id.total_price);
        items=findViewById(R.id.total_items);
        buy_btn=findViewById(R.id.buy_btn);
        map_btn = findViewById(R.id.map_but);
        address_seller = findViewById(R.id.add_prd);
        l1 = findViewById(R.id.cd2);
        this.setTitle("Cart");
        set_page();
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);



        uniqueKey = UUID.randomUUID();
        Date date = new Date();
        //getTime() returns current time in milliseconds
        long time = date.getTime();
        //Passed the milliseconds to constructor of Timestamp class
        ts = new Timestamp(time);


        usrre = FirebaseDatabase.getInstance().getReference("Address").child(user);

        usrre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address_retriver mpd = dataSnapshot.getValue(address_retriver.class);
                String usr_name = mpd.getName();
                String usr_ph = mpd.getPhone();
                userInfo.put("user", usr_name);
                userInfo.put("phone", usr_ph);
                userInfo.put("timestamp", ts);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        userInfo = new HashMap<>();
        ord_prd = new HashMap<>();

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ref.removeValue();
                //Intent i = new Intent(Cart.this, Payment.class);
                //startActivity(i);
                put_data();

            }
        });

        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String geoUr = "http://maps.google.com/maps?q=loc:" + latit + "," + longi + " (" + "Vendor" + ")";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUr));
                startActivity(intent);
            }
        });

    }

    void set_page()
    {


        usrref = FirebaseDatabase.getInstance().getReference("cart").child(user);

        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mtranz = new ArrayList<Transaction>();
                item_count = dataSnapshot.getChildrenCount();

                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Transaction tst = postsnap.getValue(Transaction.class);

                    amt=amt+Long.parseLong(tst.getQuantity())*tst.getAmount();
                    cnt=cnt+Integer.parseInt(tst.getQuantity());
                    fllg=false;
                    mtranz.add(tst);
                    uid = tst.getPuid();
                    pr_id = postsnap.getKey();

                }
                total.setText("RS. "+String.valueOf(amt)+"/-");
                total_price.setText("RS. "+String.valueOf(amt)+"/-");
                items.setText("Price("+cnt+") items");

                if(cnt==0)
                {
                    l1.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.GONE);
                    buy_btn.setVisibility(View.GONE);

                }

                if(cnt!=0) {
                    l1.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                    buy_btn.setVisibility(View.VISIBLE);
                    addref = FirebaseDatabase.getInstance().getReference("Address").child(uid);

                    DatabaseReference mDaataRef = FirebaseDatabase.getInstance().getReference("products").child(pr_id);
                    mDaataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Popular popula = dataSnapshot.getValue(Popular.class);
                            if(popula.getMobility().equals("Cart(Moving)"))
                            {
                                latit =popula.getLatitude();
                                longi = popula.getLongitude();
                                address_seller.setVisibility(View.GONE);
                                moving_prd=false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    addref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                            if(moving_prd) {
                                address_seller.setText(pd.getFlat());
                                latit = pd.getLatitute();
                                longi = pd.getLongitutde();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cartrc);
                CartAdapter adapter = new CartAdapter(mtranz);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
                recyclerView.setAdapter(adapter);
                amt=0;
                cnt=0;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    void put_data() {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        genderradioButton = (RadioButton) findViewById(selectedId);
        if (selectedId == -1) {
            Toast.makeText(Cart.this, "Select PickUp or Delivery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Cart.this, genderradioButton.getText(), Toast.LENGTH_SHORT).show();



        aref = FirebaseDatabase.getInstance().getReference("transaction").child(user).child(uniqueKey.toString());
        Map<String, Object> u = new HashMap<>();
        u.put("orderid", uniqueKey.toString());
        u.put("time", ts);
        aref.setValue(u);

        ref = FirebaseDatabase.getInstance().getReference("cart").child(user);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Transaction tst = postsnap.getValue(Transaction.class);

                    pid.add(tst.getProduct_id());
                    qnty.add(tst.getQuantity());
                    amt1 = amt1 + Long.parseLong(tst.getQuantity()) * tst.getAmount();
                    cnt1 = cnt1 + Integer.parseInt(tst.getQuantity());

                    final String otp = String.valueOf(OTP());
                    dref = FirebaseDatabase.getInstance().getReference("products").child(tst.getProduct_id());
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Popular plr = dataSnapshot.getValue(Popular.class);
                            if (prds == 0) {
                                if (item_count == 1) {
                                    userInfo.put("name", plr.getProd_name());
                                } else {
                                    userInfo.put("name", plr.getProd_name() + " + " + (item_count - 1) + " more");
                                }
                                userInfo.put("uid", plr.getUsrid());
                                userInfo.put("imageuri", plr.getImageuri());
                                userInfo.put("orderid", uniqueKey.toString());
                                userInfo.put("userid", user);
                                userInfo.put("timestamp", ts);
                                userInfo.put("quantity", String.valueOf(cnt1));
                                userInfo.put("amount", amt1);
                                userInfo.put("payment", "Nil");
                                userInfo.put("status", "Pending");
                                userInfo.put("rating", "null");
                                userInfo.put("otp", otp);
                                String asdf  = (String) genderradioButton.getText();
                                userInfo.put("ordertype", asdf.replaceAll(" ",""));
                                prodd = FirebaseDatabase.getInstance().getReference("order_summary").child(String.valueOf(uniqueKey));
                                prodd.setValue(userInfo);
                                pref = FirebaseDatabase.getInstance().getReference("producer_view").child(plr.getUsrid()).child(uniqueKey.toString());
                                Map<String, Object> uk = new HashMap<>();
                                uk.put("orderid", uniqueKey.toString());
                                uk.put("time", ts);
                                pref.setValue(uk);
                            }

                            prds++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                System.out.println("PPPPPPID " + qnty);
                prs = 0;
                for (final String ppis : pid) {
                    dref = FirebaseDatabase.getInstance().getReference("products").child(ppis);
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Popular plr = dataSnapshot.getValue(Popular.class);
                            String pname, imageuri;
                            pname = plr.getProd_name() + " " + plr.getUnit();
                            imageuri = plr.getImageuri();
                            ordref = FirebaseDatabase.getInstance().getReference("order_detials").child(String.valueOf(uniqueKey)).child(ppis);
                            ord_prd.put("name", pname);
                            ord_prd.put("quantity", qnty.get(prs));
                            ord_prd.put("imageuri", imageuri);
                            ord_prd.put("amount", Long.parseLong(plr.getProd_price()));
                            ordref.setValue(ord_prd);
                            prs++;

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


                cref = FirebaseDatabase.getInstance().getReference("cart").child(user);
                cref.setValue(null);

                Intent i2 = new Intent(Cart.this, Accept_prod.class);
                startActivity(i2);
                finish();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Cart.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    }
    static char[] OTP()
    {

        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();
        int len=4;
        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return otp;
    }


}
