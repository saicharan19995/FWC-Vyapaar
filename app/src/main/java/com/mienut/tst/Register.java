package com.mienut.tst;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    String prod= "Producer";
    String cust ="Customer";
    String sell = "Seller";
    String buyer = "User";
    String retailer = "Retailer";
    String type;
    boolean agreed = false;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName   = findViewById(R.id.fullName);
        mEmail      = findViewById(R.id.Email);
        mPassword   = findViewById(R.id.password);
        mPhone      = findViewById(R.id.phone);
        mRegisterBtn= findViewById(R.id.registerBtn);
        mLoginBtn   = findViewById(R.id.createText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);



        final Spinner dropdown = findViewById(R.id.type);
        //String[] items = new String[]{"Customer", "Retailer", "Producer"};
        String[] items = new String[]{"Customer", "Producer"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter1);
        terms_conditions();


      /*  if(fAuth.getCurrentUser() != null){

            userID = fAuth.getCurrentUser().getUid();
            final DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        if(prod.equals(documentSnapshot.getString("type")))
                        {
                            startActivity(new Intent(getApplicationContext(),Producer.class));
                            finish();
                        }
                        else if(cust.equals(documentSnapshot.getString("type")))
                        {
                            Intent intent = new Intent(Register.this, Main2Activity.class);
                            intent.putExtra("type", cust);
                            startActivity(intent);
                            finish();
                        }
                        else{

                            startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                            finish();
                        }
                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }*/



        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mPhone.getText().toString().trim().length()<10){
                    mPhone.setError("Phone Number Required");
                }

            }
        });





        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone    = fAuth.getCurrentUser().getPhoneNumber();
                type = String.valueOf(dropdown.getSelectedItem());

                /*if(sell.equals(String.valueOf(dropdown.getSelectedItem())))
                {type = "Producer";}
                if (buyer.equals(String.valueOf(dropdown.getSelectedItem())))
                {
                    type = "Customer";
                }
                if (retailer.equals(String.valueOf(dropdown.getSelectedItem())))
                {
                    type = "Retailer";
                }*/

                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Name is Required.");
                    return;
                }




                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String,Object> user = new HashMap<>();
                user.put("fName",fullName);
                user.put("email",email);
                user.put("phone",phone);
                user.put("type",type);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
                startActivity(new Intent(getApplicationContext(),Address.class));



                //For phonen verification

//                Intent intent = new Intent(Register.this,VerifyPhoneActivity.class);
//                intent.putExtra("mobile",phone);
//                intent.putExtra("email",email);
//                intent.putExtra("password",password);
//                intent.putExtra("fullname",fullName);
//                intent.putExtra("type",type);
//                startActivity(intent);

