package com.mienut.tst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Splash extends AppCompatActivity {

    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    String prod= "Producer";
    String cust ="Customer";
    boolean firststart=false;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        //firststart = prefs.getBoolean("firststart", true);


        handler.postDelayed(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                move();
                finish();

            }
        }, 2000);

    }

    private void move() {
        if(fAuth.getCurrentUser() != null) {

            userID = fAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        if (prod.equals(documentSnapshot.getString("type"))) {
                            startActivity(new Intent(getApplicationContext(), Producer.class));
                            finish();
                        } else if (cust.equals(documentSnapshot.getString("type"))) {
                            Intent intent = new Intent(Splash.this, Category.class);
                            intent.putExtra("type", cust);
                            startActivity(intent);
                            finish();
                        } else {

                            startActivity(new Intent(getApplicationContext(), Category.class));
                            finish();
                        }
                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }
        else{
            if(firststart){
                Intent menu = new Intent(getBaseContext(), Register.class);
                startActivity(menu);
            }
            else {
                Intent menu = new Intent(getBaseContext(), Login.class);
                startActivity(menu);
            }
        }
    }
}
