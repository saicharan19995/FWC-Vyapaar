package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Payment extends AppCompatActivity {

    CardView cash;

    FirebaseAuth fAuth;
    String user,usr;
    long amt=0,ttl=0;
    int cnt=0,prds=0,prs=0;
    DatabaseReference ref,cref,aref,usrref,prodd,dref,ordref,pref,payref,amtref,dre;
    Timestamp ts;
    Map<String, Object> userInfo,ord_prd;
    UUID uniqueKey;

    EditText amountEt, noteEt, nameEt, upiIdEt;
    CardView send;
    boolean flg = false;
    String id;

    final int UPI_PAYMENT = 0;

    ArrayList<String> pid= new ArrayList<String>();
    ArrayList<String> qnty= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cash = findViewById(R.id.cash);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        send = findViewById(R.id.send);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        userInfo = new HashMap<>();
        ord_prd = new HashMap<>();



        /*usrref = FirebaseDatabase.getInstance().getReference("Address").child(user);

        usrref.addValueEventListener(new ValueEventListener() {
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

                Toast.makeText(Payment.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        amtref = FirebaseDatabase.getInstance().getReference("cart").child(user);

        amtref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Transaction tst = postsnap.getValue(Transaction.class);
                    ttl = ttl + Long.parseLong(tst.getQuantity()) * tst.getAmount();
                    usr = tst.getPuid();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        amtref = FirebaseDatabase.getInstance().getReference("order_summary").child(id);
        amtref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Transaction tst = dataSnapshot.getValue(Transaction.class);
                usr = tst.getUid();
                ttl= tst.getAmount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("payment", "Cash");
                amtref.updateChildren(hopperUpdates);
                update_data();


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    payref = FirebaseDatabase.getInstance().getReference("Address").child(usr);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Warning");
                passwordResetDialog.setMessage("No refund made and cannot cancel order.");
                    payref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final address_retriver pd = dataSnapshot.getValue(address_retriver.class);

                            passwordResetDialog.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    payUsingUpi(String.valueOf(ttl), pd.getUpiid(), pd.getUpiname(), "VaviMart Payment");
                                }
                            });

                            passwordResetDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            passwordResetDialog.create().show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Payment.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });



                }


        });
    }



    void update_data()
    {
        Intent i3 = new Intent(Payment.this, Accept_prod.class);
        startActivity(i3);
        finish();

    }

    void put_data()
    {
        Date date = new Date();
        //getTime() returns current time in milliseconds
        long time = date.getTime();
        //Passed the milliseconds to constructor of Timestamp class
        ts = new Timestamp(time);

        uniqueKey = UUID.randomUUID();
        aref = FirebaseDatabase.getInstance().getReference("transaction").child(user).child(uniqueKey.toString());
        Map<String, Object> u = new HashMap<>();
        u.put("orderid",uniqueKey.toString());
        u.put("time",ts);
        aref.setValue(u);

        ref = FirebaseDatabase.getInstance().getReference("cart").child(user);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Transaction tst = postsnap.getValue(Transaction.class);

                    pid.add(tst.getProduct_id());
                    qnty.add(tst.getQuantity());
                    amt = amt + Long.parseLong(tst.getQuantity()) * tst.getAmount();
                    cnt = cnt + Integer.parseInt(tst.getQuantity());


                    dref = FirebaseDatabase.getInstance().getReference("products").child(tst.getProduct_id());
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Popular plr = dataSnapshot.getValue(Popular.class);
                            if(prds==0)
                            {
                                userInfo.put("name", plr.getProd_name() );
                                userInfo.put("uid", plr.getUsrid());
                                userInfo.put("imageuri",plr.getImageuri());
                                userInfo.put("orderid",uniqueKey.toString());
                                userInfo.put("userid",user);
                                userInfo.put("timestamp",ts);
                                userInfo.put("quantity", String.valueOf(cnt));
                                userInfo.put("amount", amt);
                                if(flg)
                                {
                                    userInfo.put("payment","UPI Payment");
                                    userInfo.put("status", "Confirm");
                                }
                                else{
                                userInfo.put("payment","Cash");
                                    userInfo.put("status", "Pending");}
                                prodd = FirebaseDatabase.getInstance().getReference("order_summary").child(String.valueOf(uniqueKey));
                                prodd.setValue(userInfo);
                                pref= FirebaseDatabase.getInstance().getReference("producer_view").child(plr.getUsrid()).child(uniqueKey.toString());
                                Map<String, Object> uk = new HashMap<>();
                                uk.put("orderid",uniqueKey.toString());
                                uk.put("time",ts);
                                pref.setValue(uk);
                            }

                            prds++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                System.out.println("PPPPPPID "+qnty);
                prs=0;
                for(final String ppis:pid){
                    dref = FirebaseDatabase.getInstance().getReference("products").child(ppis);
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Popular plr = dataSnapshot.getValue(Popular.class);
                            String pname,imageuri;
                            pname=  plr.getProd_name()+" "+plr.getUnit();
                            imageuri = plr.getImageuri();
                            ordref = FirebaseDatabase.getInstance().getReference("order_detials").child(String.valueOf(uniqueKey)).child(ppis);
                            ord_prd.put("name",pname);
                            ord_prd.put("quantity",qnty.get(prs));
                            ord_prd.put("imageuri",imageuri);
                            ord_prd.put("amount",Long.parseLong(plr.getProd_price()));
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

                    Intent i2 = new Intent(Payment.this, Accept_prod.class);
                    startActivity(i2);




            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Payment.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

    }

    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("yo", "Success!");
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Payment.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Payment.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Payment.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
                flg = true;
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("payment", "UPI Payment");
                amtref.updateChildren(hopperUpdates);
                update_data();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(Payment.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Payment.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Payment.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }


}