//                progressBar.setVisibility(View.VISIBLE);
//
//                // register the user in firebase
//
//                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//
//                            // send verification link
//
//                            FirebaseUser fuser = fAuth.getCurrentUser();
//                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
//                                }
//                            });
//
//                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
//
//
//                            userID = fAuth.getCurrentUser().getUid();
//                            DocumentReference documentReference = fStore.collection("users").document(userID);
//                            Map<String,Object> user = new HashMap<>();
//                            user.put("fName",fullName);
//                            user.put("email",email);
//                            user.put("phone",phone);
//                            user.put("type",type);
//                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: " + e.toString());
//                                }
//                            });
//                            startActivity(new Intent(getApplicationContext(),Address.class));
//
//                        }else {
//                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });

            }
        });



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
    private void terms_conditions() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(Register.this);
        dialog.setMessage("Introduction \n" +
                "VaviMart is an application which helps to establish the direct connection between buyers and sellers avoiding intermediate’s in middle.\n" +
                "Sellers - The parties who tries to export their goods using interface as application.\n" +
                "Retailers - The parties who tries both import and export the goods between the seller and customer.\n" +
                "Customer - The parties who tries to import their goods either from retailer and seller.\n" +
                "The terms and condition apply to all the buyers and sellers to set forth all the conditions with concerning sales and goods covered by the agreement issued in this document.\n" +
                "\n" +
                "Basis of sale \n" +
                "Basis of sales refers to the point that is been used in unit of measurement, that is per unit prices. In our application we try to measure the products using unit of measurement for each product with providence of the quantity and price accordance with the products and goods.\n" +
                "Basically the seller sets the price with respective to the product adapting the measurement as “per unit”. \n" +
                "\n" +
                "Orders and specifications\n" +
                " Business of buying in which buyer writes the  final order for the acceptance of the order from the seller, where the seller sets the price, quantity and appearance of the goods in their profile using the vavimart application. The application just acts as bridge or intermediate connection between the buyer and seller.\n" +
                "The seller specifies the units of goods based on their specification of that particular goods he/she wishes to sale and application is not responsible over the unit price specified by the seller/retailer.\n" +
                "The buyer must accept the sales of goods if order has been placed from any particular seller, listing the technical specification and other parameters. Our application is not responsible for any kind of misinterpretation.\n" +
                "Once the order has been processed the seller/retailer confirms the order, the buyer can proceed with the mode of payment with their convenience, after the payment the OTP will be generated in the buyers account, only after the otp acceptance, the status changes to the product delivered.\n" +
                "\n" +
                "Price of the goods\n" +
                "Price is defined as the exchange of goods between buyers and sellers in terms of money. Substitute of goods are the products which satisfy all the needs of the product to be consumed. The price of the goods will be given with per unit in measurement. The buyer must have a look over the price before ordering the product, because once the amount has been paid the order cannot be cancelled.\n" +
                "Price of the product or service is what seller feels it is worth to buy the goods from seller. The marketing of goods is based on the unit price with the firm sets a price on each product by the seller/retailer. There won’t be any discounts on the products for buyers if it is not mentioned by the seller.\n" +
                "\n" +
                "Delivery\n" +
                "The delivery of the goods mainly depends on the Seller/Retailer, where the Seller selects an option whether the seller/retailer is static or moving, if the seller/retailer is moving then the user can track the location using map option, else the map option gives the address of the user.\n" +
                "\n" +
                "Terms of payment\n" +
                "The condition under which seller will complete the order only when the buyer selects the mode of payment either with cash on delivery or online payment (UPI Payment), if the buyer selects the payment mode on cash delivery then he/she must pay the cash when order has been received. When the buyer chooses the UPI payment then he/she must pay the amount using internet banking with their convenient to the seller, once the payment has been done no return of products can be taken by the buyer. That means if the product is sold once the amount cannot be refunded back to the buyer from the seller.\n" +
                "\n" +
                "Risk and property\n" +
                "A probability of injuries, financial loss, and threat occurred caused by internal and external vulnerabilities from the product, or any other negative occurrence with respective to the product between the seller and buyer transactions will not be blamed on the application. Since applications acts as a bridge between the connection of buyer and seller, the application is not responsible for any risk on the property.\n" +
                "Adds post on the application will be purely responsible over the user, the application is not possible for any endanger occurs.\n" +
                "\n" +
                "Warranties and liability\n" +
                "The product or goods will be fixed with standard price from the seller/retailer in unit of measurement. The warranties regarding the purchase of goods or products from the seller or order from the buyer purely based on the customer –producer relationship, the application doesn’t give any warranties or liability with respect any import and export of the goods under the application. The condition under which seller complete the sale only when the buyer proceeds with generated OTP with the seller. The application acts as an interface between the exporting the goods with the users who have registered with our application.\n");
        dialog.setTitle("Terms and Conditions");
        dialog.setCancelable(false);
       /* String[] items = {"java"};
        boolean[] checkedItems = {false};
        dialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if(isChecked)
                            Toast.makeText(Register.this, "Clicked on java", Toast.LENGTH_LONG).show();
                        agreed=true;
                        check_box();
                        break;

                }
            }
        });
*/

        dialog.setPositiveButton("I Agree",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();

                    }
                });

        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();

                    }
                });


        alertDialog=dialog.create();
        alertDialog.show();
        //check_box();

    }

    void check_box(){
        if (agreed) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
    }

}
